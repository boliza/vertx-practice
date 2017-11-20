package io.shike.vertx.async;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class FutureExamples {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Future<Buffer> f1 = Future.future();
    Future<Message<JsonObject>> f2 = Future.future();
    Future<Message<JsonObject>> f3 = Future.future();
    f1.setHandler(h1 -> {
      if (h1.succeeded()) {
        System.out.println(h1.result().toJsonObject());
        vertx.eventBus().send("x.json", h1.result().toJsonObject(), new DeliveryOptions(), f2.completer());
      }
    });
    f2.setHandler(h2 -> System.out.println(h2.result().body()));
    f3.setHandler(h3 -> h3.result().reply(h3.result().body().put("y", "y")));
    vertx.fileSystem().readFile("x.json", f1.completer());
    vertx.eventBus().consumer("x.json", (Handler<Message<JsonObject>>) event -> event.reply(event.body().put("y", "y")));
  }

}
