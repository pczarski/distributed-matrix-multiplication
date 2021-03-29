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

    public static Double[][] divideMatrix(Double[][] matrix, int start_row, int start_column, int size){
        Double[][] out = new Double[size][size];
        for(int i = 0; i < out.length; i++){
            for(int j = 0; j<out[i].length; j++){
                out[i][j] = matrix[i+start_row][j+start_column];
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

    public static void mapToLargerMatrix(Object[][] target, Object[][] from, int start_row, int start_column){
        assert target.length >= from.length;
        for(int i = 0; i<from.length; i++){
            for(int j = 0; j<from[i].length; j++){
                target[start_row+i][start_column+j] = from[i][j];
            }
        }
    }
}
