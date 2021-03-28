

public class BlockMatrix {
    // assumes square matrix power of 2

    Double[][] matrix;

    public int getBlocks() {
        return blocks;
    }

    public int getSize() {
        return matrix.length;
    }

    public int getBlockRows() {
        return (int) getSize() / getBlockSize();
    }

    public int getBlockSize() {
        return (int) Math.sqrt((double) (matrix.length * matrix.length) / getBlocks());
    }

    private int blocks;
    public BlockMatrix(int n, int m, int blocks){
        this.matrix = new Double[n][m];
        this.blocks = blocks;

    }
    public BlockMatrix(Double[][] matrix, int blocks){
        this.matrix = matrix;
        this.blocks = blocks;
    }

    public Double[][] getBlock(int i, int j){
        int m = matrix.length;
        int blockSize = getBlockSize();
        return MatrixHelpers.divideMatrix(matrix, i*blockSize, j*blockSize, blockSize);
    }

    @Override
    public String toString() {
        return MatrixHelpers.twoDArrToString(matrix);
    }
}
