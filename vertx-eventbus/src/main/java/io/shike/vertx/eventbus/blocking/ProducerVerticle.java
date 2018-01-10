package io.shike.vertx.eventbus.blocking;

import java.util.concurrent.atomic.AtomicLong;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ProducerVerticle extends AbstractVerticle {

  private Logger logger = LoggerFactory.getLogger(ProducerVerticle.class);

  private AtomicLong al = new AtomicLong(1L);

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions(), cv -> cv.result().deployVerticle(new ProducerVerticle()));
  }

  @Override
  public void start() throws Exception {
    MessageProducer<Long> producer = vertx.eventBus().sender("eb");
//    vertx.setPeriodic(1L, p -> {
//      producer.send(al.getAndIncrement(), event -> System.out.println(event.result().body()));
//      if (al.get() > 2000) {
//        vertx.cancelTimer(p);
//      }
//    });
    while (al.get() <= 2000) {
      producer.send(al.getAndIncrement(), event -> System.out.println(event.result().body()));
    }
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
