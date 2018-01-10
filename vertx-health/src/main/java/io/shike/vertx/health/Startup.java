package io.shike.vertx.health;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Startup extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Startup());
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    HealthCheckHandler hch = HealthCheckHandler.create(vertx);
    vertx.setPeriodic(1001L, p -> vertx.eventBus().send("receiver", System.currentTimeMillis()));
    vertx.eventBus().consumer("receiver", event -> {
      if (event.body().hashCode() % 3 == 0) {
        hch.register("receiver", event1 -> event1.complete(Status.KO(new JsonObject().put("load", 99))));
      } else {
        hch.register("receiver", event1 -> event1.complete(Status.OK(new JsonObject().put("yeah", 99))));
      }
    });
    Router router = Router.router(vertx);
    router.get("/health").handler(hch);
    HttpServer server = vertx.createHttpServer().requestHandler(router::accept).listen(8080);
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    super.stop(stopFuture);
  }
}
