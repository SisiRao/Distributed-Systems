
package edu.cmu.andrew.sisir;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the edu.cmu.andrew.sisir package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetHashValue_QNAME = new QName("http://sisir.andrew.cmu.edu/", "getHashValue");
    private final static QName _GetMenu_QNAME = new QName("http://sisir.andrew.cmu.edu/", "getMenu");
    private final static QName _AddBlock_QNAME = new QName("http://sisir.andrew.cmu.edu/", "addBlock");
    private final static QName _BlockChainToStringResponse_QNAME = new QName("http://sisir.andrew.cmu.edu/", "blockChainToStringResponse");
    private final static QName _IsChainVaildResponse_QNAME = new QName("http://sisir.andrew.cmu.edu/", "isChainVaildResponse");
    private final static QName _AddBlockResponse_QNAME = new QName("http://sisir.andrew.cmu.edu/", "addBlockResponse");
    private final static QName _GetHashValueResponse_QNAME = new QName("http://sisir.andrew.cmu.edu/", "getHashValueResponse");
    private final static QName _CompareBytes_QNAME = new QName("http://sisir.andrew.cmu.edu/", "compareBytes");
    private final static QName _BlockChainToString_QNAME = new QName("http://sisir.andrew.cmu.edu/", "blockChainToString");
    private final static QName _IsChainVaild_QNAME = new QName("http://sisir.andrew.cmu.edu/", "isChainVaild");
    private final static QName _GetMenuResponse_QNAME = new QName("http://sisir.andrew.cmu.edu/", "getMenuResponse");
    private final static QName _CompareBytesResponse_QNAME = new QName("http://sisir.andrew.cmu.edu/", "compareBytesResponse");
    private final static QName _NoSuchAlgorithmException_QNAME = new QName("http://sisir.andrew.cmu.edu/", "NoSuchAlgorithmException");
    private final static QName _UnsupportedEncodingException_QNAME = new QName("http://sisir.andrew.cmu.edu/", "UnsupportedEncodingException");
    private final static QName _CompareBytesArg1_QNAME = new QName("", "arg1");
    private final static QName _CompareBytesArg0_QNAME = new QName("", "arg0");
    private final static QName _GetHashValueResponseReturn_QNAME = new QName("", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.cmu.andrew.sisir
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMenuResponse }
     * 
     */
    public GetMenuResponse createGetMenuResponse() {
        return new GetMenuResponse();
    }

    /**
     * Create an instance of {@link IsChainVaild }
     * 
     */
    public IsChainVaild createIsChainVaild() {
        return new IsChainVaild();
    }

    /**
     * Create an instance of {@link BlockChainToString }
     * 
     */
    public BlockChainToString createBlockChainToString() {
        return new BlockChainToString();
    }

    /**
     * Create an instance of {@link UnsupportedEncodingException }
     * 
     */
    public UnsupportedEncodingException createUnsupportedEncodingException() {
        return new UnsupportedEncodingException();
    }

    /**
     * Create an instance of {@link NoSuchAlgorithmException }
     * 
     */
    public NoSuchAlgorithmException createNoSuchAlgorithmException() {
        return new NoSuchAlgorithmException();
    }

    /**
     * Create an instance of {@link CompareBytesResponse }
     * 
     */
    public CompareBytesResponse createCompareBytesResponse() {
        return new CompareBytesResponse();
    }

    /**
     * Create an instance of {@link BlockChainToStringResponse }
     * 
     */
    public BlockChainToStringResponse createBlockChainToStringResponse() {
        return new BlockChainToStringResponse();
    }

    /**
     * Create an instance of {@link IsChainVaildResponse }
     * 
     */
    public IsChainVaildResponse createIsChainVaildResponse() {
        return new IsChainVaildResponse();
    }

    /**
     * Create an instance of {@link AddBlock }
     * 
     */
    public AddBlock createAddBlock() {
        return new AddBlock();
    }

    /**
     * Create an instance of {@link GetHashValue }
     * 
     */
    public GetHashValue createGetHashValue() {
        return new GetHashValue();
    }

    /**
     * Create an instance of {@link GetMenu }
     * 
     */
    public GetMenu createGetMenu() {
        return new GetMenu();
    }

    /**
     * Create an instance of {@link CompareBytes }
     * 
     */
    public CompareBytes createCompareBytes() {
        return new CompareBytes();
    }

    /**
     * Create an instance of {@link GetHashValueResponse }
     * 
     */
    public GetHashValueResponse createGetHashValueResponse() {
        return new GetHashValueResponse();
    }

    /**
     * Create an instance of {@link AddBlockResponse }
     * 
     */
    public AddBlockResponse createAddBlockResponse() {
        return new AddBlockResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHashValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "getHashValue")
    public JAXBElement<GetHashValue> createGetHashValue(GetHashValue value) {
        return new JAXBElement<GetHashValue>(_GetHashValue_QNAME, GetHashValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMenu }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "getMenu")
    public JAXBElement<GetMenu> createGetMenu(GetMenu value) {
        return new JAXBElement<GetMenu>(_GetMenu_QNAME, GetMenu.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddBlock }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "addBlock")
    public JAXBElement<AddBlock> createAddBlock(AddBlock value) {
        return new JAXBElement<AddBlock>(_AddBlock_QNAME, AddBlock.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BlockChainToStringResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "blockChainToStringResponse")
    public JAXBElement<BlockChainToStringResponse> createBlockChainToStringResponse(BlockChainToStringResponse value) {
        return new JAXBElement<BlockChainToStringResponse>(_BlockChainToStringResponse_QNAME, BlockChainToStringResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsChainVaildResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "isChainVaildResponse")
    public JAXBElement<IsChainVaildResponse> createIsChainVaildResponse(IsChainVaildResponse value) {
        return new JAXBElement<IsChainVaildResponse>(_IsChainVaildResponse_QNAME, IsChainVaildResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddBlockResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "addBlockResponse")
    public JAXBElement<AddBlockResponse> createAddBlockResponse(AddBlockResponse value) {
        return new JAXBElement<AddBlockResponse>(_AddBlockResponse_QNAME, AddBlockResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHashValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "getHashValueResponse")
    public JAXBElement<GetHashValueResponse> createGetHashValueResponse(GetHashValueResponse value) {
        return new JAXBElement<GetHashValueResponse>(_GetHashValueResponse_QNAME, GetHashValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompareBytes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "compareBytes")
    public JAXBElement<CompareBytes> createCompareBytes(CompareBytes value) {
        return new JAXBElement<CompareBytes>(_CompareBytes_QNAME, CompareBytes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BlockChainToString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "blockChainToString")
    public JAXBElement<BlockChainToString> createBlockChainToString(BlockChainToString value) {
        return new JAXBElement<BlockChainToString>(_BlockChainToString_QNAME, BlockChainToString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsChainVaild }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "isChainVaild")
    public JAXBElement<IsChainVaild> createIsChainVaild(IsChainVaild value) {
        return new JAXBElement<IsChainVaild>(_IsChainVaild_QNAME, IsChainVaild.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMenuResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "getMenuResponse")
    public JAXBElement<GetMenuResponse> createGetMenuResponse(GetMenuResponse value) {
        return new JAXBElement<GetMenuResponse>(_GetMenuResponse_QNAME, GetMenuResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompareBytesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "compareBytesResponse")
    public JAXBElement<CompareBytesResponse> createCompareBytesResponse(CompareBytesResponse value) {
        return new JAXBElement<CompareBytesResponse>(_CompareBytesResponse_QNAME, CompareBytesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NoSuchAlgorithmException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "NoSuchAlgorithmException")
    public JAXBElement<NoSuchAlgorithmException> createNoSuchAlgorithmException(NoSuchAlgorithmException value) {
        return new JAXBElement<NoSuchAlgorithmException>(_NoSuchAlgorithmException_QNAME, NoSuchAlgorithmException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnsupportedEncodingException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sisir.andrew.cmu.edu/", name = "UnsupportedEncodingException")
    public JAXBElement<UnsupportedEncodingException> createUnsupportedEncodingException(UnsupportedEncodingException value) {
        return new JAXBElement<UnsupportedEncodingException>(_UnsupportedEncodingException_QNAME, UnsupportedEncodingException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg1", scope = CompareBytes.class)
    public JAXBElement<byte[]> createCompareBytesArg1(byte[] value) {
        return new JAXBElement<byte[]>(_CompareBytesArg1_QNAME, byte[].class, CompareBytes.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = CompareBytes.class)
    public JAXBElement<byte[]> createCompareBytesArg0(byte[] value) {
        return new JAXBElement<byte[]>(_CompareBytesArg0_QNAME, byte[].class, CompareBytes.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = GetHashValueResponse.class)
    public JAXBElement<byte[]> createGetHashValueResponseReturn(byte[] value) {
        return new JAXBElement<byte[]>(_GetHashValueResponseReturn_QNAME, byte[].class, GetHashValueResponse.class, ((byte[]) value));
    }

}
