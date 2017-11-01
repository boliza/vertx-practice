package io.shike.vertx.eventbus;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Startup {

  public static List<String> addresses = Arrays.asList("eb1", "eb2");

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Future future1 = Future.future();
    Future future2 = Future.future();
    vertx.deployVerticle(new ConsumerVerticle(), future1);
    vertx.deployVerticle(new ProducerVerticle(), future2);

    CompositeFuture.all(future1, future2).setHandler(event -> {
      if (event.succeeded()) {
        System.out.println("Startup consumer and producer verticles");
      } else {
        event.cause().printStackTrace();
      }
    });
  }

}
