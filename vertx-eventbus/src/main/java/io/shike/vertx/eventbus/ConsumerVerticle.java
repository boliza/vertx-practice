package io.shike.vertx.eventbus;

import io.vertx.core.AbstractVerticle;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ConsumerVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
//    for (String address : Startup.addresses) {
//      vertx.eventBus().consumer(address, event -> System.out.println(event.address() + " consumer message: " + event.body()));
//    }
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
