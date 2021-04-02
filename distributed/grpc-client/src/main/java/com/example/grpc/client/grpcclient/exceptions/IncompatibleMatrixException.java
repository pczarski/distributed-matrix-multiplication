package com.example.grpc.client.grpcclient.exceptions;

public class IncompatibleMatrixException extends Exception{
    public  IncompatibleMatrixException(String errorMessage){
        super(errorMessage);
    }
}
