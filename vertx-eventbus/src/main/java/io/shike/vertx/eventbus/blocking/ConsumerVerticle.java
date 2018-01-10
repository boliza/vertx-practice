package io.shike.vertx.eventbus.blocking;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.impl.MessageProducerImpl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ConsumerVerticle extends AbstractVerticle {

  static String eb;
  private Logger logger = LoggerFactory.getLogger(ConsumerVerticle.class);

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), cv -> cv.result().deployVerticle(new ConsumerVerticle()));
  }

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("eb", (Handler<Message<Long>>) event -> {
      logger.info("Consumer message:" + event.body());
      if (eb == null) {
        eb = event.headers().get(MessageProducerImpl.CREDIT_ADDRESS_HEADER_NAME);
      }
      vertx.executeBlocking(fut -> vertx.setTimer(10, tid -> fut.complete()),
                            ret -> {
                              event.reply("OK-" + event.body());
                              vertx.eventBus().send(eb, event.body().intValue());
                            });
    });
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
