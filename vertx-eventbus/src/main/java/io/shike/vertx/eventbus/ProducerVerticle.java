package io.shike.vertx.eventbus;

import io.vertx.core.AbstractVerticle;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ProducerVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    for (int i = 0; i < 100; i++) {
      vertx.eventBus().send(Startup.addresses.get(i % 2), "message-" + i);
    }
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
