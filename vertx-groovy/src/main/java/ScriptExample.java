import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

import java.io.FileNotFoundException;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import io.vertx.core.json.JsonObject;

/**
 * @author Ranger Tsao(https://github.com/boliza)
 */
public class ScriptExample {

  public static void main(String[] args) throws FileNotFoundException, ScriptException {
    ScriptEngine engine = new GroovyScriptEngineImpl();
    Bindings bindings = new SimpleBindings();
    bindings.put("rule", new JsonObject().put("x", "x"));
    JsonObject object = (JsonObject) engine.eval("import io.vertx.core.json.JsonObject\n" +
                                                   "\n" +
                                                   "def parse(JsonObject rule) {\n" +
                                                   "  def json = new JsonObject().put(\"k1\", \"v1\").put(\"k2\", \"v2\")\n" +
                                                   "  println(json)\n" +
                                                   "  return json.put(\"rule\", rule)\n" +
                                                   "}\n" +
                                                   "parse(rule)", bindings);
    System.out.println(object);
  }

}
