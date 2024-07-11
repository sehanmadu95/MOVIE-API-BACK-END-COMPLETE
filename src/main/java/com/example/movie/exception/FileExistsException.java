package com.example.movie.exception;


public class FileExistsException extends  RuntimeException{
    public FileExistsException(String msg){
        super(msg);
    }
}
