package com.example.matrix;

import java.util.List;

public class Matrix {
    Double[][] matrix;
    public Matrix(int n, int m){
        matrix = new Double[n][m];
    }
    public void setN(int n, List<Double> list) {
        matrix[n] = (Double[]) list.toArray();
    }
    public Double[][] getMatrix(){
        return matrix;
    }
    public static Double[][] convertToMatrix(List<List<Double>> list){
        //assumes square matrix
        if(list.size() == 0){
            return new Double[0][0];
        }
        Double[][] temp = new Double[list.size()][list.get(0).size()];
        int i = 0;
        for(List<Double> ls: list){
            temp[++i] = (Double[]) ls.toArray();
        }
        return temp;
    }
}
