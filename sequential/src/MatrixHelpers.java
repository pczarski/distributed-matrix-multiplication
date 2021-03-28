public class MatrixHelpers {
    public static Double[][] divideMatrix(Double[][] matrix, int start_row, int start_column, int size){
        Double[][] out = new Double[size][size];
        for(int i = 0; i < out.length; i++){
            for(int j = 0; j<out[i].length; j++){
                out[i][j] = matrix[i+start_row][j+start_column];
            }
        }
        return out;
    }

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
}
