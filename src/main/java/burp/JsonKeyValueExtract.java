package burp;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class JsonKeyValueExtract {
    public static String startString = null;
    public static String stopString = null;
    public static void main(String[] args) {
        //String jsonString = "{\"Accounts\": [{ \"Name\" : \"Savings acc\", \"id_1\": \"800002\" }, { \"Name\" : \"Checking acc\", \"id1\": \"800002\" }, { \"Name\" : \"Credit Card aacc\", \"id\": \"4539082039396288\" }]}";
        //String targetValue = "800002";
        //String targetValue = "800003";
    	String jsonString = "{\"username\":\"demo1234\",\"password\":\"demo1234\"}";
    	String targetValue = "demo1234";
    	Map <String, Object> jsonMap = new LinkedHashMap<>();
    	String[] result = findPath(new JSONObject(jsonString), targetValue);
        System.out.println(result[0] + ", JSON: " + result[1]);
        System.out.println("startString ="+ startString + "stopString " + stopString);
    }

    public static String[] findPath(JSONObject jsonObject, String targetValue) {
    	List<String> path = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            //System.out.println("jsonObject  ="+jsonObject);
            if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.length(); i++) {
                    String[] innerResult = findPath(array.getJSONObject(i), targetValue);
                    if (innerResult != null) {
                        //System.out.println("innerResult ="+List.of(innerResult));
                        path.add(key);
                        //System.out.println("innerResult  ="+List.of(innerResult));
                        path.addAll(List.of(innerResult));
                        //System.out.println("path ="+path);
                        return new String[] {String.join("-->", path), array.getJSONObject(i).toString()};
                    }
                }
            } else if (value instanceof JSONObject) {
                String[] innerResult = findPath((JSONObject) value, targetValue);
                System.out.println("value ="+value);
                if (innerResult != null) {
                	//System.out.println("else if 1 key added ="+key);
                    path.add(key);
                    path.addAll(List.of(innerResult));
                    //System.out.println("path1  ="+path);
                    return new String[] {String.join("-->", path), value.toString()};
                }
            } else if (value.equals(targetValue)) {
            	System.out.println("key else if key added ="+jsonObject);
                path.add(key);
                //  "password": "demo1234"
                int startStringIndex = jsonObject.toString().indexOf(targetValue)-5;
                startString = jsonObject.toString().substring(startStringIndex, jsonObject.toString().indexOf(targetValue));
                System.out.println("start string index = "+ startStringIndex + " start string " + startString);
                int stopIndex = jsonObject.toString().indexOf(targetValue)+targetValue.length();
                stopString = jsonObject.toString().substring(stopIndex, stopIndex+1);
                //System.out.println("stopString string = "+ stopIndex + stopString);
                return new String[] {String.join("-->", path), jsonObject.toString()};
            }
        }
        return null;
    }
    
	public static String getStartString() {
		return startString;
	}
	public static String getStopString() {
		return stopString;
	}
}
