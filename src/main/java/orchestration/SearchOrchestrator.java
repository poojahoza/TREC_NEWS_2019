package main.java.orchestration;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.CommandValidator;
import main.java.utils.SearchUtils;
import main.java.searcher.BaseBM25;
import main.java.containers.Container;
import main.java.utils.*;
import main.java.entityrelation.FeatureGenerator;

import java.io.IOException;
import java.util.Map;


/**
 * @author poojaoza
 **/
public class SearchOrchestrator  implements ProgramOrchestrator{

    private RegisterCommands.CommandSearch searchParser = null;
    private CommandValidator.ValidateSearchCommands validate = null;

    public SearchOrchestrator(CommandParser parser)
    {
        searchParser = parser.getSearchCommand();
        validate = new CommandValidator.ValidateSearchCommands(searchParser);
    }

    @Override
    public void run()  {

        Map<String,String> queryCBOR = null;

        validate.ValidateRetrievalOptions();
        if(searchParser.isArticleEnabled())
        {
            queryCBOR = SearchUtils.readOutline(searchParser.getQueryfile());
        }else
        {
            queryCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());
        }

        if(searchParser.isBM25Enabled())
        {
            BaseBM25 bm = null;
            try {
                bm = new BaseBM25(searchParser.getkVAL(),searchParser.getIndexlocation());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String,Map<String, Container>> res = bm.getRanking(queryCBOR);

            String datafile ="";
            if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
            {
                datafile = "_test";
            }
            else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
            {
                datafile = "_train";
            }

            String level = searchParser.isArticleEnabled()? "_article": "_section";
            String mname= "BM_25"+level+datafile;

            RunWriter.writeRunFile(mname,res);
        }

        if(searchParser.isEntityRelationEnabled()){
            validate.ValidateEntityRelation();

            try {
                //Map<String,String> querysecCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());
                //Map<String,String> querysecCBOR = SearchUtils.readJSONfile("/media/poojaoza/ExtraDrive1/Data/datasets/treccastweb/2019/data/training/train_topics_annotated_v1.0.json");
                //Map<String,String> querysecCBOR = SearchUtils.readJSONfile("/media/poojaoza/ExtraDrive1/Data/datasets/treccastweb/2019/data/evaluation/evaluation_topics_annotated_v1.0.json");


                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);

                Entities e = new Entities();
                Map<String, Map<String, Integer>> query_ent_list = e.getSortedEntitiesPerQuery(bm25_ranking);
                Map<String, Map<String, Double[]>> entity_ranking_list = e.readEntityRunFileDetails(searchParser.getEcmentityfile());

                FeatureGenerator featuregenerator = new FeatureGenerator();
                Map<String, Map<String, Double[]>> featureVectors = featuregenerator.getFeatureVectors(query_ent_list, bm25_ranking, entity_ranking_list);
                //Map<String, Map<String, Double[]>> featureVectors = featuregenerator.getNormalizedFeatureVectors(query_ent_list, bm25_ranking, entity_ranking_list);
                Map<String, Map<String, Double>> hopRelationfeatureVectors = featuregenerator.extractFeatures(featureVectors, 0);
                Map<String, Map<String, Double>> relComentionfeatureVectors = featuregenerator.extractFeatures(featureVectors, 1);
                Map<String, Map<String, Double>> comentionfeatureVectors = featuregenerator.extractFeatures(featureVectors, 2);
                Map<String, Map<String, Double>> cocouplingcountfeatureVectors = featuregenerator.extractFeatures(featureVectors, 3);
                Map<String, Map<String, Double>> cocouplingrelfeatureVectors = featuregenerator.extractFeatures(featureVectors, 4);
                Map<String, Map<String, Double>> biblorelcouplingfeatureVectors = featuregenerator.extractFeatures(featureVectors, 5);
                Map<String, Map<String, Double>> biblocountcouplingfeatureVectors = featuregenerator.extractFeatures(featureVectors, 6);
                Map<String, Map<String, Double>> outlinksDirectlinksfeatureVectors = featuregenerator.extractFeatures(featureVectors, 7);
                Map<String, Map<String, Double>> inlinksDirectlinksfeatureVectors = featuregenerator.extractFeatures(featureVectors, 8);
                Map<String, Map<String, Double>> bidirlinksDirectlinksfeatureVectors = featuregenerator.extractFeatures(featureVectors, 9);
                Map<String, Map<String, Double>> sortedhopRelationFeatureVectors = featuregenerator.sortFeatureVectors(hopRelationfeatureVectors);
                Map<String, Map<String, Double>> sortedrelComentionFeatureVectors = featuregenerator.sortFeatureVectors(relComentionfeatureVectors);
                Map<String, Map<String, Double>> sortedcomentionFeatureVectors = featuregenerator.sortFeatureVectors(comentionfeatureVectors);
                Map<String, Map<String, Double>> sortedcocouplingCountFeatureVectors = featuregenerator.sortFeatureVectors(cocouplingcountfeatureVectors);
                Map<String, Map<String, Double>> sortedcocouplingRelFeatureVectors = featuregenerator.sortFeatureVectors(cocouplingrelfeatureVectors);
                Map<String, Map<String, Double>> sortedbiblorelcouplingFeatureVectors = featuregenerator.sortFeatureVectors(biblorelcouplingfeatureVectors);
                Map<String, Map<String, Double>> sortedbiblocountcouplingFeatureVectors = featuregenerator.sortFeatureVectors(biblocountcouplingfeatureVectors);
                Map<String, Map<String, Double>> sortedoutlinksDirectlinksFeatureVectors = featuregenerator.sortFeatureVectors(outlinksDirectlinksfeatureVectors);
                Map<String, Map<String, Double>> sortedinlinksDirectlinksFeatureVectors = featuregenerator.sortFeatureVectors(inlinksDirectlinksfeatureVectors);
                Map<String, Map<String, Double>> sortedbidirlinksDirectlinksFeatureVectors = featuregenerator.sortFeatureVectors(bidirlinksDirectlinksfeatureVectors);

                WriteFile write_file = new WriteFile();
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                write_file.generateEntityRunFile(sortedhopRelationFeatureVectors, "1hoprelation_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedrelComentionFeatureVectors, "rel_comention_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedcomentionFeatureVectors, "count_comention_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedcocouplingCountFeatureVectors, "co_coupling_count_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedcocouplingRelFeatureVectors, "co_coupling_relevance_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedbiblorelcouplingFeatureVectors, "biblo_relevance_coupling_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedbiblocountcouplingFeatureVectors, "biblo_count_coupling_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedoutlinksDirectlinksFeatureVectors, "outlinks_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedinlinksDirectlinksFeatureVectors, "inlinks_feature_vector"+level+datafile);
                write_file.generateEntityRunFile(sortedbidirlinksDirectlinksFeatureVectors, "bidirectional_feature_vector"+level+datafile);
                write_file.generateFeatureVectorRunFile(featureVectors, "feature_vectors"+level+datafile);
                write_file.generateEntityRankLibRunFile(featureVectors, searchParser.getQrelfile(), "rank_lib"+level+datafile);

                System.out.println("**************************************************************************");
                System.out.println(sortedhopRelationFeatureVectors);
                Map<String, Map<String, Double>> hop_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedhopRelationFeatureVectors);
                hop_entities_score = e.getRerankedParas(hop_entities_score);

                write_file.generateEntityRunFile(hop_entities_score, "paragraph_1hoprelation_feature"+level+datafile);

                Map<String, Map<String, Double>> rel_comention_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedrelComentionFeatureVectors);
                rel_comention_entities_score = e.getRerankedParas(rel_comention_entities_score);

                write_file.generateEntityRunFile(rel_comention_entities_score, "paragraph_rel_comention_feature"+level+datafile);


                Map<String, Map<String, Double>> comention_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedcomentionFeatureVectors);
                comention_entities_score = e.getRerankedParas(comention_entities_score);

                write_file.generateEntityRunFile(comention_entities_score, "paragraph_count_comention_feature"+level+datafile);

                Map<String, Map<String, Double>> co_coupling_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedcocouplingCountFeatureVectors);
                co_coupling_entities_score = e.getRerankedParas(co_coupling_entities_score);

                write_file.generateEntityRunFile(co_coupling_entities_score, "paragraph_co_coupling_feature"+level+datafile);

                Map<String, Map<String, Double>> biblo_co_coupling_entities_score = e.getParagraphsScoreDouble(bm25_ranking, sortedbiblorelcouplingFeatureVectors);
                biblo_co_coupling_entities_score = e.getRerankedParas(biblo_co_coupling_entities_score);

                write_file.generateEntityRunFile(biblo_co_coupling_entities_score, "paragraph_biblo_co_coupling_feature"+level+datafile);


            }catch (Exception ioe){
                ioe.printStackTrace();
            }
        }


    }

    }
