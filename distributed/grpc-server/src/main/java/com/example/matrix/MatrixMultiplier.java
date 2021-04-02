package com.example.matrix;

public class MatrixMultiplier {

    public static Double dotProduct(Double[][] A, Double[][] B, int ai, int bi) {
        assert A.length == B.length;
        double c = 0;
        for(int i = 0; i < A.length; i++){
            c += A[ai][i] * B[i][bi];
        }
        return c;
    }

    public static Double[][] multiplyBlock(Double[][] A, Double[][] B) {
        Double[][] C = new Double[A.length][B.length];
        for(int i = 0; i < C.length; i++){
            for(int j = 0; j <C.length; j++){
                C[i][j] = dotProduct(A, B, i, j);
            }
        }
        return C;
    }

    public static Double[][] addBlock(Double[][] A, Double[][] B){
        Double[][] C = new Double[A.length][B.length];
        for(int i = 0; i < C.length; i++){
            for(int j = 0; j < C.length; j++){
                C[i][j]=A[i][j]+B[i][j];
            }
        }
        return C;
    }
}
