package io.shike.vertx.couchdb;

import java.util.Base64;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

/**
 * @author Ranger Tsao(http://www.mowenxi.com)
 */
public class Startup extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(Startup.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Startup());
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        String basic = Base64.getEncoder().encodeToString("metagraph:metagraph".getBytes());
        logger.info("Basic is {0}", basic);
        WebClient client = WebClient.create(vertx);
        client.post(5984, "127.0.0.1", "/_replicate")
              .as(BodyCodec.jsonObject())
              .putHeader("Authorization", "Basic " + basic)
              .sendJsonObject(new JsonObject().put("source", "metagraph")
                                              .put("target", "test")
                                              .put("continuous", true)
                                              .put("create_target", true),
                              ar -> {
                                  if (ar.succeeded()) {
                                      logger.info(ar.result().body());
                                  } else {
                                      logger.error(ar.cause().getMessage());
                                  }
                              });
        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        stopFuture.complete();
    }
}
