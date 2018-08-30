package com.company;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CreateRelationData {

    public static void main(String[] args) {
        // set up pipeline properties
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,entitymentions");
        // set up Stanford CoreNLP pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // build annotation for a review
        String fileContents;
        try {
            fileContents = IOUtils.slurpFile(args[0]);
        } catch (IOException e) {
            fileContents = "Joe Smith lives in Hawaii.";
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }     catch (Exception ex){
            fileContents = "Joe Smith lives in Hawaii.";
        }
        Annotation annotation = new Annotation(fileContents);
        pipeline.annotate(annotation);
        int sentNum = 0;
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            int tokenNum = 1;
            int elementNum = 0;
            int entityNum = 0;
            CoreMap currEntityMention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(entityNum);
            String currEntityMentionWords = currEntityMention.get(CoreAnnotations.TokensAnnotation.class).stream().map(token -> token.word()).
                    collect(Collectors.joining("/"));
            String currEntityMentionTags =
                    currEntityMention.get(CoreAnnotations.TokensAnnotation.class).stream().map(token -> token.tag()).
                            collect(Collectors.joining("/"));
            String currEntityMentionNER = currEntityMention.get(CoreAnnotations.EntityTypeAnnotation.class);
            while (tokenNum <= sentence.get(CoreAnnotations.TokensAnnotation.class).size()) {
                if (currEntityMention.get(CoreAnnotations.TokensAnnotation.class).get(0).index() == tokenNum) {
                    String entityText = currEntityMention.toString();
                    System.out.println(sentNum+"\t"+currEntityMentionNER+"\t"+elementNum+"\t"+"O\t"+currEntityMentionTags+"\t"+
                            currEntityMentionWords+"\t"+"O\tO\tO");
                    // update tokenNum
                    tokenNum += (currEntityMention.get(CoreAnnotations.TokensAnnotation.class).size());
                    // update entity if there are remaining entities
                    entityNum++;
                    if (entityNum < sentence.get(CoreAnnotations.MentionsAnnotation.class).size()) {
                        currEntityMention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(entityNum);
                        currEntityMentionWords = currEntityMention.get(CoreAnnotations.TokensAnnotation.class).stream().map(token -> token.word()).
                                collect(Collectors.joining("/"));
                        currEntityMentionTags =
                                currEntityMention.get(CoreAnnotations.TokensAnnotation.class).stream().map(token -> token.tag()).
                                        collect(Collectors.joining("/"));
                        currEntityMentionNER = currEntityMention.get(CoreAnnotations.EntityTypeAnnotation.class);
                    }
                } else {
                    CoreLabel token = sentence.get(CoreAnnotations.TokensAnnotation.class).get(tokenNum-1);
                    System.out.println(sentNum+"\t"+token.ner()+"\t"+elementNum+"\tO\t"+token.tag()+"\t"+token.word()+"\t"+"O\tO\tO");
                    tokenNum += 1;
                }
                elementNum += 1;
            }
            sentNum++;
            System.out.println();
            System.out.println("O\t3\tLive_In");
            System.out.println();
        }

    }
}