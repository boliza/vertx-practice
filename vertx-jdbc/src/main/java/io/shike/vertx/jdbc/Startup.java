package io.shike.vertx.jdbc;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Startup extends AbstractVerticle {

  private Logger logger = LoggerFactory.getLogger(Startup.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(Startup.class.getName());
  }

  @Override
  public void start() throws Exception {
    JsonObject jdbcConfig = new JsonObject().put("driver_class", "com.mysql.jdbc.Driver")
                                            .put("url", "jdbc:mysql://localhost:3306/shike?useUnicode=true&characterEncoding=UTF-8&useSSL=false")
                                            .put("user", "root").put("password", "root");
    JDBCClient jdbcClient = JDBCClient.createShared(vertx, jdbcConfig);

    jdbcClient.getConnection(open -> {
      if (open.succeeded()) {
        SQLConnection connection = open.result();
        connection.execute("CREATE TABLE IF NOT EXISTS test (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(20), age INT DEFAULT 0)",
                           create -> {
                             if (create.succeeded()) {
                               logger.info("create table test");

                               //generator data
                               List<JsonArray> params = new ArrayList<>();
                               for (int i = 0; i < 100; i++) {
                                 params.add(new JsonArray().add("name-" + i).add(i));
                               }
                               connection.batchWithParams("insert into shike.test (name, age) VALUES (?,?)",
                                                          params,
                                                          insert -> {
                                                            if (insert.succeeded()) {
                                                              logger.info("insert success, write data {0}", insert.result().size());
                                                              connection.queryStream("select * from test", query -> {
                                                                if (query.succeeded()) {
                                                                  query.result().handler(row -> logger.info(row));
                                                                  query.result().endHandler(end -> connection.execute("truncate table shike.test", truncate -> vertx.close()));
                                                                }
                                                              });
                                                            } else {
                                                              logger.error(insert.cause());
                                                              vertx.close();
                                                            }
                                                          });
                             } else {
                               logger.error(create.cause());
                               vertx.close();
                             }
                           });
      } else {
        logger.error(open.cause());
        vertx.close();
      }
    });
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
