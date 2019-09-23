package main.java.indexer;


import edu.unh.cs.TrecCarParagraph;
import edu.unh.cs.lucene.TrecCarLuceneConfig;
import edu.unh.cs.treccar_v2.Data;
import main.java.utils.IndexUtils;
import main.java.utils.SearchUtils;
import main.java.dbpedia.DBpediaWebAPIClient;
import org.apache.commons.httpclient.HttpClient;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.Map;


public class IndexBuilder
{
    private final IndexWriter indexWriter;
    private static int increment=0;
    private DBpediaWebAPIClient dBpediaWebAPIClient;

    public IndexBuilder(String indexDir) throws IOException {
        indexWriter = IndexUtils.createIndexWriter(indexDir);
        dBpediaWebAPIClient = new DBpediaWebAPIClient();
    }


    private void writePara(Data.Paragraph p,
                           IndexWriter i,
                           TrecCarLuceneConfig.LuceneIndexConfig cfg,
                           TrecCarParagraph trecCarParaRepr){
        final Document doc = trecCarParaRepr.paragraphToLuceneDoc(p);
        try {
            int incrementFactor=10000;
            indexWriter.addDocument(doc);
            increment++;

            //commit the Data after incrementFactorVariable paragraph

            if(increment % incrementFactor ==0)
            {
                indexWriter.commit();
            }
        }catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Create an index of the cbor file passed as a parameter.
     */

    public void performIndex(String cborLoc) throws IOException
    {
/*        Iterable<Data.Paragraph> para = IndexUtils.createParagraphIterator(cborLoc);
        TrecCarLuceneConfig.LuceneIndexConfig cfg = TrecCarLuceneConfig.paragraphConfig();
        TrecCarParagraph tcpr = cfg.getTrecCarParaRepr();
        StreamSupport.stream(para.spliterator(), true)
                .forEach(paragraph ->
                {
                    writePara(paragraph,indexWriter, cfg, tcpr);

                });*/
        Map<String, Map<String, String>> input_data = SearchUtils.readJSONfile(cborLoc);
        //HttpClient httpClient = new HttpClient();
        int total_paras = 0;
        for(Map.Entry<String, Map<String, String>> article: input_data.entrySet()){
            for(Map.Entry<String, String> paragraph: article.getValue().entrySet()){
                total_paras += 1;
            }
        }
        System.out.println("Total paragraphs : "+total_paras);
        /*for(Map.Entry<String, Map<String, String>> article: input_data.entrySet()){
            for(Map.Entry<String, String> paragraph: article.getValue().entrySet()){
                try {
                    int incrementFactor=10000;
                    Document doc = new Document();

                    doc.add(new StringField("ArticleId",article.getKey() , Field.Store.YES));
                    doc.add(new StringField("ParaId",paragraph.getKey() , Field.Store.YES));
                    doc.add(new TextField("Text", paragraph.getValue(), Field.Store.YES));

                    String entities = dBpediaWebAPIClient.getEntities(paragraph.getValue(), httpClient);
                    doc.add(new StringField("EntityIds",entities , Field.Store.YES));
                    indexWriter.addDocument(doc);
                    increment++;
                    System.out.println("article id in indexbuilder: "+article.getKey()+" "+Integer.toString(increment));

                    //commit the Data after incrementFactorVariable paragraph

                    if(increment % incrementFactor ==0)
                    {
                        indexWriter.commit();
                    }
                }catch(IOException ioe) {
                    System.out.println(ioe.getMessage());
                }

            }

        }*/
        closeIndexWriter();
    }

    /**
     * Closes the indexwriter so that we can use it in searching.
     * @throws IOException
     */
    private void closeIndexWriter()
    {
        if (indexWriter != null)
        {
            try
            {
                indexWriter.close();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

        }
    }

}