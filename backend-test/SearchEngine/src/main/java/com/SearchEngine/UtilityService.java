package com.SearchEngine;


import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.stereotype.Service;

@Service
public class UtilityService {

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
}
