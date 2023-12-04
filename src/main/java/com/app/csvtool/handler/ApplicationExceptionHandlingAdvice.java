package com.app.csvtool.handler;

import java.util.Optional;

import com.app.csvtool.exception.AppException;
import com.app.csvtool.payload.ExceptionResponse;
import com.app.csvtool.utils.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ApplicationExceptionHandlingAdvice extends ResponseEntityExceptionHandler {

//    @Override
//    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
//                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
//        String error = "Malformed JSON request";
//        return buildResponseEntity(new ExceptionResponse(error), HttpStatus.BAD_REQUEST);
//    }
//
//    // for @Valid failure
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
//        Optional<ObjectError> error = ex.getBindingResult().getAllErrors().stream().findFirst();
//        String errorMessage = error.get().getDefaultMessage();
//        ExceptionResponse responseBody = new ExceptionResponse(errorMessage);
//        return buildResponseEntity(responseBody, status);
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
//                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
//        ExceptionResponse responseBody = new ExceptionResponse(ex.getMessage());
//        return buildResponseEntity(responseBody, status);
//    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleAppException(AppException ex, WebRequest request) {
        ExceptionResponse responseBody;
        if(ex.getCode() != -1) {
            responseBody = new ExceptionResponse(ex.getMessage(), ex.getCode());
        }
        else {
            responseBody = new ExceptionResponse(ex.getMessage(), ex.getStatus().value());
        }

        return buildResponseEntity(responseBody, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occured : {}",ex);
        ExceptionResponse responseBody = new ExceptionResponse(ErrorMessage.SOMETHING_WENT_WRONG + ex.getMessage());
        return buildResponseEntity(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // create response entity
    private ResponseEntity<Object> buildResponseEntity(ExceptionResponse apiExceptionResponse, HttpStatus status) {
        return new ResponseEntity<>(apiExceptionResponse, status);
    }
}

