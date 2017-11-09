import io.vertx.core.Vertx;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Startup {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle("groovy/GroovyVerticle.groovy");
  }
}
