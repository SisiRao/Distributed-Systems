
package edu.cmu.andrew.sisir;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** XMLHelper.java Class process the XML string the client passes in and return 
 *  as a BlockChainRequest. 
 *
 * @author sisi
 */
class XMLHelper {
    
    public XMLHelper(){
        
    }
    
    /** 
     * Extract data in a XML String.
     * 
     * @param xmlStr XML String
     * @return BlockChainRequest Object
    */
    public BlockChainRequest process(String request) {
        
        int option = 0;
        String data ="";
        String signature="";
        int difficulty=0;
        
        Document doc = convertStringToDocument(request);
        doc.getDocumentElement().normalize();
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	System.out.println("----------------------------");
        NodeList nList = doc.getElementsByTagName("request");
        Node reqnode= nList.item(0);
        System.out.println("----------------------------");

        if (reqnode.getNodeType() == Node.ELEMENT_NODE) {

            Element info = (Element) reqnode;
            option = Integer.parseInt(info.getElementsByTagName("option").item(0).getTextContent());
            data = info.getElementsByTagName("data").item(0).getTextContent();
            signature = info.getElementsByTagName("signature").item(0).getTextContent();
            difficulty = Integer.parseInt(info.getElementsByTagName("difficulty").item(0).getTextContent());


            System.out.println("option : " + option);
            System.out.println("data : " + data);
            System.out.println("difficulty : " + difficulty);
            System.out.println("signature : " + signature);
	}
        
        BlockChainRequest r = new BlockChainRequest(option, data, difficulty, signature);

        return r;
    }

    
    /** 
     * Convert XML String to Document object
     * 
     * @param xmlStr XML String
     * @return Document Object
    */
    private Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
    
    
    
    
}
