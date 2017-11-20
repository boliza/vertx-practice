package io.shike.vertx.eventbus;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Startup {

  public static List<String> addresses = Arrays.asList("eb1", "eb2");

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Future<String> future1 = Future.future();
    Future<String> future2 = Future.future();
    future1.compose((String event) -> vertx.deployVerticle(new ConsumerVerticle()), future2)
           .setHandler(f2 -> System.out.println("Start two verticles"));
    vertx.deployVerticle(new ProducerVerticle(), future1);
  }

}
