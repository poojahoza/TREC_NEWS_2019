package main.java.commandparser;

/**
 * @author poojaoza
 **/
public class CommandValidator {

    public static class ValidateIndexCommands
    {
        RegisterCommands.CommandIndex indexParser=null;

        public ValidateIndexCommands(RegisterCommands.CommandIndex indexParser)
        {
            this.indexParser = indexParser;
        }

        private void CALLEXIT(int status)
        {
            System.exit(status);
        }

        public void ValidateIndex()
        {
            if(indexParser.getIndexPath()==null)
            {
                CALLEXIT(-1);
            }
        }


    }


    public static class ValidateSearchCommands
    {

        private RegisterCommands.CommandSearch searchParser = null;

        public ValidateSearchCommands(RegisterCommands.CommandSearch searchParser)
        {
            this.searchParser = searchParser;
        }


        private void CALLEXIT(int status)
        {
            System.exit(status);
        }


        public void ValidateRetrievalOptions()
        {
            if(!searchParser.isArticleEnabled() && !searchParser.isSectionEnabled())
            {
                System.out.println("Please use either article or section level option");
                CALLEXIT(-1);
            }
            else if (searchParser.isArticleEnabled() && searchParser.isSectionEnabled())
            {
                System.out.println("Please use either section level or article level option, not bot");
                CALLEXIT(-1);
            }
        }


        public void ValidateEcmExpansion(){
            if(searchParser.getIndexlocation()== null)
            {
                System.out.println("Please pass the paragraph index location path");
                CALLEXIT(-1);
            }
            if(searchParser.getEcmentityfile() == null)
            {
                System.out.println("Please pass the entity run file path");
                CALLEXIT(-1);
            }
        }

        public void ValidateEntityRelation()
        {
            if(searchParser.getEntityIndLoc() == null)
            {
                System.out.println("Please pass the entity index location path");
                CALLEXIT(-1);
            }
            if(searchParser.getQrelfile() == null)
            {
                System.out.println("Please pass the entity qrel location path");
                CALLEXIT(-1);
            }
            if(searchParser.getEcmentityfile() == null)
            {
                System.out.println("Please pass the entity run file path");
                CALLEXIT(-1);
            }

        }


    }


}
