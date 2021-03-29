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

    private Double[][] multiplyBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        MatrixResponse response = stub.multiplyBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    private Double[][] addBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        MatrixResponse response = stub.addBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    private Double[][] footprintResult;

    public void multiplyMatrix(double deadline, long startTime){

        // TODO the loaded matrix
        int blocks = getNumberOfBlocks(A.length);
        BlockMatrix Ab = new BlockMatrix(A, blocks);
        BlockMatrix Bb = new BlockMatrix(B, blocks);
        Double[][] C = new Double[A.length][A.length];

        int rows = (int) Math.sqrt(blocks);
        int multiplyCalls = rows * rows * rows;
        int addCalls = (rows-1)*rows*rows;

        /*
        Footprinting:
        AddBlock calls are faster than multiplyBlock calls, but since this is en estimation, and
        since we have to leave some extra time for network delays etc. they will be treated to take
        equal amount of time.
         */

       // StubHandler handler0 = new StubHandler(stubs.get(0), C);
        long t1 = System.currentTimeMillis();
       // handler0.performSingleAction(ActionType.MULTIPLY_BLOCK, Ab.getBlock(0,0), Bb.getBlock(0,0));
        this.footprintResult = multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,0), stubs.get(0));
        long t2 = System.currentTimeMillis();

        // deadline is in milliseconds
        int nServers = (int) Math.ceil((t2-t1)*(multiplyCalls+addCalls-1) / (deadline - (t2 - startTime)));
        nServers = Math.min(nServers, 8);
        multiplyMatrix(1, blocks, rows, Ab, Bb, C);
        System.out.println(t2-t1);
        System.out.println(nServers);
        System.out.println(MatrixHelpers.twoDArrToString(C));
        shutDownChannels();
    }

//    private long footPrinting(int nCalls, Double[][] B1, Double[][] B2, Double[][] resultMatrix){
//        long t1 = System.currentTimeMillis();
//        resultMatrix = multiplyBlock(B1, B2, stubs.get(0));
//        long t2 = System.currentTimeMillis();
//        return t2 - t1;
//
//    }
    private void addAndMultiply(
            MatrixServiceGrpc.MatrixServiceBlockingStub stub, Double[][] C, BlockMatrix Ab, BlockMatrix Bb,
            int a1i, int a1j, int b1i, int b1j, int a2i, int a2j, int b2i, int b2j, int row, int col
    ){
        MatrixHelpers.mapToLargerMatrix(C,
                addBlock(
                        multiplyBlock(Ab.getBlock(a1i, a1j), Bb.getBlock(b1i, b1j), stub),
                        multiplyBlock(Ab.getBlock(a2i, a2j), Bb.getBlock(b2i, b2j), stub), stub),
                row * Ab.getBlockSize(), col * Ab.getBlockSize()
        );
    }

    private void multiplyMatrix(int nServers, int blocks, int rows, BlockMatrix Ab, BlockMatrix Bb, Double[][] C){
        if(blocks == MIN_BLOCKS){
            switch (nServers){
                case 1:
                    // complete after footprint res for 0,0
                    MatrixHelpers.mapToLargerMatrix(C,
                            addBlock(footprintResult, multiplyBlock(Ab.getBlock(0, 1), Bb.getBlock(1, 0), stubs.get(0)), stubs.get(0)),
                            0,0
                    );
                    //res for 0, 1
                    addAndMultiply(stubs.get(0), C, Ab, Bb, 0, 0, 0,1, 0,  1, 1, 1, 0, 1);
                    //res for 1, 0
                    addAndMultiply(stubs.get(0), C, Ab, Bb, 1, 0, 0,0, 1,  1, 1, 0, 1, 0);
                    //res for 1,1
                    addAndMultiply(stubs.get(0), C, Ab, Bb, 1, 0, 0,1, 1,  1, 1, 1, 1, 1);
            }
        }else{

        }
    }

    private void finishFootPrint(BlockMatrix Ab, BlockMatrix Bb, Double[][] C, MatrixServiceGrpc.MatrixServiceBlockingStub stub){

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
