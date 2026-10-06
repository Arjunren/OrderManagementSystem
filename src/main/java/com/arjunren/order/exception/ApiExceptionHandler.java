package com.arjunren.order.exception;

import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(DomainException.class) ResponseEntity<?> domain(DomainException e){return error(e.getStatus(),"REQUEST_ERROR",e.getMessage(),null);}
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){Map<String,String> fields=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->fields.putIfAbsent(x.getField(),x.getDefaultMessage()));return error(HttpStatus.UNPROCESSABLE_ENTITY,"VALIDATION_ERROR","Invalid request data",fields);}
    @ExceptionHandler(AccessDeniedException.class) ResponseEntity<?> denied(){return error(HttpStatus.FORBIDDEN,"FORBIDDEN","Insufficient permissions",null);}
    @ExceptionHandler(Exception.class) ResponseEntity<?> unknown(Exception e){return error(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_ERROR","An internal error occurred",null);}
    private ResponseEntity<?> error(HttpStatus status,String code,String message,Object fields){Map<String,Object> body=new LinkedHashMap<>();body.put("code",code);body.put("message",message);if(fields!=null)body.put("fields",fields);return ResponseEntity.status(status).body(Map.of("error",body));}
}

