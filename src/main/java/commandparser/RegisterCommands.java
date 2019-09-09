package main.java.commandparser;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author poojaoza
 **/
public class RegisterCommands {
    public enum qeType
    {
        entityText , entityID , entityTextID,entityIDInEntityField;
    }
    /*
     All the commands as part of the index should be registered here
     */
    @Parameters(separators = "=",commandDescription = "Command to Index the Corpus")
    public static class CommandIndex
    {

        @Parameter(names = {"-i","--corpus-file"},description = "Corpus file to index. In case of Entity Abstract, please specify Entity Index location",required=true)
        private String IndexPath;

        @Parameter(names = {"-d","--dest-location"},description = "Location to save the index file")
        private String destpath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"--para-index"}, description = "Perform Paragraph Index")
        private Boolean isParaIndex = false;

        @Parameter(names = {"--entity-index"}, description = "Perform Entity Index")
        private Boolean isEntity = false;

        @Parameter(names = {"--abstract-index"}, description = "Perform Entity Abstract Index")
        private Boolean isEntityAbstract = false;


        @Parameter(names = "--help", help = true)
        private boolean help;



        boolean isHelp()
        {
            return help;
        }

        public String getIndexPath()
        {
            return IndexPath;
        }

        public String getDestpath()
        {
            return destpath;
        }

        public Boolean getIsParaIndex() {return isParaIndex;}

        public Boolean getIsEntity() {return isEntity;}

        public Boolean getIsEntityAbstract() {return isEntityAbstract;}

    }

     /*
     All the commands as part of the search should be registered here
     The required parameter is set to true because its mandatory

     Example command for your search implementation.
     --rerank one of the method implementation requires word embeddings file, whatever data you need, you can accept it.

     There are some helper functions that needs to be implemented to validate the inputs for your method, if the user missed any
     data, the program should exit with a status message

     */

    @Parameters(separators = "=",commandDescription = "Command to search")
    public static class CommandSearch
    {
        @Parameter(names = {"-i", "--index-loc"}, description = "Indexed directory to search", required = true)
        private String indexlocation = null;

        @Parameter(names = {"-q", "--query-cbor"}, description = "Query file (CBOR file)", required = true)
        private String queryfile = null;

        @Parameter(names = {"-qrel", "--entity-qrel"}, description = "Entity qrel file")
        private String qrelfile = null;

        @Parameter(names = "--help", help = true)
        private boolean help;
        @Parameter(names = {"-entity-run", "--entity-run"}, description = "Entity run file")
        private String ecmentityfile = null;

        @Parameter(names = {"-k","--candidate-set-val"}, description = "How many candidate set to retrieve using BM25")
        private Integer kVAL=100;

        @Parameter(names = "--entity-index",description ="Pass the index location of entity index")
        private String entityIndLoc = null;

        @Parameter(names = {"-bm25","--default-bm25"},description ="Rerank the initial retrieved cluster using document similarity")
        private boolean isBM25 =false;

        @Parameter(names = "--entity-relation",description ="Generate the feature vectors and ranklib model")
        private boolean isEntityRelationEnabled =false;


        @Parameter(names = "article",description ="Article level retrieval")
        private boolean isArticleEnabled =false;

        @Parameter(names = "section",description ="Section level retrieval")
        private boolean isSectionEnabled =false;


        @Parameter(names = "--test", description = "Only for testing purposes")
        private boolean isTestEnabled = false;

        public boolean isTestEnabled() {
            return isTestEnabled;
        }

        public boolean isArticleEnabled()
        {
            return isArticleEnabled;
        }
        public boolean isSectionEnabled()
        {
            return isSectionEnabled;
        }

        public String getIndexlocation() {
            return indexlocation;
        }

        public String getQueryfile() {
            return queryfile;
        }

        public String getQrelfile() {
            return qrelfile;
        }


        public String getEcmentityfile()
        {
            return ecmentityfile;
        }

        public Integer getkVAL()
        {
            return kVAL;
        }

        public boolean isBM25Enabled()
        {
            return isBM25;
        }


        public boolean isEntityRelationEnabled()
        {
            return isEntityRelationEnabled;
        }


        boolean isHelp() {
            return help;
        }

        public String getEntityIndLoc() {
            return entityIndLoc;
        }

    }


    @Parameters(separators = "=", commandDescription = "Help Information")
    public static class CommandHelp {

    }

    @Parameters(separators = "=", commandDescription = "Ranker")
    public static class Ranker {
        @Parameter(names = {"--model-file"}, description = "Location of the model file", required = true)
        private String modelFile = null;

        @Parameter(names = {"--run-file"}, description = "Location of the run file", required = true)
        private String runfile = null;

        @Parameter(names = {"--mname"}, description = "Method name suffix")
        private String mname = "mrfupdated";

        public String getModelFile() {
            return modelFile;
        }

        public String getRunfile() {
            return runfile;
        }

        public String getMname() {
            return mname;
        }

    }

}
