package main.java.utils;

import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs.treccar_v2.Data;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author poojaoza
 **/

class XMLHandler extends  DefaultHandler{

    private boolean num = false;
    private boolean url = false;
    private boolean entity_id = false;
    private boolean entity_mention = false;


    private Map<String, String> query_data = new LinkedHashMap();
    private Map<String, Map<String, String>> query_entity_list = new LinkedHashMap<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //super.startElement(uri, localName, qName, attributes);
        if(qName.equalsIgnoreCase("num")){
            num = true;
            System.out.println("found num");
        }else if(qName.equalsIgnoreCase("url")){
            url = true;
            System.out.println("found url");
        }else if(qName.equalsIgnoreCase("id")){
            entity_id = true;
            System.out.println("found entity id");
        }else if(qName.equalsIgnoreCase("mention")){
            entity_mention = true;
            System.out.println("found mention");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //
        if(qName.equalsIgnoreCase("top")){
            System.out.println("End element top");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //super.characters(ch, start, length);
        if(num){
            System.out.println("query num : "+new String(ch, start, length));
            String query_num = new String(ch, start, length).split(" ")[1];
            num = false;
        }else if(url){
            System.out.println("query url : "+new String(ch, start, length));
            String query_url = new String(ch, start, length);
            url = false;
        }else if(entity_id){
            System.out.println("entity id : "+new String(ch, start, length));
            entity_id = false;
        }else if(entity_mention){
            System.out.println("entity mention : "+new String(ch, start, length));
            entity_mention = false;
        }
    }
}
public class SearchUtils {

    static public Map<String, Map<String, String>> readEntity(String filename){
       Map<String, Map<String, String>> entity_data = new LinkedHashMap<>();

        //FileInputStream qrelStream = null;
        /*try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
        }
        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
        {
            data.put(page.getPageId(), page.getPageName());
        }*/
        try {
/*            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            SAXParser parser = saxFactory.newSAXParser();
            parser.parse(
                    new SequenceInputStream(
                            Collections.enumeration(Arrays.asList(
                                    new InputStream[]{
                                            new ByteArrayInputStream("<dummy>".getBytes()),
                                            new FileInputStream("/media/poojaoza/ExtraDrive1/Data/datasets/WashingtonPost.v2/trec_news_2019_test_topics/newsir19-entity-ranking-topics.xml"),//bogus xml
                                            new ByteArrayInputStream("</dummy>".getBytes()),
                                    }))
                    ),
                    new XMLHandler()
            );
            //XMLReader xmlReader =  parser.getXMLReader();
            //xmlReader.parse("/media/poojaoza/ExtraDrive1/Data/datasets/WashingtonPost.v2/trec_news_2019_test_topics/newsir19-entity-ranking-topics.xml");
            System.out.println("#############################################");*/

            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document=builder.parse(new SequenceInputStream(
                    Collections.enumeration(Arrays.asList(
                            new InputStream[]{
                                    new ByteArrayInputStream("<dummy>".getBytes()),
                                    new FileInputStream("/media/poojaoza/ExtraDrive1/Data/datasets/WashingtonPost.v2/trec_news_2019_test_topics/newsir19-entity-ranking-topics.xml"),//bogus xml
                                    //new FileInputStream("/media/poojaoza/ExtraDrive1/Data/datasets/WashingtonPost.v2/trec_news_2019_test_topics/newsir18-entities.xml"),//bogus xml
                                    new ByteArrayInputStream("</dummy>".getBytes()),
                            }))
            ));
            Element documentElement=document.getDocumentElement();
            NodeList sList=documentElement.getElementsByTagName("top");
            if (sList != null && sList.getLength() > 0)
            {
                for (int i = 0; i < sList.getLength(); i++)
                {
                    Node node = sList.item(i);
                    if(node.getNodeType()==Node.ELEMENT_NODE)
                    {
                        Element e = (Element) node;

                        NodeList nodeList = e.getElementsByTagName("num");

                        String query_num= nodeList.item(0).getChildNodes().item(0)
                                .getNodeValue().split(" ")[2];


                        /*nodeList = e.getElementsByTagName("url");
                        String url= nodeList.item(0).getChildNodes().item(0)
                                .getNodeValue();

                        query_data.put(query_num, url);*/

                        NodeList eList = e.getElementsByTagName("entity");

                        if(eList != null && eList.getLength() > 0){
                            Map<String, String> entities = new LinkedHashMap<>();
                            System.out.println(entities.size());
                            for (int j = 0; j < eList.getLength(); j++){
                                Node entity_node = eList.item(j);
                                if(entity_node.getNodeType() == Node.ELEMENT_NODE){
                                    Element element = (Element) entity_node;

                                    NodeList nodeList1 = element.getElementsByTagName("id");

                                    String entity_id = nodeList1.item(0).getChildNodes().item(0).getNodeValue();

                                    nodeList1 = element.getElementsByTagName("mention");
                                    String entity_mention = nodeList1.item(0).getChildNodes().item(0).getNodeValue();

                                    entities.put(entity_id, entity_mention);
                                }
                            }
                            entity_data.put(query_num, entities);
                        }
                    }
                }
            }
            System.out.println("*****************************");
            System.out.println(entity_data.size());
            System.out.println("*****************************");

        }catch(ParserConfigurationException pce){
            pce.printStackTrace();
        }catch (SAXException saxe){
            saxe.printStackTrace();
        }catch (FileNotFoundException fe){
            fe.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return entity_data;

    }

    static public Map<String, String> readOutline(String filename) {
        Map<String, String> query_data = new LinkedHashMap<String, String>();
        /*Map<String, Map<String, String>> entity_data = new LinkedHashMap<>();
        Map<String, Map<String, String>> final_data = new LinkedHashMap<>();*/

        //FileInputStream qrelStream = null;
        /*try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
        }
        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
        {
            data.put(page.getPageId(), page.getPageName());
        }*/
        try {
            /*SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            SAXParser parser = saxFactory.newSAXParser();
            parser.parse(
                    new SequenceInputStream(
                            Collections.enumeration(Arrays.asList(
                                    new InputStream[]{
                                            new ByteArrayInputStream("<dummy>".getBytes()),
                                            new FileInputStream("/media/poojaoza/ExtraDrive1/Data/datasets/WashingtonPost.v2/trec_news_2019_test_topics/newsir19-entity-ranking-topics.xml"),//bogus xml
                                            new ByteArrayInputStream("</dummy>".getBytes()),
                                    }))
                    ),
                    new XMLHandler()
            );
            //XMLReader xmlReader =  parser.getXMLReader();
            //xmlReader.parse("/media/poojaoza/ExtraDrive1/Data/datasets/WashingtonPost.v2/trec_news_2019_test_topics/newsir19-entity-ranking-topics.xml");
            System.out.println("#############################################");*/

            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document=builder.parse(new SequenceInputStream(
                    Collections.enumeration(Arrays.asList(
                            new InputStream[]{
                                    new ByteArrayInputStream("<dummy>".getBytes()),
                                    new FileInputStream("/media/poojaoza/ExtraDrive1/Data/datasets/WashingtonPost.v2/trec_news_2019_test_topics/newsir19-entity-ranking-topics.xml"),//bogus xml
                                    //new FileInputStream("/media/poojaoza/ExtraDrive1/Data/datasets/WashingtonPost.v2/trec_news_2019_test_topics/newsir18-entities.xml"),//bogus xml
                                    new ByteArrayInputStream("</dummy>".getBytes()),
                            }))
            ));
            Element documentElement=document.getDocumentElement();
            NodeList sList=documentElement.getElementsByTagName("top");
            if (sList != null && sList.getLength() > 0)
            {
                for (int i = 0; i < sList.getLength(); i++)
                {
                    Node node = sList.item(i);
                    if(node.getNodeType()==Node.ELEMENT_NODE)
                    {
                        Element e = (Element) node;

                        NodeList nodeList = e.getElementsByTagName("num");

                        String query_num= nodeList.item(0).getChildNodes().item(0)
                                .getNodeValue().split(" ")[2];


                        nodeList = e.getElementsByTagName("url");
                        String url= nodeList.item(0).getChildNodes().item(0)
                                .getNodeValue();

                        query_data.put(query_num, url);

                        /*NodeList eList = e.getElementsByTagName("entities");

                        if(eList != null && eList.getLength() > 0){
                            Map<String, String> entities = new LinkedHashMap<>();
                            for (int j = 0; j < eList.getLength(); j++){
                                Node entity_node = eList.item(j);
                                if(entity_node.getNodeType() == Node.ELEMENT_NODE){
                                    Element element = (Element) entity_node;

                                    NodeList nodeList1 = element.getElementsByTagName("id");

                                    String entity_id = nodeList1.item(0).getChildNodes().item(0).getNodeValue();

                                    nodeList1 = element.getElementsByTagName("mention");
                                    String entity_mention = nodeList1.item(0).getChildNodes().item(0).getNodeValue();

                                    entities.put(entity_id, entity_mention);
                                }
                            }
                            entity_data.put(query_num, entities);
                        }*/
                    }
                }
            }
            System.out.println("*****************************");
            System.out.println(query_data.size());
            System.out.println("*****************************");

        }catch(ParserConfigurationException pce){
            pce.printStackTrace();
        }catch (SAXException saxe){
            saxe.printStackTrace();
        }catch (FileNotFoundException fe){
            fe.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return query_data;
    }

    static public Map<String, String> readOutlinePara(String filename) {
        Map<String, String> data = new LinkedHashMap<String, String>();

        FileInputStream qrelStream = null;
        try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
        }
        for ( Data.Paragraph page : DeserializeData.iterableParagraphs(qrelStream))
        {
            data.put(page.getParaId(),page.getTextOnly());
        }
        return data;
    }

    @Deprecated
    static public Map<String, String> readSectionPathOld(String filename) {
        Map<String, String> data = new LinkedHashMap<String, String>();

        FileInputStream qrelStream = null;
        try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());

        }
        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
        {
            StringBuilder queryBuilder = new StringBuilder();

            for (List<Data.Section> sectionPath : page.flatSectionPaths())
            {
                queryBuilder.append(" ");
                queryBuilder.append(page.getPageName());
                for(Data.Section sec:sectionPath)
                {
                    queryBuilder.append(" ");
                    queryBuilder.append(sec.getHeading().replaceAll("[^\\w\\s]",""));
                }
                //queryBuilder.append(String.join(" ", Data.sectionPathHeadings(sectionPath)).replaceAll("[^\\w\\s]",""));
            }
            //System.out.println(queryBuilder.toString());
            data.put(page.getPageId(), queryBuilder.toString());
        }
        return data;
    }

    /*
    The method getMD5Hash is taken from geeksforgeeks.org
    https://www.geeksforgeeks.org/md5-hash-in-java/

    input parameters - the string for which we need to generate the hash
    output - hash value of the input string
     */
    static String getMD5Hash(String input){
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while(hashtext.length()<32){
                hashtext = "0"+hashtext;
            }
            return hashtext;
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static void parseJSON(JSONObject turns, Map<String, Map<String, String>> query_list){

        String article_id = turns.get("id").toString();
        System.out.println("article id : "+article_id);
        JSONArray queries = (JSONArray) turns.get("contents");
        for(int i = 0; i < queries.size(); i++){
            JSONObject paraObj = (JSONObject) queries.get(i);
            /*if(article_id.equals("5f8dadba-0279-11e2-8102-ebee9c66e190")){
                System.out.println("=========");
                System.out.println(queries);
                System.out.println(paraObj);
                System.out.println("=========");
            }*/
            if(paraObj != null) {
                if (paraObj.containsKey("subtype")) {
                    String paragraph_text = paraObj.get("content").toString() + " ";
                    String paragraph_id = getMD5Hash(paragraph_text);

                    Map<String, String> paragraph = new LinkedHashMap<>();
                    paragraph.put(paragraph_id, paragraph_text);
                    query_list.put(article_id, paragraph);

                }
            }
        }
    }

    public static Map<String, Map<String, String>> readJSONfile(String fname){
        Map<String, Map<String, String>> queries = new LinkedHashMap<>();
        JSONParser jsonParser = new JSONParser();

        try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {

            //Read JSON file
            String line = reader.readLine();

            while (line != null) {

                Object obj = jsonParser.parse(line);
                parseJSON((JSONObject)obj, queries);
                line = reader.readLine();
                /*if(queries.size() >= 5){
                    return queries;
                }*/
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ParseException po) {
            po.printStackTrace();
        }
        return queries;
    }

    /*
     Ack -- The below code is taken from the Professor Dietz Trema-UNH examples
     */
    public static Map<String,String> readOutlineSectionPath(String fname)
    {
        Map<String, String> data = new LinkedHashMap<String, String>();

        FileInputStream qrelStream = null;
        try {
            qrelStream = new FileInputStream(new File(fname));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
        }


        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream)) {
            data.put(page.getPageId(),page.getPageName());
            for (List<Data.Section> sectionPath : page.flatSectionPaths()) {
                final String queryId = Data.sectionPathId(page.getPageId(), sectionPath);
                String queryStr = buildSectionQueryStr(page, sectionPath);

                data.put(queryId,queryStr);

            }

        }
        return data;
    }
    /**
     * Function: createIndexSearcher
     * Desc: Creates an IndexSearcher (responsible for querying a Lucene index directory).
     * @param indexLoc: Location of a Lucene index directory.
     * @return IndexSearcher
     */
    public static IndexSearcher createIndexSearcher(String indexLoc) {
        Path indexPath = Paths.get(indexLoc);
        IndexSearcher searcher = null;
        try {
            FSDirectory indexDir = FSDirectory.open(indexPath);
            DirectoryReader reader = DirectoryReader.open(indexDir);
            searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searcher;
    }

    /**
     * Function: createStandardBooleanQuery
     * Desc: Creates a boolean query (a bunch of terms joined with OR clauses) given a query string.
     *       Note: this is just tokenized using the StandardAnalyzer (so no stemming!).
     *
     * @param queryString: Query string that will be tokenized into query terms.
     * @param termField: The document field that we will be searching against with our query terms.
     * @return
     */
    public static Query createStandardBooleanQuery(String queryString, String termField) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        ArrayList<String> tokens = createTokenList(queryString, new EnglishAnalyzer());

        for (String token : tokens) {
            Term term = new Term(termField, token);
            TermQuery termQuery = new TermQuery(term);
            builder.add(termQuery, BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }

    public static Query createStandardBooleanQuerywithBigrams(String queryString, String termField) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        ArrayList<String> tokens = createTokenList(queryString, new EnglishAnalyzer());

        for (int i = 0; i < tokens.size() - 1; i++) {
            Term term = new Term(termField, tokens.get(i) + tokens.get(i+1));
            TermQuery termQuery = new TermQuery(term);

            builder.add(termQuery, BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }

    /*
     Ack -- The below code is taken from the Professor Dietz Trema-UNH examples
     */
    public static String buildSectionQueryStr(Data.Page page, List<Data.Section> sectionPath) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(page.getPageName());
        for (Data.Section section: sectionPath) {
            queryStr.append(" ").append(section.getHeading());
        }
        return queryStr.toString();
    }

    /**
     * Function: createTokenList
     * Desc: Given a query string, chops it up into tokens and returns an array list of tokens.
     * @param queryString: String to be tokenized
     * @param analyzer: The analyzer responsible for parsing the string.
     * @return A list of tokens (Strings)
     */
    public static ArrayList<String> createTokenList(String queryString, Analyzer analyzer) {
        final ArrayList<String> tokens = new ArrayList<>();

        final StringReader stringReader = new StringReader(queryString);
        try {
            final TokenStream tokenStream = analyzer.tokenStream("text", stringReader);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokens.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
            }
            tokenStream.end();
            tokenStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokens;
    }


}
