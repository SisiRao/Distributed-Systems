
package project3task2client;

import edu.cmu.andrew.sisir.NoSuchAlgorithmException_Exception;
import edu.cmu.andrew.sisir.UnsupportedEncodingException_Exception;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is a JAX-WS service client. The client class will pass a single message
 * (containing a single String holding an XML message) to a single JAX-WS service.
 * 
 * @author sisi
 */
public class BlockChainClient {

    private static BigInteger n; // n is the modulus for both the private and public keys
    private static BigInteger e; // e is the exponent of the public key
    private static BigInteger d; // d is the exponent of the private key

    /**
    * This method computes a hash of a given String with SHA-256.
    * @param data a string to be hashed.
    * 
    * @return a byte[] that contains the hash.
    */
    private static byte[] getHash(String data)throws UnsupportedEncodingException{
        byte[] bytesOfMessage = data.getBytes("UTF-8");
         //SHA-256 hash
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-256");
            bytesOfMessage = sha.digest(bytesOfMessage);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlockChainClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytesOfMessage;
    }

    /**
    * This method encrypt the byte[] hash with user's RSA private key.
    * @param byte[] hash.
    * 
    * @return a string of encrypted message.
    */
    private static String rsaEncrypt(byte[] hash) throws UnsupportedEncodingException{
        // since RSA doesn't support negative input    
        // add an additional 0 in the front to make the hash -> biginteger always positive
        byte[] newHash = hash;
        
        if (new BigInteger(hash).compareTo(BigInteger.ZERO) < 0) {
            newHash = new byte[33];
            newHash[0] = 0;
            for (int i = 1; i < newHash.length; i++) {
            newHash[i] = hash[i - 1];
            }
        }
        
        BigInteger m = new BigInteger(newHash);
        BigInteger c = m.modPow(d, n);
        return c.toString();
    }
    
    /**
    * This method generate XML String.
    * @param op the integer value of user option
    * @param sig RSA encrypted signature
    * @param d transaction data
    * @param diff difficulty level
    * 
    * @return a XML String
    */
    public static String generateXMLString(int op,String sig,String d,int dif){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            // root elements
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement("request");
            doc.appendChild(root);
            
            // request infomation elements
            Element option      = doc.createElement("option");
            Element data        = doc.createElement("data");
            Element difference  = doc.createElement("difficulty");
            Element signature   = doc.createElement("signature");
            
            option.appendChild(doc.createTextNode(op+""));
            data.appendChild(doc.createTextNode(d));
            difference.appendChild(doc.createTextNode(dif+""));
            signature.appendChild(doc.createTextNode(sig));

            root.appendChild(option);
            root.appendChild(data);
            root.appendChild(difference);
            root.appendChild(signature);
            
            // write xml doc to a string
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            
            return sw.toString();
 
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BlockChainClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(BlockChainClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(BlockChainClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
        
    }

    public BlockChainClient() {
        e = new BigInteger("65537");
        d = new BigInteger("2240803607502707148730017397980550250867481678846273139684080974922339716789125989284711894142272127841164483917407336869506505649109902075534761057770733053328679417345368283990723110936991027777170984163662131514801591073330615273886883773");
        n = new BigInteger("4247448908890959317608652211466415670024646327882811359522071290050191699760201011128045217827970743089700557700509750876626560603783580929182683689452492570145824987928477257377769951051980034364980498172790647011947325123387814133108946633");
    }

    /**
    * This method provides the BlockChain menu to the user and send request in 
    * XML String to the BlockChain Server.
    * 
    * @param args
    */
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException_Exception, UnsupportedEncodingException_Exception{
        BlockChainClient client = new BlockChainClient();
        long start, end;
        int option = 0;
        String result, feedback;
        loop:
        while (true) {
            System.out.println("1. Add a transaction to the blockchain.\n"
                    + "\n"
                    + "2. Verify the blockchain.\n"
                    + "\n"
                    + "3. View the blockchain.\n"
                    + "\n"
                    + "4. Exit.");
            Scanner sc = new Scanner(System.in);
            option = Integer.parseInt(sc.nextLine());
            switch (option) {
                case 1: {
                    System.out.println("Enter difficulty");
                    int difficulty = Integer.parseInt(sc.nextLine());
                    System.out.println("Enter transaction");
                    String data = sc.nextLine();
                    byte[] hash = getHash(data);
                    String signature = rsaEncrypt(hash);
//                    System.out.println("signature: "+signature);
                    //Add transaction to the blockchain
                    start = System.currentTimeMillis();
                    result = generateXMLString(option,signature,data,difficulty);
                    feedback = getMenu(result);
                    if (feedback.equals("false")) {
                        System.out.println("Key Verification failed.");
                        break;
                    }
                    end = System.currentTimeMillis();
                    System.out.printf("Total execution time to add this block %d milliseconds \n", (end - start));
                    break;
                }

                case 2: {
                    System.out.println("Verifying");
                    start = System.currentTimeMillis();
                    result = generateXMLString(option,"","",0);
                    feedback = getMenu(result);
                    if (feedback.equals("true")) {
                        System.out.println("Chain verification: true");
                    } else {
                        System.out.println("Chain verification: false");
                    }
                    end = System.currentTimeMillis();
                    System.out.printf("Total execution time to verify the chain is %d milliseconds \n", (end - start));
                    break;
                }
                
                case 3: {
                    System.out.println("View the Blockchain");
                    result = generateXMLString(option,"","",0);
                    feedback = getMenu(result);
                    System.out.println(feedback);
                    break;
                }
                case 4: {
                    break loop;
                }

            }

        }
    }

    /**
     * pass user request to server and get feedback
     * @param request
     * @return
     * @throws NoSuchAlgorithmException_Exception
     * @throws UnsupportedEncodingException_Exception 
     */
    private static String getMenu(java.lang.String request) throws NoSuchAlgorithmException_Exception, UnsupportedEncodingException_Exception {
        edu.cmu.andrew.sisir.BlockChain service = new edu.cmu.andrew.sisir.BlockChain();
        edu.cmu.andrew.sisir.BlockChainServer port = service.getBlockChainServerPort();
        return port.getMenu(request);
    }
    
    
}
