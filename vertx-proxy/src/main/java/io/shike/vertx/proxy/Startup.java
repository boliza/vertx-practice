package io.shike.vertx.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * @author Ranger Tsao(http://www.mowenxi.com)
 */
public class Startup extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    vertx.deployVerticle(Server.class.getName());
    vertx.deployVerticle(Proxy.class.getName());
    startFuture.complete();
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    stopFuture.complete();
  }
}
