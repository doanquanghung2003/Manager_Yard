package bean;

import com.google.gson.JsonObject;

public interface Bean {
	public JsonObject toJsonObject();
	public void parse(JsonObject obj) throws Exception;
}
