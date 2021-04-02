package com.example.matrix.helpers;

import java.util.List;

public class MatrixHelpers {

    private final Double[][] matrix;
    private int currRow;

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
