package main.java.dbpedia;

/**
 * @author poojaoza
 **/

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DBpediaWebAPIClient {

    private final String API_URL = "http://api.dbpedia-spotlight.org/";
    private final double CONFIDENCE = 0.0;
    private final int SUPPORT = 0;

    private String getDBpediaEntities(String input_text){

        String dbpedia_response;
        String entities_list = "";

        try {

            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(API_URL + "en/annotate/?" +
                    "text=" + URLEncoder.encode(input_text, "utf-8"));
            getMethod.addRequestHeader(new Header("Accept", "application/json"));
            System.out.println(getMethod.getURI());

            httpClient.executeMethod(getMethod);

            dbpedia_response = getMethod.getResponseBodyAsString();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(dbpedia_response);
            System.out.println("***********************");

            if(jsonObject.containsKey("Resources")){

                JSONArray jsonArray = (JSONArray) jsonObject.get("Resources");
                for(int j = 0; j < jsonArray.size(); j++){
                    JSONObject resource = (JSONObject) jsonArray.get(j);

                    entities_list += (String)resource.get("@surfaceForm");
                    entities_list += "\n";
                    System.out.println(entities_list);
                }
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        return entities_list;
    }

    public String getEntities(String text){
        return getDBpediaEntities(text);
    }


}
