package com.example.grpc.client.grpcclient.exceptions;

public class MatrixTooSmallException extends Exception{
    public MatrixTooSmallException(String message){
        super(message);
    }
    public MatrixTooSmallException(){
        super();
    }
}
