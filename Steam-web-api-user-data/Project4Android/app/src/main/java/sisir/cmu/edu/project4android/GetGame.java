package sisir.cmu.edu.project4android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sisi on 3/25/18.
 */

public class GetGame {
    MostPlayedGame mpg = null;

    /*
	 * search is the public GetGame method.  Its arguments are the steamID, and the MostPlayedGame object that called it.  This provides a callback
	 * path such that the gameReady method in that object is called when the game information is available from the search.
	 */
    public void search(String steamID, MostPlayedGame mpg) {
        this.mpg = mpg;
        new AsyncSteamSearch().execute(steamID);
    }

    /*
	 * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
	 * doInBackground is run in the helper thread.
	 * onPostExecute is run in the UI thread, allowing for safe UI updates.
	 */
    private class AsyncSteamSearch extends AsyncTask<String, Void, Game> {
        protected Game doInBackground(String... ids) {
            return search(ids[0]);
        }

        /**
         * 		invoked on the UI thread after the background computation finishes.
         * 		The result of the background computation is passed to this step as a parameter.
         */
        protected void onPostExecute(Game game) {
            if(game.gamecount.equals("0")) {
                mpg.gameReady(null, game.gamecount, null, null, null);
            } else {
                mpg.gameReady(game.img, game.gamecount, game.appid, game.name, game.playtime);
            }
        }

        /*
         * Search Steam Web API for the steam id argument, and return a Game object that contains all the information
         * of a game.
         */
        private Game search(String steamID) {
            //Use following URL to check Task 1
//            String urlString = "https://fathomless-beach-88572.herokuapp.com/SteamUserGameServlet";
            //Comment the above URL and uncomment the below URL to check Task 2
            String urlString = "https://vast-wave-97630.herokuapp.com/SteamUserGameServlet";
            // url to test in local
//            String urlString = "http://10.0.2.2:8080/Project4Task2/SteamUserGameServlet";

            HttpURLConnection conn;
            StringBuffer sb = new StringBuffer();
            try {
                urlString += "?steamID="+steamID;
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "text/plain");

                // things went well so let's read the response
                String output = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //process the received json string
            Game favoriteGame = null;
            JSONParser parser = new JSONParser();
            JSONObject obj;
            try {
//                Log.d("sb",sb.toString());
                obj = (JSONObject) parser.parse(sb.toString());
                long game_count = (long) obj.get("game_count");

                if (game_count != 0) {
                    long appid = (long) obj.get("appid");
                    String name = (String) obj.get("name");
                    String imgurl = (String) obj.get("imgurl");
//                    Log.d("imgurl",imgurl);
                    long playtime = (long) obj.get("playtime");

                    if (imgurl.equals("")) {
                        imgurl = imgurl.replace("\\","");
                        favoriteGame = new Game(""+game_count, ""+appid, name, ""+playtime+"mins", null);
                    } else {
                        // load the image with image url
                        Bitmap picture;
                        try {
                            URL u = new URL(imgurl);
                            picture = getRemoteImage(u);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null; // so compiler does not complain
                        }

                        favoriteGame = new Game(""+game_count, ""+appid, name, ""+playtime+"mins",picture);
                    }

                }
                // if no such user or user has no game
                else {
                    favoriteGame = new Game(""+game_count, null, null, null, null);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            Log.d("favoriteGame", favoriteGame.toString());
            return favoriteGame;

        }

        /*
         * Given a URL referring to an image, return a bitmap of that image
         */
        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


    }


}

/**
 * The class to hold received game data
 * */
class Game {

    String gamecount;
    String appid;
    String name;
    String playtime; // in minutes
    Bitmap img;

    public Game(String gc, String appid, String name, String playtime, Bitmap img) {
        this.gamecount = gc;
        this.appid = appid;
        this.name = name;
        this.playtime = playtime;
        this.img = img;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gamecount='" + gamecount + '\'' +
                ", appid='" + appid + '\'' +
                ", name='" + name + '\'' +
                ", playtime='" + playtime + '\'' +
                ", img=" + img +
                '}';
    }
}
