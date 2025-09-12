package com.s3.common.exception;


import com.s3.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {

        return ResponseEntity.badRequest().body(
                new ErrorResponse(ErrorCode.INVALID_INPUT, ex.getMessage(), Instant.now())
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException (ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ErrorCode.NOT_FOUND, ex.getMessage(), Instant.now()));

        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericError(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), Instant.now()));
        }
    }




