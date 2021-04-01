import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Block Matrix Multiplication for square matrices
 */

public class Main {

    public static final int MAX_BLOCKS = 16;
    private static int add=0;
    private static int mult = 0;

    public static Double dotProduct(Double[][] A, Double[][] B, int ai, int bi) {
        assert A.length == B.length;
        double c = 0;
        for(int i = 0; i < A.length; i++){
            c += A[ai][i] * B[i][bi];
        }
        return c;
    }

    public static Double[][] multiplyBlock(Double[][] A, Double[][] B) {
        mult++;
        Double[][] C = new Double[A.length][B.length];
        for(int i = 0; i < C.length; i++){
            for(int j = 0; j <C.length; j++){
                C[i][j] = dotProduct(A, B, i, j);
            }
        }
        return C;
    }

    public static Double[][] addBlock(Double[][] A, Double[][] B){
        add++;
        Double[][] C = new Double[A.length][B.length];
        for(int i = 0; i < C.length; i++){
            for(int j = 0; j < C.length; j++){
                C[i][j]=A[i][j]+B[i][j];
            }
        }
        return C;
    }


    public static void mapToLargerMatrix(Object[][] target, Object[][] from, int start_row, int start_column){
        assert target.length >= from.length;
        for(int i = 0; i<from.length; i++){
            for(int j = 0; j<from[i].length; j++){
                target[start_row+i][start_column+j] = from[i][j];
            }
        }
    }


    public static Double[][] blockDotProduct(int rows, int ai, int bi, BlockMatrix A, BlockMatrix B){
        Double[][] temp;
        Double[][] res = MatrixHelpers.ZeroMatrix(A.getBlockSize());
        for(int i = 0; i < rows; i++){
            temp = multiplyBlock(A.getBlock(ai, i), B.getBlock(i, bi));
            res = addBlock(res, temp);
        }
        return res;
    }

    public static Double[][] blockMatrixMultiplication(Double[][] A, Double[][] B, int blocks){
        Double[][] C = new Double[A.length][A.length];
        BlockMatrix Ab = new BlockMatrix(A, blocks);
        BlockMatrix Bb = new BlockMatrix(B, blocks);
        int rows = (int) Math.sqrt(blocks);
        for(int i = 0; i<rows; i++){
            for(int j = 0; j < rows; j++){
                mapToLargerMatrix(C, blockDotProduct(rows, i, j, Ab, Bb), i*Ab.getBlockSize(), j*Bb.getBlockSize());
            }
        }
        return C;
    }


    public static void main(String[] args) {

        Double X[][] = {
                {1., 2., 3., 4.,1., 2., 3., 4.},
                {5., 6., 7., 8.,1., 1., 3., 8.},
                {9., 2., 3., 4.,1., 2., 3., 9.},
                {1., 6., 3., 8.,0., 3., 3., 4.},
                {1., 2., 3., 4.,2., 2., 3., 2.},
                {5., 6., 4., 8.,1., 5., 3., 3.},
                {1., 2., 3., 4.,1., 2., 3., 5.},
                {5., 6., 7., 8.,1., 9., 3., 8.},
        };

        Double Y[][] = {
                {1., 2., 3., 4.,1., 2., 3., 4.},
                {5., 6., 7., 8.,1., 1., 3., 8.},
                {9., 2., 3., 4.,1., 2., 3., 9.},
                {1., 6., 3., 8.,0., 3., 3., 4.},
                {1., 2., 3., 4.,2., 2., 3., 2.},
                {5., 6., 4., 8.,1., 5., 3., 3.},
                {1., 2., 3., 4.,1., 2., 3., 5.},
                {5., 6., 7., 8.,1., 9., 3., 8.},
        };

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

//        BlockMatrix C = new BlockMatrix(X, 4);
//        for(int i = 0; i < C.getBlockRows(); i++){
//            for(int j = 0; j < C.getBlockRows(); j++){
//                System.out.println(MatrixHelpers.twoDArrToString(C.getBlock(i, j)));
//                System.out.println("");
//            }
//        }
        System.out.println(MatrixHelpers.twoDArrToString(multiplyBlock(A, B)));
        System.out.println("");

        System.out.println(MatrixHelpers.twoDArrToString(blockMatrixMultiplication(A, B, 4)));
        System.out.println(add);
        System.out.println(mult);

//        mapToLargerMatrix(C, A, 4,4);
//        System.out.println(twoDArrToString(C));


    }
}
