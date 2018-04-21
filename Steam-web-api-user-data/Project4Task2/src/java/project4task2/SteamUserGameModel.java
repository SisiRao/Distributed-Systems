package project4task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author sisi
 */
public class SteamUserGameModel {

    public String[] doSteamSearch(String steamID) {
        String urlString = "http://api.steampowered.com/"
                + "IPlayerService/GetOwnedGames/v0001/?"
                + "key=18C3271FAB442E0163DFDC03C70083A4&"
                + "steamid="+steamID+"&format=json&include_appinfo=1";
                
        // Fetch the most played game
        String result = fetch(urlString);
        String[] results = new String[]{result, urlString};
        return results;
    }
    
    public String fetch(String urlString)
    {
        
        ArrayList<Game> usergames = new ArrayList<>();
        StringBuilder jsonString = new StringBuilder();
        Game favorateGame = null;
        long game_count = 0;
        
        // send request to Steam API
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which 
             * may be different than the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                jsonString.append(str);
                
            }
            in.close();
            JSONParser parser = new JSONParser();
            JSONObject result;
            
            
            try {
                // parse json string received from Steam restful API
                result = (JSONObject) parser.parse(jsonString.toString());
                JSONObject response = (JSONObject)result.get("response");
                game_count = (long) response.get("game_count");
                if(game_count == 0)
                    return buildJsonString(null, 0);
                
                JSONArray games = (JSONArray) response.get("games");
                
                for(int i = 0; i<games.size(); i++)
                {
                    JSONObject game = (JSONObject) games.get(i);
                    long appid = (long) game.get("appid");
                    String name = (String)game.get("name");
                    long playtime = (long)game.get("playtime_forever");
                    String logourl = (String)game.get("img_logo_url");
                    String imgurl = "http://media.steampowered.com/steamcommunity/public/images/apps/"+appid+"/"+logourl+".jpg\n";
                    Game newgame = new Game(appid,name,playtime,imgurl);
                    usergames.add(newgame);
                    if(favorateGame == null || favorateGame.playtime<playtime)
                        favorateGame = newgame;

                }


            }catch (ParseException e) {
                e.printStackTrace();
            }
                    }
        catch (IOException e) {
            return buildJsonString(null, 0);
        }
        
        return buildJsonString(favorateGame, game_count);
    }
    
    /**
     * This class is to build json String and send back to the servlet 
     * 
     * @param game The information of this game object will be included in the json object.
     * @param game_count the total number of game a user owns. Free games are excluded.
     */
     public String buildJsonString(Game game, long game_count){
        JSONObject obj = new JSONObject();
        if (game == null) {
            obj.put("game_count",game_count);
        } else {
            obj.put("game_count",game_count);
            obj.put("appid",game.appid);
            obj.put("name", game.name);
            obj.put("imgurl", game.imgurl);
            obj.put("playtime", game.playtime);
        }

        StringWriter sw = new StringWriter();
        try {
            obj.writeJSONString(sw);
        } catch (IOException e) {
            e.printStackTrace();
        }                    
        return sw.toString();
     }

}


/**
 * This class stores the statistics related to a game in Steam Platform.
 */
class Game{
   
    long appid;
    String name;
    long playtime; // in minutes
    String imgurl;
    
    public Game(long appid, String name, long playtime,String imgurl) {
        this.appid = appid;
        this.name = name;
        this.playtime = playtime;
        this.imgurl = imgurl;
    }
    
    @Override
    public String toString() {
        return "Game [appid=" + appid + ", name=" + name + ", playtime=" + playtime/60 + "h, imgurl="
                        + imgurl + "]";
    }
    
    
}

