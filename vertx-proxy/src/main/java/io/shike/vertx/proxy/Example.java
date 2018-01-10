package io.shike.vertx.proxy;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Example {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    WebClient client = WebClient.create(vertx);
    client.post(80, "www.ahgyss.cn", "/ktggInfo.jspx")
          .putHeader("Content-Type", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
          .putHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
          .setQueryParam("fyid", "1451")
          .setQueryParam("bh", "4D5EEEB6AD0E1377DCA05C771D161209")
          .send(event -> {
            int index = event.result().body().toString().indexOf("\"hiddenTxt");
            System.out.println(index);
            System.out.println(event.result().body().toString().replaceAll("\\p{Cc}", ""));
          });
  }

}
