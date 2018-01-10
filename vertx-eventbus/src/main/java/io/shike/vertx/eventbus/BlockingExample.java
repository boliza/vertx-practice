package io.shike.vertx.eventbus;


import java.util.Collections;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class BlockingExample {

  private Vertx vertx;
  private String test;

  public BlockingExample(Vertx vertx) {
    this.vertx = vertx;
  }

  public static void main(String[] args) {
//    Vertx vertx = Vertx.vertx();
//    new BlockingExample(vertx).test(test -> {
//      if (test.succeeded()) {
//        System.out.println(test.result());
//      } else {
//        System.out.println("Oh");
//        test.cause().printStackTrace();
//      }
//    });
    Collections.singleton(null).forEach(e -> {
      System.out.println(e);
    });
  }

  public void test(Handler<AsyncResult<String>> handler) {
    vertx.executeBlocking(fut -> {
      try {
        String ret = test.substring(10);
        fut.complete(ret);
      } catch (ArrayIndexOutOfBoundsException e) {
        fut.fail(e);
      }
    }, handler);
  }

}
