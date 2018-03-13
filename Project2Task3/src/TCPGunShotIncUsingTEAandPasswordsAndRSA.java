
/**
 *
 * @author sisi
 */
import java.net.*;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.io.*;
import java.math.BigInteger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TCPGunShotIncUsingTEAandPasswordsAndRSA {
    //pair S = (d,n) as the RSA secret key.

    public static RSAPrivateKey privateKey = null;

    /*Establish connection with client*/
    public static void main(String args[]) {
        try {
            getPriKey();
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("Waiting for installers to visit…");

            while (true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }

        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }

    private static void getPriKey() {
        try {
            FileInputStream fis = new FileInputStream("GunshotSensorKeys.jks");
            try {

                //get private key from keystore
                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load(fis, "sesame".toCharArray());
                fis.close();
                Certificate cert = keyStore.getCertificate("GunshotSensorKeys");
                RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
                Key key = keyStore.getKey("GunshotSensorKeys", 
                                          "sesame".toCharArray());
                RSAPrivateKey priKey = (RSAPrivateKey) key;
                TCPGunShotIncUsingTEAandPasswordsAndRSA.privateKey = priKey;
            } catch (NoSuchAlgorithmException | CertificateException | 
                     IOException | KeyStoreException | 
                     UnrecoverableKeyException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("Target file does not exists.");
        }
    }
}


/*Validate client information and handle client request*/
class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    static int countClient = 0;
    static TreeMap<String, String[]> sensorInfo = new TreeMap<String, String[]>(); //key:sensor id, value: positions
    static TreeMap<String, String> userInfo = new TreeMap<String, String>();//key:user id, value:user title
    static TreeMap<String, String[]> loginInfo = new TreeMap<String, String[]>();//key:user id, value:{salt,hash of salt+pwd}
    static TEA tea = null;

    public Connection(Socket aClientSocket) {
        try {
            //init
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {
            //initiate user data
            countClient++;
            userInfo.put("Moe", "Chief Sensor Installer");
            userInfo.put("Larry", "Associate Sensor Installer");
            userInfo.put("Shemp", "Associate Sensor Installer");
            loginInfo.put("Moe", new String[]{"a56c1b6a", 
                                              "B30C2C3B9CE469682865C41A39B05E6567256CABBAADBED97549FB603D2FACB4"});
            loginInfo.put("Larry", new String[]{"e9252861", 
                                                "BB49E2C910812638A8A08B338AE33C7D6365B1685982F21B969EE9388DEC5326"});
            loginInfo.put("Shemp", new String[]{"571e5c1e", 
                                                "DB9ADDF0740F450FFE0AAAD1766F58173836183F2988641DC95CFD8E257BB6F1"});

            String jsonText = checkClientKey();
            if (jsonText == null) {
                return;
            }

            String result = process(jsonText);//result[0]:feedback string, result[1]:status code

            byte[] encryptedFeedback = tea.encrypt(result.getBytes());
            out.write(encryptedFeedback);
            in.close();

            //out.writeUTF(data);
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {/*close failed*/
            }
        }

    }

    /*Check if client side entered the correct symmetric key
	 * 1. Instantiate TEA with server symmetric key
	 * 2. Decrypt client encrypted data with TEA
	 * 3. Check if these two matches. Report attack if failed.*/
    private String checkClientKey() throws IOException {
        byte[] data = new byte[1024];
        int count = in.read(data);
        if (count < 0) {
            return null;
        }
        int i = 0;
        byte[] keyArray = new byte[count];
        while (i < count) {
            keyArray[i] = data[i];
            i++;
        }
        // using RSA decrypt
        BigInteger enc_key = new BigInteger(keyArray);
        BigInteger key = enc_key.modPow(TCPGunShotIncUsingTEAandPasswordsAndRSA.privateKey.getPrivateExponent(),
                                        TCPGunShotIncUsingTEAandPasswordsAndRSA.privateKey.getModulus());

        Connection.tea = new TEA(key.toByteArray());

        //read in encrypted data from client
        data = new byte[1024];
        count = in.read(data);
        if (count < 0) {
            return null;
        }
        byte[] crypt = new byte[count];
        i = 0;
        while (i < count) {
            crypt[i] = data[i];
            i++;
        }

        //validate symmetric key
        byte[] result = tea.decrypt(crypt);
        String s = new String(result);
        for (char c : s.toCharArray()) {
            if (c > 128) {
                System.out.println("Got visit " + countClient + ",illegal symmetric key used. This may be an attack.");
                return null;
            }
        }
        return s;
    }

    /*convert hex String to Byte Array*/
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private String process(String jsonText) {
        //check client information
        JSONParser parser = new JSONParser();
        JSONObject user;
        try {
            //get info

            user = (JSONObject) parser.parse(jsonText);
            String userid = (String) user.get("ID");
            String passWord = (String) user.get("passwd");
            String sensorid = (String) user.get("Sensor ID");
            String lat = (String) user.get("Latitude");
            String lon = (String) user.get("Longitude");

            String feedback;

            //check password
            String hex = null;
            if (loginInfo.containsKey(userid)) {

                Integer number = Integer.parseUnsignedInt(loginInfo.get(userid)[0], 16);
                byte[] salt = ByteBuffer.allocate(4).putInt(number).array();
                byte[] pwd = passWord.getBytes();
                byte[] combined = new byte[salt.length + pwd.length];
                for (int i = 0; i < combined.length; ++i) {
                    combined[i] = i < salt.length ? salt[i] : pwd[i - salt.length];
                }

                //hash salt + password
                byte[] hash = null;
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    hash = digest.digest(combined);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                hex = javax.xml.bind.DatatypeConverter.printHexBinary(hash);

            }
            //if no such user in database, or invalid password.
            if (!loginInfo.containsKey(userid) || !loginInfo.get(userid)[1].equals(hex)) {
                feedback = "Illegal ID or Password";
                System.out.println("Got visit " + countClient + " from " + 
                                    userid + ", " + userInfo.get(userid) + 
                                    " Illegal Password attempt. This may be an attack.");
            } else {
                //if this is a new sensor
                if (!sensorInfo.containsKey(sensorid)) {
                    sensorInfo.put(sensorid, new String[]{lat, lon, userid});
                    System.out.println("Got visit " + countClient + " from " + 
                                        userid + ", " + userInfo.get(userid));
                    writeToFile();
                    feedback = "Thank you. The sensor’s location was securely "
                            + "transmitted to GunshotSensing\n" + "Inc.";

                } //if this is an update to existing sensor
                else if (userInfo.get(userid).equals("Chief Sensor Installer")) {
                    sensorInfo.put(sensorid, new String[]{lat, lon, userid});
                    System.out.println("Got visit " + countClient + " from " + 
                                        userid + ", " + userInfo.get(userid) + 
                                        ", a sensor has been moved.");
                    writeToFile();
                    feedback = "Thank you. The sensor’s location was securely "
                            + "transmitted to GunshotSensing\n"
                            + "Inc.";
                } //if this is an update from unauthorized user
                else {
                    System.out.println("Got visit " + countClient + " from " +
                                        userid + ", " + userInfo.get(userid)
                                        + ", an unauthorized move. This may be an attack.");
                    feedback = "Unauthorized move.";

                }
            }

            return feedback;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void writeToFile() {
        try {
            //write kml file to desktop
            String deskTopLocation = System.getProperty("user.home")
                    + "/Desktop/Sensors.kml";
            FileWriter writer = new FileWriter(deskTopLocation);
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String[]> entry : sensorInfo.entrySet()) {
                String[] sensor = entry.getValue();
                sb.append("<Placemark>\n"
                        + " <name>" + entry.getKey() + "</name>\n"
                        + " <description>" + sensor[2] + "</description>\n"
                        + " <styleUrl>#style1</styleUrl>\n"
                        + " <Point>\n"
                        + " <coordinates>" + sensor[0] + "," + sensor[1] + ",0.00</coordinates>\n"
                        + " </Point>\n"
                        + "</Placemark>\n");
            }

            String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                    + "<kml xmlns=\"http://earth.google.com/kml/2.2\">\n"
                    + "<Document>\n"
                    + " <Style id=\"style1\">\n"
                    + " <IconStyle>\n"
                    + " <Icon>\n"
                    + " <href>https://lh3.googleusercontent.com/MSOuW3ZjC7uflJAMstcykSOEOwI_cVz96s2rtWTN4-Vu1NOBw80pTqrTe06R_AMfxS2=w170</href>\n"
                    + " </Icon>\n"
                    + " </IconStyle>\n"
                    + " </Style>\n"
                    + sb.toString()
                    + "</Document>\n"
                    + "</kml>";

            writer.write(content);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
