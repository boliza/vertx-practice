package io.shike.vertx.jdbc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class MongoExample extends AbstractVerticle {

  public static void main(String[] args) throws InterruptedException {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MongoExample());
  }

  @Override
  public void start() throws Exception {
    JsonObject config = new JsonObject("{\n" +
                                         "  \"db_name\": \"fuyun\",\n" +
                                         "  \"hosts\": [\n" +
                                         "    {\n" +
                                         "      \"host\": \"192.168.0.11\",\n" +
                                         "      \"port\": 27701\n" +
                                         "    },\n" +
                                         "    {\n" +
                                         "      \"host\": \"192.168.0.12\",\n" +
                                         "      \"port\": 27701\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}\n");
    System.out.println(config.encodePrettily());
    MongoClient client = MongoClient.createShared(vertx, config.put("useObjectId", true));
    System.out.println(client);
    client.findOne("extract_rule", new JsonObject().put("seedId", "5a2a5671432d647177d70ce6").put("biz", "url"), new JsonObject(), event -> {
      if (event.result() == null) {
        System.out.println("no rule");
      } else {
        System.out.println(event.result().encodePrettily());
        System.out.println(event.result().getString("_id"));
      }
    });
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
