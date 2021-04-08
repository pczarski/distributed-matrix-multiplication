package com.example.grpc.client.grpcclient.services;

import com.example.grpc.client.grpcclient.services.helpers.StubHelpers;
import com.example.grpc.client.grpcclient.exceptions.IncompatibleMatrixException;
import com.example.grpc.server.grpcserver.*;
import com.example.matrix.BlockMatrix;
import com.example.matrix.helpers.MatrixHelpers;
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
    private static final int SMALLEST_N_BLOCKS_THRESHOLD = 256;
    private static final int MIN_BLOCKS = 4;
    private static final int MAX_BLOCKS = 16;


    // we will always need at least one stub
    private ArrayList<ManagedChannel> channels = new ArrayList(
            Arrays.asList(ManagedChannelBuilder.forAddress("3.87.212.33", 9090).usePlaintext().build())
    );

    private ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub> stubs = new ArrayList<>(
            Arrays.asList(MatrixServiceGrpc.newBlockingStub(channels.get(0)))
    );


    // list for local testing:
    /*
    {"localhost", "localhost", "localhost", "localhost", "localhost", "localhost", "localhost", "localhost"}
    {9090, 50051, 50052, 50053, 50054, 50055, 50057, 50056}
     */
    //list for aws
    /*
    {"18.208.177.81", "54.174.112.40", "3.82.104.179", "34.201.60.223", "34.201.54.107", "54.152.37.176", "35.170.245.15", "54.242.181.182"}
    {9090, 9090, 9090, 9090, 9090, 9090, 9090, 9090}
     */
    private final static String[] ADDRESS_LIST = {"3.89.140.116", "18.234.166.111", "54.161.195.170", "3.86.224.86", "52.87.32.107", "3.87.215.200", "3.87.224.101", "3.88.170.238"};
    private final static int[] PORT_LIST = {9090, 9090, 9090, 9090, 9090, 9090, 9090, 9090};

    // we only use put request so the matrices are always initialized to something
    private Double[][] A = MatrixHelpers.zeroMatrix(4);
    private Double[][] B = MatrixHelpers.zeroMatrix(4);

    private final StubHandler handler;

    @Autowired
    public GRPCClientService(StubHandler handler) {
        this.handler = handler;
    }

    public void setA(Double[][] a) {
        A = a;
    }

    public void setB(Double[][] b) {
        B = b;
    }

    /**
     * Main method for the service
     * @param deadline in how many milliseconds the user wants to complete the multiplication
     * @param startTime system time in milliseconds for when the request was received
     * @return resulting matrix
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IncompatibleMatrixException
     */
    public Double[][] multiplyMatrix(long deadline, long startTime) throws InterruptedException, ExecutionException, IncompatibleMatrixException{

        if(A.length != B.length){
            throw new IncompatibleMatrixException("A and B must be the same size");
        }
        if(A.length == 2){
            // for a tiny matrix just return the result
            return StubHelpers.multiplyBlock(A, B, getStub(0));
        }
        int blocks = calculateNumber(A.length);
        BlockMatrix Ab = new BlockMatrix(A, blocks);
        BlockMatrix Bb = new BlockMatrix(B, blocks);
        Double[][] C = new Double[A.length][A.length];

        int rows = (int) Math.sqrt(blocks);
        int multiplyCalls = rows * rows * rows;

        /*
        Footprinting:
        AddBlock calls are faster than multiplyBlock calls, but since this is en estimation, and
        since we have to leave some extra time for network delays etc. they will be treated to take
        equal amount of time.
         */

        // some extra time needs to be given to account for sequential elements and network latency change
        // would need to be calculated dynamically normally, but this should be good enough for the coursework
        int offset = 0;//1132;

        long t1 = System.currentTimeMillis();
        StubHelpers.multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,0), getStub(0));
        long t2 = System.currentTimeMillis();

        // deadline is in milliseconds
        int nServers = (int) Math.ceil(((t2-t1)*(multiplyCalls)+offset) / Math.max((deadline - (t2-startTime)), 1)); //take time passed into consideration
        nServers = Math.min(nServers, 8);
        nServers = (nServers == 0)? 1 : nServers;
        multiplyMatrix(nServers, blocks, rows, Ab, Bb, C);
        System.out.println("\nNumber of servers used: "+nServers);
        return C;
    }

    /**
     * This methods implements the distributed matrix multiplication
     * @param nServers number of servers
     * @param blocks number of blocks
     * @param rows number of rows in the matrix
     * @param Ab Block matrix A
     * @param Bb Block matrix B
     * @param C Result matrix C
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void multiplyMatrix(int nServers, int blocks, int rows, BlockMatrix Ab, BlockMatrix Bb, Double[][] C) throws InterruptedException, ExecutionException{
        List<CompletableFuture<Object>> futures;

        /* Option for 4 blocks is partially hard-coded, unlike for 16+ blocks, because the splitting method is on individual multiplication level.
        This partial hard coding allows for micro-management of distributing the multiplication for more efficient results.
        */
        if(blocks == MIN_BLOCKS){
            switch (nServers){
                case 1:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs1 =
                            {getStub(0), getStub(0), getStub(0), getStub(0), getStub(0), getStub(0), getStub(0), getStub(0)}; // distribution of multiplication across stubs
                    multiplyWithStubSequence(stubs1, C, Ab, Bb);
                    break;
                case 2:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs2 =
                            {getStub(0), getStub(0), getStub(0), getStub(0), getStub(1), getStub(1), getStub(1), getStub(1)};
                    multiplyWithStubSequence(stubs2, C, Ab, Bb);
                    break;
                case 3:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs3 =
                            {getStub(0), getStub(0), getStub(0), getStub(1), getStub(1), getStub(1), getStub(2), getStub(2)};
                    multiplyWithStubSequence(stubs3, C, Ab, Bb);
                    break;
                case 4:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs4 =
                            {getStub(0), getStub(0), getStub(1), getStub(1), getStub(2), getStub(2), getStub(3), getStub(3)};
                    multiplyWithStubSequence(stubs4, C, Ab, Bb);
                    break;
                case 5:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs5 =
                            {getStub(0), getStub(0), getStub(1), getStub(1), getStub(2), getStub(2), getStub(3), getStub(4)};
                    multiplyWithStubSequence(stubs5, C, Ab, Bb);
                    break;
                case 6:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs6 =
                            {getStub(0), getStub(0), getStub(1), getStub(1), getStub(2), getStub(3), getStub(4), getStub(5)};
                    multiplyWithStubSequence(stubs6, C, Ab, Bb);
                    break;
                case 7:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs7 =
                            {getStub(0), getStub(0), getStub(1), getStub(2), getStub(3), getStub(4), getStub(5), getStub(6)};
                    multiplyWithStubSequence(stubs7, C, Ab, Bb);
                    break;
                case 8:
                    MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs8 =
                            {getStub(0), getStub(1), getStub(2), getStub(3), getStub(4), getStub(5), getStub(6), getStub(7)};
                    multiplyWithStubSequence(stubs8, C, Ab, Bb);
            }
        }

        else{
            futures = new ArrayList<>();
            int perServer = rows * rows / nServers;
            int c = 0;
            int currentStub = 0;
            int evenOutStub = 0;
            boolean evenOut = false;
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < rows; j++){
                    if(evenOut){
                        if(++evenOutStub == nServers){
                            evenOutStub = 0;
                        }
                    }
                    if(++c % perServer == 0){
                        currentStub++;
                    }
                    if(currentStub >= nServers){
                        evenOut = true;
                    }
                    int stubNumber = evenOut? evenOutStub : currentStub;
                    futures.add(handler.blockDotProduct(rows, i, j, Ab, Bb, C, getStub(stubNumber)));
                }
            }
            waitForStubs(futures);
        }
    }

    /**
     * the helper method that performs the block multiplication according to the split given by stubList.
     * This method always "thinks" that there are servers.
     * To spread the load across less servers simply repeat the reference to a stub in the array.
     * @param stubList the sequence describing which server does which multiplication
     * @param C result matrix to map to
     * @param Ab Block matrix A
     * @param Bb Block matrix B
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void multiplyWithStubSequence(MatrixServiceGrpc.MatrixServiceBlockingStub[] stubList, Double[][] C, BlockMatrix Ab, BlockMatrix Bb) throws InterruptedException, ExecutionException{
        ArrayList<CompletableFuture<Double[][]>> multResults = new ArrayList<>();
        // 0,0
        multResults.add(handler.multiplyBlock(Ab.getBlock(0,0), Bb.getBlock(0,0), stubList[0]));
        multResults.add(handler.multiplyBlock(Ab.getBlock(0,1), Bb.getBlock(1,0), stubList[1]));
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

    private static void waitForStubs(List<CompletableFuture<Object>> futures) throws InterruptedException, ExecutionException {
        for(CompletableFuture<Object> future: futures){
            future.get();
        }
    }

    private int calculateNumber(int size){
        return (size < SMALLEST_N_BLOCKS_THRESHOLD) ? MIN_BLOCKS : MAX_BLOCKS;
    }

    public void shutDownChannels(){
        for(ManagedChannel channel: channels){
            channel.shutdown();
        }
    }

    // get stub will only instantiate a channel if it's needed to save resources
    private MatrixServiceGrpc.MatrixServiceBlockingStub getStub(int index){
        if(index >= MAX_SERVERS){
            throw new IllegalArgumentException();
        }
        if(index >= stubs.size()-1){
            for(int i = stubs.size(); i <= index; i++){
                channels.add(ManagedChannelBuilder.forAddress(ADDRESS_LIST[channels.size()-1], PORT_LIST[channels.size()-1]).usePlaintext().build());
                stubs.add(MatrixServiceGrpc.newBlockingStub(channels.get(channels.size()-1)));
            }
        }
        return stubs.get(index);
    }
}
