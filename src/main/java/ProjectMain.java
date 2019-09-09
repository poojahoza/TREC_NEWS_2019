package main.java;

import main.java.commandparser.CommandParser;
import main.java.orchestration.*;

/**
 * @author poojaoza
 **/
public class ProjectMain {
    public static void main(String[] args)
    {
        /*
          Parses the command line and creates the parser
        */
        CommandParser parser = new CommandParser(args);
        ProgramOrchestrator orchestrator = null;
        if(args.length < 1)
        {
            parser.getParser().usage();
        }
        else {
            if (parser.getParser().getParsedCommand().equals("index")) {
                orchestrator = new IndexOrchestrator(parser);
                orchestrator.run();

            } else if (parser.getParser().getParsedCommand().equals("search")) {
            } else if (parser.getParser().getParsedCommand().equals("ranker")) {
            } else {
                parser.getParser().usage();
            }
        }
    }

}
