package main.java.orchestration;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.CommandValidator;
import main.java.indexer.IndexBuilder;

import java.io.IOException;

/**
 * @author poojaoza
 **/
public class IndexOrchestrator implements ProgramOrchestrator {

    private RegisterCommands.CommandIndex indexParser = null;
    private CommandValidator.ValidateIndexCommands validate = null;

    public IndexOrchestrator(CommandParser parser){
        indexParser = parser.getIndexCommand();
        validate = new CommandValidator.ValidateIndexCommands(indexParser);
    }

    @Override
    public void run()
    {

        if(indexParser.getIsParaIndex())
        {
            validate.ValidateIndex();

            String destFinalPath= indexParser.getDestpath()+"_paragraph";
            IndexBuilder ib = null;
            try {
                ib = new IndexBuilder(destFinalPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ib.performIndex(indexParser.getIndexPath());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        if(indexParser.getIsEntity())
        {
            validate.ValidateIndex();

            String destFinalPath= indexParser.getDestpath()+"_entity";
        }

        if(indexParser.getIsEntityAbstract()){
            validate.ValidateIndex();
        }
    }

}
