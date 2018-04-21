package project4task2;

/**
 * This class will store the information retrieved from Mongo DB database.
 * @author sisi
 */


public class Entry {
        private final String timestamp;
        private final String mobileType;
        private final String browser;
        private final String steamID;
        private final String gamecount;
        private final String gamename;
        private final String url;

        
        public Entry(String timestamp, String mobileType, String browser, String steamID, String gamecount, String gamename, String u) {
            this.timestamp = timestamp;
            this.mobileType = mobileType;
            this.browser = browser;
            this.steamID = steamID;
            this.gamecount = gamecount;
            this.gamename = gamename;
            this.url = u;
        }
        
        // getters 
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public String getMobileType() {
            return mobileType;
        }

        public String getBrowser() {
            return browser;
        }

        public String getSteamID() {
            return steamID;
        }

        public String getGamecount() {
            return gamecount;
        }

        public String getGamename() {
            return gamename;
        }
        
        public String getUrl() {
            return url;
        }
    }