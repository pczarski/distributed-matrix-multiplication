package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.*;
import com.example.matrix.BlockMatrix;
import com.example.matrix.MatrixHelpers;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class GRPCClientService {

    private static final int MAX_SERVERS = 8;
    private static final int SMALLEST_N_BLOCKS_THRESHOLD = 1024;
    private static final int MIN_BLOCKS = 4;
    private static final int MAX_BLOCKS = 16;

    private ArrayList<ManagedChannel> channels = new ArrayList(
            Arrays.asList(ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build())
    );

    private ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub> stubs =
            new ArrayList<>(Arrays.asList(MatrixServiceGrpc.newBlockingStub(channels.get(0))));


    static Double A[][] = {
            {1., 2., 3., 4.},
            {5., 6., 7., 8.},
            {9., 10., 11., 12.},
            {13., 14., 15., 16.}
    };

    static Double B[][] = {
            {1., 2., 3., 4.},
            {5., 6., 7., 8.},
            {9., 10., 11., 12.},
            {13., 14., 15., 16.}
    };

    static Double X[][] = {
            {1., 2., 3., 4.,1., 2., 3., 4.},
            {5., 6., 7., 8.,1., 1., 3., 8.},
            {9., 2., 3., 4.,1., 2., 3., 9.},
            {1., 6., 3., 8.,0., 3., 3., 4.},
            {1., 2., 3., 4.,2., 2., 3., 2.},
            {5., 6., 4., 8.,1., 5., 3., 3.},
            {1., 2., 3., 4.,1., 2., 3., 5.},
            {5., 6., 7., 8.,1., 9., 3., 8.},
    };

    static Double Y[][] = {
            {1., 2., 3., 4.,1., 2., 3., 4.},
            {5., 6., 7., 8.,1., 1., 3., 8.},
            {9., 2., 3., 4.,1., 2., 3., 9.},
            {1., 6., 3., 8.,0., 3., 3., 4.},
            {1., 2., 3., 4.,2., 2., 3., 2.},
            {5., 6., 4., 8.,1., 5., 3., 3.},
            {1., 2., 3., 4.,1., 2., 3., 5.},
            {5., 6., 7., 8.,1., 9., 3., 8.},
    };

    public void muliplyBlock(){

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);

        MatrixResponse response = stub.multiplyBlock(BufferHelpers.buildRequest(A, B));
        channel.shutdown();
        Double[][] C = BufferHelpers.parseMatrix(response.getIList());
        System.out.println(MatrixHelpers.twoDArrToString(C));
    }

    public Double[][] multiplyBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        MatrixResponse response = stub.multiplyBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    public void multiplyMatrix(double deadline){
        // TODO the loaded matrix
        int blocks = getNumberOfBlocks(A.length);
        BlockMatrix Ab = new BlockMatrix(A, blocks);
        BlockMatrix Bb = new BlockMatrix(B, blocks);
        int rows = (int) Math.sqrt(blocks);
        int multiplyCalls = rows * rows * rows;
        int addCalls = (rows-1)*rows*rows;
        /*
        AddBlock calls are faster than multiplyBlock calls, but since this is en estimation, and
        since we have to leave some extra time for network delays etc. they will be treated to take
        equal amount of time.
         */

        long t1 = System.currentTimeMillis();
        Double[][] footPrintMultResult = multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,0), stubs.get(0));
        long t2 = System.currentTimeMillis();

        // deadline is in milliseconds
        int nServers = (int) Math.ceil((t2-t1)*(multiplyCalls+addCalls) / deadline);
        nServers = Math.min(nServers, 8);
        System.out.println(t2-t1);
        System.out.println(nServers);
        System.out.println(MatrixHelpers.twoDArrToString(footPrintMultResult));
        shutDownChannels();
    }

//    private long footPrinting(int nCalls, Double[][] B1, Double[][] B2, Double[][] resultMatrix){
//        long t1 = System.currentTimeMillis();
//        resultMatrix = multiplyBlock(B1, B2, stubs.get(0));
//        long t2 = System.currentTimeMillis();
//        return t2 - t1;
//
//    }

    private void multiplyMatrix(int nServers, int blocks, Double[][] A, Double[][] B){
        Double[][] C = new Double[A.length][A.length];
        BlockMatrix Ab = new BlockMatrix(A, blocks);
        BlockMatrix Bb = new BlockMatrix(B, blocks);
        int rows = (int) Math.sqrt(blocks);

    }

    private int getNumberOfBlocks(int size){
        return (size <= SMALLEST_N_BLOCKS_THRESHOLD) ? MIN_BLOCKS : MAX_BLOCKS;
    }

    private void shutDownChannels(){
        for(ManagedChannel channel: channels){
            channel.shutdown();
        }
    }


}
