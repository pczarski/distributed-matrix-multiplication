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
