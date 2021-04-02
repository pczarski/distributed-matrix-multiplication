package com.example.matrix;

import com.example.grpc.client.grpcclient.exceptions.BadMatrixException;
import com.example.grpc.client.grpcclient.exceptions.IncompatibleMatrixException;
import com.example.grpc.client.grpcclient.exceptions.MatrixTooSmallException;

import java.util.Arrays;
import java.util.List;

public class MatrixHelpers {

    private Double[][] matrix;
    private int currRow;

    public static Double[][] zeroMatrix(int size){
        Double[][] out = new Double[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                out[i][j] = 0.;
            }
        }
        return out;
    }

    public static Double[][] parseMatrixFromString(String matrix) throws MatrixTooSmallException, IncompatibleMatrixException, BadMatrixException {
        if(matrix.length() == 0){
            throw new MatrixTooSmallException("Matrix size must be at least 2");
        }
        String lines[] = matrix.split("\\r?\\n");
        int rowLength = countCharInString(lines[0], ' ') + 1;
        if(rowLength != lines.length){
            throw new IncompatibleMatrixException("Matrix must be square");
        }
        if(rowLength == 1){
            throw new MatrixTooSmallException("Matrix size must be at least 2");
        }
        Double[][] ret = new Double[rowLength][rowLength];
        for(int i = 0; i < rowLength; i++){
            ret[i] = strArrToDouble(lines[i].split(" +"), rowLength);
        }

        return ret;
    }

    public static boolean isPowerOf2(Double[][] matrix){
        return (matrix.length & (matrix.length -1)) == 0;
    }

    private static Double[] strArrToDouble(String[] strings, int size) throws BadMatrixException {
        Double[] ret = new Double[size];
        if(strings.length != size){
            throw new BadMatrixException("Rows must be same length");
        }
        for(int i = 0; i < size; i++){
            ret[i] = Double.parseDouble(strings[i]);
        }
        return ret;
    }

    private static int countCharInString(String str, char c){
        int count =0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

//    private static Double[] strToDoubleArr(String row){
//        int rowLength = (row.length()+1)/2;
//        Double[] out = new Double[rowLength];
//        for(int i = 0; i< rowLength; i++){
//            out[i] = Double.parseDouble(String.valueOf(row.charAt(i*2)));
//        }
//        return out;
//    }

    public static Double[][] divideMatrix(Double[][] matrix, int start_row, int start_column, int size){
        Double[][] out = new Double[size][size];
        for(int i = 0; i < out.length; i++){
            for(int j = 0; j<out[i].length; j++){
                out[i][j] = matrix[i+start_row][j+start_column];
            }
        }
        return out;
    }

    public static String matrixToString(Object[][] A){
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
