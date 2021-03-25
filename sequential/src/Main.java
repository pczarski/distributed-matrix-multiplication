import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Block Matrix Multiplication for square matrices
 */

public class Main {

    public static final int MAX_BLOCKS = 8;

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

    public static Object[][] divideMatrix(Object[][] matrix, int start_row, int start_column, int size){
        Object[][] out = new Object[size][size];
        for(int i = 0; i < out.length; i++){
            for(int j = 0; j<out[i].length; j++){
                out[i][j] = matrix[i+start_row][j+start_column];
            }
        }
        return out;
    }

    public static void mapToLargerMatrix(Object[][] target, Object[][] from, int start_row, int start_column){
        assert target.length >= from.length;
        for(int i = 0; i<from.length; i++){
            for(int j = 0; j<from[i].length; j++){
                target[start_row+i][start_column+j] = from[i][j];
            }
        }
    }

    public static Double[][] blockMultiplication(Double[][] A, Double[][] B, int blocks){
        // blocks can be: 1, 2, 4, 8
        assert A.length == B.length;
        int size = A.length;
        if(size/2 <= blocks) {
            // we cannot divide more into more than size/2 blocks
            blocks = size/2;
        }
        int blockSize = size / blocks;
        Double[][] result = new Double[size][size];
        for(int i = 0; i<size; i+=blockSize){
           // mapToLargerMatrix();
        }


        return null;
    }


    public static void main(String[] args) {
        Double A[][] = {
                {1., 2., 3., 4.},
                {5., 6., 7., 8.},
                {9., 10., 11., 12.},
                {13., 14., 15., 16.}
        };

        Double B[][] = {
                {1., 2., 3., 4.},
                {5., 6., 7., 8.},
                {9., 10., 11., 12.},
                {13., 14., 15., 16.}
        };

        Double C[][] = new Double[8][8];
        mapToLargerMatrix(C, A, 4,4);
        System.out.println(twoDArrToString(C));

        ArrayList<ArrayList<Double>> test = new ArrayList<>();

    }
}
