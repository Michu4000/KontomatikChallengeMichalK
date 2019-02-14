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

    public static JsonArray parseStringToJsonArray(String jsonString, String wantedArray) {
        JsonObject jsonObject = parseStringToJson(jsonString);
        return jsonObject.getJsonArray(wantedArray);
    }

    public static JsonObject parseStringToJson(String jsonString) {
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        return reader.readObject();
    }
}