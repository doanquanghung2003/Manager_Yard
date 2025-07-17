package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bean.Bean;

public class JsonUtils {
	public static JsonArray toJsonArray(List<? extends Bean> beans) {
		JsonArray output = new JsonArray();
		
		if(beans != null) for(Bean bean : beans){
			output.add(bean.toJsonObject());
		}
		
		return output;
	}

	public static <T extends Bean> List<T> toBeans(JsonArray beans, Class<T> cls) throws Exception {
	    List<T> listBeans = new ArrayList<>();

	    for (JsonElement obj : beans) {
	        T bean = cls.getDeclaredConstructor().newInstance();
	        bean.parse(obj.getAsJsonObject());
	        listBeans.add(bean);
	    }

	    return listBeans;
	}

	
	
}
