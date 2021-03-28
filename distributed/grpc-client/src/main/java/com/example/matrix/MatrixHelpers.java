package com.example.matrix;

import java.util.List;

public class MatrixHelpers {

    private Double[][] matrix;
    private int currRow;

    public static Double[][] ZeroMatrix(int size){
        Double[][] out = new Double[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                out[i][j] = 0.;
            }
        }
        return out;
    }

    public static String twoDArrToString(Object[][] A){
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < A.length; i++){
            for(int j = 0; j<A[i].length; j ++){
                buffer.append(A[i][j]+" ");
            }
            buffer.append("\n");
        }
        return buffer.substring(0, buffer.length()-2);
    }

    public static Double[][] convertToMatrix(List<List<Double>> list){
        //assumes square matrix
        if(list.size() == 0){
            return new Double[0][0];
        }
        Double[][] temp = new Double[list.size()][list.get(0).size()];
        int i = 0;
        for(List<Double> ls: list){
            temp[i++] = ls.toArray(Double[]::new);
        }
        return temp;
    }

    public MatrixHelpers(int size){
        this.matrix = new Double[size][size];
        this.currRow = 0;
    }

    public void parseRow(List<Double> row){
        this.matrix[currRow++] = row.toArray(Double[]::new);
    }

    public Double[][] getMatrix(){
        return this.matrix;
    }
}
