package com.PayVang.Mobile.CustomExceptions;

//CustomExceptionHandler.java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<String> handleCustomException(UnauthorizedException ex) {
   return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
}

@ExceptionHandler(InvalidRequestException.class)
public ResponseEntity<String> handleCustomException(InvalidRequestException ex) {
   return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(InternalServerException.class)
public ResponseEntity<String> handleCustomException(InternalServerException ex) {
   return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
}

// Add more exception handlers as needed for other custom exceptions
}
