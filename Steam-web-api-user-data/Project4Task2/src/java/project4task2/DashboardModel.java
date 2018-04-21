package project4task2;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;

import com.mongodb.client.MongoCursor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author sisi
 */
public class DashboardModel {

    public void logInfo(String ua, String curTime, String steamID, String result,String url) {
        
        //connect to mongo db
        MongoClientURI connectionString = new MongoClientURI("mongodb://lychnis:admin@ds123399.mlab.com:23399/ds-project4");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("ds-project4");
        MongoCollection<Document> collection = database.getCollection("UserSteamGame");
        
        // prepare the document to log into database
        Document document = new Document("mobileType", ua)
                            .append("timestamp", curTime)
                            .append("steamID", steamID)
                            .append("result", result)
                            .append("url", url);
        
        //write document to db
	collection.insertOne(document);
    }
 

    public Entry[] getInfo() {
        //connect to mongo db
        MongoClientURI connectionString = new MongoClientURI("mongodb://lychnis:admin@ds123399.mlab.com:23399/ds-project4");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("ds-project4");
        MongoCollection<Document> collection = database.getCollection("UserSteamGame");
        
        //get all documents from db
        MongoCursor<Document> cursor = collection.find().iterator();
        LinkedList<String> data = new LinkedList();
        try {
            while (cursor.hasNext()) {
                data.add(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
        
        return process(data);
    }
    
    private Entry[] process (LinkedList<String> data){
        
        Entry[] entries = new Entry[data.size()];
        JSONParser parser = new JSONParser();
        JSONObject obj;
        JSONObject subobj;
        int i = 0;
        // process data retrieved from database
        for(String s : data) {

            String timestamp;
            String mobileType;
            String browser;
            String steamID;
            String gamecount;
            String gamename = "";
            String url;
            
            try {
                // retrieve log
                obj = (JSONObject) parser.parse(s);
                String ua = String.valueOf(obj.get("mobileType"));
                timestamp = String.valueOf(obj.get("timestamp"));
                steamID = String.valueOf(obj.get("steamID"));
                url = String.valueOf(obj.get("url"));
                String response = String.valueOf(obj.get("result"));
                
                // extract data from response
                subobj = (JSONObject) parser.parse(response);
                gamecount = String.valueOf((long) subobj.get("game_count"));
                
                if (!gamecount.equals("0")) {
                    gamename = (String) subobj.get("name");
                }
                
                // extract data from HTTP header user agent
                if (ua.contains("Android")) {
                    mobileType = "Android";
                } else if (ua.contains("iPhone")){
                    mobileType = "iPhone";
                } else {
                    mobileType = "other";
                }
                
                if (ua.contains("Firefox")) {
                    browser = "Firefox";
                } else if (ua.contains("OPR") || ua.contains("Opera")) {
                    browser = "Opera";
                } else if (ua.contains("Safari") && !ua.contains("Chrome")) {
                    browser = "Safari";
                } else if (ua.contains("Chrome") && !ua.contains("Chromium")) {
                    browser = "Chrome";
                } else {
                    browser = "other";
                }
                
                // add new entry into entries
                entries[i++] = new Entry(timestamp, mobileType, browser, steamID, gamecount, gamename, url);
                
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } // end for loop

         return entries;   
    }

    public String getTopGameCount(Entry[] entries) {
        // find the max number of games a person owns.
        int max = 0;
        for(Entry e : entries) {
            if (Integer.parseInt(e.getGamecount()) > max) {
                max = Integer.parseInt(e.getGamecount());
            }
        }
        
        return String.valueOf(max);
    }

    public String getMostMobileDevice(Entry[] entries) {
        // find the most popular type of mobile devices
        int iphone = 0;
        int andriod = 0;
        int other = 0;
        
        for(Entry e : entries) {
            if (e.getMobileType().equals("Android")) {
                    andriod++;
                } else if (e.getMobileType().equals("iPhone")){
                    iphone++;
                } else {
                    other++;
                }
        }
        if (iphone > andriod && iphone > other) {
            return "iPhone";
        } else if (andriod > iphone && andriod > other) {
            return "Android";
        } else {
            return "Other";
        }
    }

    public String getMostPlayedGame(Entry[] entries) {
        // get the name of game that was played for the longest time by most of people
        Map<String, Integer> map = new HashMap<>();
         for(Entry e : entries) {
             if (map.containsKey(e.getGamename())) {
                 map.put(e.getGamename(), map.get(e.getGamename())+1);
             } else {
                  map.put(e.getGamename(), 1);
             }
         }
         
         Iterator itr = map.entrySet().iterator();
         String name = "";
         int max = 0;
         while (itr.hasNext()) {
            Map.Entry pair =  (Map.Entry) itr.next();
            if (max < (int) pair.getValue()) {
                name = (String) pair.getKey();
            }
         }
         
         return name;
    }
    
    
    
}
