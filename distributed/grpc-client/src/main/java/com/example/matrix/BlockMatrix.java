package com.example.matrix;

import com.example.matrix.helpers.MatrixHelpers;

/**
 * This class allows an abstraction of a matrix divided into blocks.
 * Each block is accessible with standard matrix indices as if they were individual elements
 * assumes that a matrix is square and a power of 2 in size
 */
public class BlockMatrix {

    Double[][] matrix;

    public int getBlocks() {
        return blocks;
    }

    public int getBlockSize() {
        return (int) Math.sqrt((double) (matrix.length * matrix.length) / getBlocks());
    }

    private int blocks;

    public BlockMatrix(Double[][] matrix, int blocks){
        this.matrix = matrix;
        this.blocks = blocks;
    }

    public Double[][] getBlock(int i, int j){
        int blockSize = getBlockSize();
        return MatrixHelpers.divideMatrix(matrix, i*blockSize, j*blockSize, blockSize);
    }

    @Override
    public String toString() {
        return "Blocks: " + getBlocks() +"\nmatrix:\n"+ MatrixHelpers.matrixToString(matrix);
    }
}
