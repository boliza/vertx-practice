package io.shike.vertx.proxy;

import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ESBulk extends AbstractVerticle {

  private static final byte[] bytes = "\n".getBytes();

  static Long timerId;
  private Logger logger = LoggerFactory.getLogger(ESBulk.class);
  private WebClient client;
  private int batchSize = 10;
  private int currentSize = 1;
  private Buffer buffer = Buffer.buffer();


  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    AtomicInteger i = new AtomicInteger(100);

    vertx.deployVerticle(ESBulk.class, new DeploymentOptions().setInstances(3), event -> {
      vertx.setPeriodic(100, l -> {
        timerId = l;
        vertx.eventBus().send("eb", new JsonObject().put("id", String.valueOf(i.get())).put("content", "my value is " + i.getAndIncrement()));
      });
      vertx.setTimer(9190, l -> vertx.cancelTimer(timerId));
      vertx.setTimer(20000, l -> vertx.close());
    });
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    client = WebClient.create(vertx, new WebClientOptions().setDefaultHost("192.168.0.7").setDefaultPort(9200));
    MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("eb");
    consumer.handler(msg -> {
      buffer.appendBuffer(new JsonObject().put("index", new JsonObject().put("_index", "bulk").put("_type", "bulk").put("_id", msg.body().getString("id"))).toBuffer());
      buffer.appendBytes(bytes);
      buffer.appendBuffer(msg.body().toBuffer());
      buffer.appendBytes(bytes);
      if (currentSize++ >= batchSize) {
        consumer.pause();
        bulk(() -> {
          currentSize = 1;
          buffer = Buffer.buffer();
          consumer.resume();
        });
      }
    });
    super.start(startFuture);
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    if (currentSize > 1) {
      bulk(stopFuture::complete);
    } else {
      stopFuture.complete();
    }
  }

  private void bulk(Action action) {
    client.post("/_bulk")
          .putHeader("Content-Type", "application/json")
          .as(BodyCodec.jsonObject())
          .sendBuffer(buffer, bulk -> {
            if (bulk.failed()) {
              logger.error("Failed bulk documents", bulk.cause());
            } else {
              if (bulk.result().body().containsKey("error")) {
                logger.error("Failed bulk documents", bulk.result().body());
              } else {
                logger.info("Succeeded index documents" + buffer.toString());
              }
            }
            action.perform();
          });
  }

  @FunctionalInterface
  public interface Action {
    void perform();
  }

}
