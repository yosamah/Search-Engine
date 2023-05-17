package com.SearchEngine;


import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UtilityService {

    List<String> stopWords;

    UtilityService(){
        stopWords = new ArrayList<>(Arrays.asList("is", "the", "an", "are", "at",
                "be", "but", "by", "for", "if", "in", "into", "it", "no", "not", "of", "on", "or",
                "such", "that", "their", "then", "there", "these", "they", "to", "was", "will", "with",
                "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "you're", "yours",
                "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself",
                "its", "itself", "them", "theirs", "what", "which", "who", "whom", "this", "that", "those",
                "am", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a",
                "and", "because", "as", "until", "while", "about", "against", "between", "through", "during",
                "before", "after", "above", "below", "from", "up", "down", "out", "off", "over", "under", "again",
                "further", "once", "here", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more",
                "most", "other", "some", "much", "nor", "only", "own", "same", "so", "than", "too", "very", "can",
                "will", "just", "don't", "should", "now"));
    }

    public double[] computePopularity(double[][] A, double[] x) {
        double d = 2.0;
        RealMatrix matrixA = new Array2DRowRealMatrix(A);
        RealMatrix resultOld = new Array2DRowRealMatrix(x);
        RealMatrix resultNew = matrixA.multiply(resultOld);
        double[] matrixDifference;
        int counter = 0;
        while (d > 0.1) {
            resultOld = resultNew;
            resultNew = matrixA.multiply(resultOld);
            matrixDifference = resultOld.subtract(resultNew).getColumn(0);
            d = dotProduct(matrixDifference, matrixDifference);
            counter++;
            if (counter > 1000)
                break;
        }
        System.out.println(counter);
        return resultNew.getColumn(0);
    }

    double dotProduct(double[] x, double[] y) {
        if (x.length != y.length)
            throw new RuntimeException("Arrays must be same size");
        double sum = 0;
        for (int i = 0; i < x.length; i++)
            sum += x[i] * y[i];
        return sum;
    }

    List<String> removeStopWords(String searchedWord){
        List<String> words = List.of(searchedWord.toLowerCase().split("\\s+"));
        List<String> processedWords = new ArrayList<>();
        for(String word: words)
            if(!stopWords.contains(word))
                processedWords.add(word);
        return processedWords;
    }
}
