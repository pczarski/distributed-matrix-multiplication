package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.*;
import com.example.matrix.BlockMatrix;
import com.example.matrix.MatrixHelpers;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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


    private final static String[] addressList = {"localhost", "localhost", "localhost", "localhost", "localhost", "localhost", "localhost", "localhost"};
    private final static int[] portList = {50051, 9090, 50053, 50054, 50055, 50056, 50057, 50058};
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

    private Double[][] footprintResult;

    public void multiplyMatrix(double deadline, long startTime) throws InterruptedException, ExecutionException{

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
        this.footprintResult = StubHelpers.multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,0), stubs.get(0));
        long t2 = System.currentTimeMillis();

        // deadline is in milliseconds
        int nServers = (int) Math.ceil((t2-t1)*(multiplyCalls) / (deadline - (t2 - startTime)));
        nServers = Math.min(nServers, 8);
        multiplyMatrix(2, blocks, rows, Ab, Bb, C);
        System.out.println(t2-t1);
        System.out.println(nServers);
        System.out.println(MatrixHelpers.twoDArrToString(C));
       // shutDownChannels();
    }


    @Autowired
    private StubHandler handler;

    private void multiplyMatrix(int nServers, int blocks, int rows, BlockMatrix Ab, BlockMatrix Bb, Double[][] C) throws InterruptedException, ExecutionException{
        List<CompletableFuture<Object>> futures;
        List<CompletableFuture<Double[][]>> multResults;
        CompletableFuture<Double[][]> res00;
        CompletableFuture<Double[][]> res01;
        CompletableFuture<Double[][]> res10;
        CompletableFuture<Double[][]> res11;
        MatrixServiceGrpc.MatrixServiceBlockingStub[] stubList;
        if(blocks == MIN_BLOCKS){
            switch (nServers){
                case 1:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs1 = {stubs.get(0), stubs.get(0), stubs.get(0), stubs.get(0), stubs.get(0), stubs.get(0), stubs.get(0), stubs.get(0)};
                    multiplyWithStubSequence(stubs1, C, Ab, Bb);
                    // complete after footprint res for 0,0
//                    MatrixHelpers.mapToLargerMatrix(C,
//                            StubHelpers.addBlock(footprintResult, StubHelpers.multiplyBlock(Ab.getBlock(0, 1), Bb.getBlock(1, 0), stubs.get(0)), stubs.get(0)),
//                            0,0
//                    );
//                    //res for 0, 1
//                    StubHelpers.addAndMultiply(stubs.get(0), C, Ab, Bb, 0, 0, 0,1, 0,  1, 1, 1, 0, 1);
//                    //res for 1, 0
//                    StubHelpers.addAndMultiply(stubs.get(0), C, Ab, Bb, 1, 0, 0,0, 1,  1, 1, 0, 1, 0);
//                    //res for 1,1
//                    StubHelpers.addAndMultiply(stubs.get(0), C, Ab, Bb, 1, 0, 0,1, 1,  1, 1, 1, 1, 1);
                    break;
                case 2:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs2 = {stubs.get(0), stubs.get(0), stubs.get(0), stubs.get(0), addStub(), stubs.get(1), stubs.get(1), stubs.get(1)};
                    multiplyWithStubSequence(stubs2, C, Ab, Bb);
//
//                    // res for 0, 0
//                    CompletableFuture<Object> calc1 = handler.addAndMultiply(stubs.get(0), C, Ab, Bb, 0, 0, 0,0, 0,  1, 1, 0, 0, 0);
//                    //res for 0, 1
//                    CompletableFuture<Object> calc2 = handler.addAndMultiply(stubs.get(0), C, Ab, Bb, 0, 0, 0,1, 0,  1, 1, 1, 0, 1);
//                    //res for 1, 0
//                    CompletableFuture<Object> calc3 = handler.addAndMultiply(addStub(), C, Ab, Bb, 1, 0, 0,0, 1,  1, 1, 0, 1, 0);
//                    //res for 1,1
//                    CompletableFuture<Object> calc4 = handler.addAndMultiply(stubs.get(1), C, Ab, Bb, 1, 0, 0,1, 1,  1, 1, 1, 1, 1);
//
//                    CompletableFuture.allOf(calc1, calc2, calc3, calc4).join();
                    break;
                case 3:
//
//                    futures = new ArrayList<>();
//                    // res for 0, 0
//                    futures.add(handler.addAndMultiply(stubs.get(0), C, Ab, Bb, 0, 0, 0,0, 0,  1, 1, 0, 0, 0));
//                    //res for 0, 1
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 0, 0, 0,1, 0,  1, 1, 1, 0, 1));
//                    //res for 1, 0
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 1, 0, 0,0, 1,  1, 1, 0, 1, 0));
//                    //res for 1,1
//                    futures.add(handler.addAndMultiply(stubs.get(0), C, Ab, Bb, 1, 0, 0,1, 1,  1, 1, 1, 1, 1));
//
//                    waitForStubs(futures);
                    break;
                case 4:
//                    futures = new ArrayList<>();
//                    // res for 0, 0
//                    futures.add(handler.addAndMultiply(stubs.get(0), C, Ab, Bb, 0, 0, 0,0, 0,  1, 1, 0, 0, 0));
//                    //res for 0, 1
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 0, 0, 0,1, 0,  1, 1, 1, 0, 1));
//                    //res for 1, 0
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 1, 0, 0,0, 1,  1, 1, 0, 1, 0));
//                    //res for 1,1
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 1, 0, 0,1, 1,  1, 1, 1, 1, 1));
//                    waitForStubs(futures);
                    break;
                case 5:
//                    futures = new ArrayList<>();
//                    // res for 0, 0
//                    CompletableFuture<Double[][]> resA = handler.multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,0), stubs.get(0));
//                    CompletableFuture<Double[][]> resB = handler.multiplyBlock(Ab.getBlock(0,1), Bb.getBlock(1,0), addStub());
//                    //res for 0, 1
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 0, 0, 0,1, 0,  1, 1, 1, 0, 1));
//                    //res for 1, 0
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 1, 0, 0,0, 1,  1, 1, 0, 1, 0));
//                    //res for 1,1
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 1, 0, 0,1, 1,  1, 1, 1, 1, 1));
//
//                    //complete 0,0
//                    CompletableFuture<Double[][]> res = handler.addBlock(resA.get(), resB.get(), stubs.get(0));
//                    MatrixHelpers.mapToLargerMatrix(C, res.get(), 0, 0);
//                    waitForStubs(futures);
                    break;
                case 6:
//                    multResults = new ArrayList<>();
//                    futures = new ArrayList<>();
//                    // 0,0
//                    multResults.add(handler.multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,0), stubs.get(0)));
//                    multResults.add(handler.multiplyBlock(Ab.getBlock(0,1), Bb.getBlock(1,0), addStub()));
//
//                    // 0,1
//                    multResults.add(handler.multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,1), addStub()));
//                    multResults.add(handler.multiplyBlock(Ab.getBlock(0,1), Bb.getBlock(1,1), addStub()));
//
//                    //res for 1, 0
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 1, 0, 0,0, 1,  1, 1, 0, 1, 0));
//                    //res for 1,1
//                    futures.add(handler.addAndMultiply(addStub(), C, Ab, Bb, 1, 0, 0,1, 1,  1, 1, 1, 1, 1));
//
//                    res00 = handler.addBlock(multResults.get(0).get(), multResults.get(1).get(), stubs.get(0));
//                    res01 = handler.addBlock(multResults.get(2).get(), multResults.get(3).get(), stubs.get(1));
//                    MatrixHelpers.mapToLargerMatrix(C, res00.get(), 0, 0);
//                    MatrixHelpers.mapToLargerMatrix(C, res01.get(), 0, Ab.getBlockSize());
//                    waitForStubs(futures);
//                    break;
                case 8:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs8 = {stubs.get(0), addStub(), addStub(), addStub(), addStub(), addStub(), addStub(), addStub()};
                    multiplyWithStubSequence(stubs8, C, Ab, Bb);
            }
        }else{

        }
    }

    private void multiplyWithStubSequence(MatrixServiceGrpc.MatrixServiceBlockingStub[] stubList, Double[][] C, BlockMatrix Ab, BlockMatrix Bb) throws InterruptedException, ExecutionException{
        ArrayList<CompletableFuture<Double[][]>> multResults = new ArrayList<>();
        // 0,0
        multResults.add(handler.multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,0), stubList[0]));
       // System.out.println(MatrixHelpers.twoDArrToString(multResults.get(0).get()));
        multResults.add(handler.multiplyBlock(Ab.getBlock(0,1), Bb.getBlock(1,0), stubList[1]));
        //System.out.println(MatrixHelpers.twoDArrToString(multResults.get(1).get()));
        // 0,1
        multResults.add(handler.multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,1), stubList[2]));
        multResults.add(handler.multiplyBlock(Ab.getBlock(0,1), Bb.getBlock(1,1), stubList[3]));

        // 1,0
        multResults.add(handler.multiplyBlock(Ab.getBlock(1,0), Bb.getBlock(0,0), stubList[4]));
        multResults.add(handler.multiplyBlock(Ab.getBlock(1,1), Bb.getBlock(1,0), stubList[5]));

        // 1,1
        multResults.add(handler.multiplyBlock(Ab.getBlock(1,0), Bb.getBlock(0,1), stubList[6]));
        multResults.add(handler.multiplyBlock(Ab.getBlock(1,1), Bb.getBlock(1,1), stubList[7]));

        CompletableFuture<Double[][]> res00 = handler.addBlock(multResults.get(0).get(), multResults.get(1).get(), stubList[0]);
        CompletableFuture<Double[][]> res01 = handler.addBlock(multResults.get(2).get(), multResults.get(3).get(), stubList[2]);
        CompletableFuture<Double[][]> res10 = handler.addBlock(multResults.get(4).get(), multResults.get(5).get(), stubList[4]);
        CompletableFuture<Double[][]> res11 = handler.addBlock(multResults.get(6).get(), multResults.get(7).get(), stubList[6]);
        MatrixHelpers.mapToLargerMatrix(C, res00.get(), 0, 0);
        MatrixHelpers.mapToLargerMatrix(C, res01.get(), 0, Ab.getBlockSize());
        MatrixHelpers.mapToLargerMatrix(C, res10.get(), Ab.getBlockSize(), 0);
        MatrixHelpers.mapToLargerMatrix(C, res11.get(), Ab.getBlockSize(), Ab.getBlockSize());
    }

    // Always uses the second stub to perform the addition
//    private void addAndMultiplyTwoStubs(MatrixServiceGrpc.MatrixServiceBlockingStub stub, Double[][] C, BlockMatrix Ab, BlockMatrix Bb,
//    int a1i, int a1j, int b1i, int b1j, int a2i, int a2j, int b2i, int b2j, int row, int col
//    ) throws InterruptedException
//
//    {
//        CompletableFuture<Double[][]> resA = handler.multiplyBlock(Ab.getBlock(a1i, a1j), Bb.getBlock(b1i, b1j), stub);
//        CompletableFuture<Double[][]> resB = handler.multiplyBlock(Ab.getBlock(a2i, a2j), Bb.getBlock(b2i, b2j), stub);
//        MatrixHelpers.mapToLargerMatrix(C,
//                addBlock(
//                        multiplyBlock(Ab.getBlock(a1i, a1j), Bb.getBlock(b1i, b1j), stub),
//                        multiplyBlock(Ab.getBlock(a2i, a2j), Bb.getBlock(b2i, b2j), stub), stub),
//                row * Ab.getBlockSize(), col * Ab.getBlockSize()
//        );
//    }

    private static void waitForStubs(List<CompletableFuture<Object>> futures) throws InterruptedException, ExecutionException {
        for(CompletableFuture<Object> future: futures){
            future.get();
        }
    }
    private int getNumberOfBlocks(int size){
        return (size <= SMALLEST_N_BLOCKS_THRESHOLD) ? MIN_BLOCKS : MAX_BLOCKS;
    }

    public void shutDownChannels(){
        for(ManagedChannel channel: channels){
            channel.shutdown();
        }
    }

    private MatrixServiceGrpc.MatrixServiceBlockingStub addStub(){
        if(stubs.size() == MAX_SERVERS){
            throw new IllegalArgumentException();
        }
        channels.add(ManagedChannelBuilder.forAddress(addressList[channels.size()-1], portList[channels.size()-1]).usePlaintext().build());
        stubs.add(MatrixServiceGrpc.newBlockingStub(channels.get(channels.size()-1)));
        return stubs.get(stubs.size()-1);
    }



}
