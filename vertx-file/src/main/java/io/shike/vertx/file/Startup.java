package io.shike.vertx.file;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.parsetools.RecordParser;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class Startup {

  private static Logger logger = LoggerFactory.getLogger(Startup.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    FileSystem fs = vertx.fileSystem();
    RecordParser parser = RecordParser.newDelimited(System.getProperty("line.separator", "\n"),
                                                    buffer -> logger.info("name of this line is: {0}", buffer.toString()));
    fs.open("names.txt", new OpenOptions(), ar -> {
      if (ar.succeeded()) {
        AsyncFile file = ar.result();
        file.handler(parser);
        file.endHandler(h -> {
          parser.handle(Buffer.buffer(System.getProperty("line.separator", "\n")));// add this to read the last buffer
          vertx.close();
        });
      } else {
        logger.error("can't find file");
      }
    });
  }

}
