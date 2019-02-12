package michu4k.kontomatikchallenge.utils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class JsonUtils {
    public static String writeJsonToString(JsonObject jsonObject) {
        Writer writer = new StringWriter();
        Json.createWriter(writer).write(jsonObject);
        return writer.toString();
    }

    public static JsonArray parseResponseToJsonArray(String response, String wantedArray) {
        JsonReader reader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = reader.readObject();
        return jsonObject.getJsonArray(wantedArray);
    }
}