package com.concur.swbservice.service.component;

import com.concur.swbservice.api.model.v1_0.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class ExceptionLoggerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ExceptionLoggerAdvice.class);

    @ExceptionHandler(value = {HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class})
    @ResponseBody
    public ResponseEntity<ErrorResponse> invalidRequest(HttpServletRequest req, Exception ex) {
        String message;
        if (ex.getCause() instanceof JsonProcessingException) {
            message = ((JsonProcessingException) ex.getCause()).getOriginalMessage();
        } else {
            message = ex.getMessage();
        }
        return new ErrorResponseEntity(HttpStatus.BAD_REQUEST, req, message);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<ErrorResponse> invalidRequest(HttpServletRequest req, MethodArgumentNotValidException ex) {
        String[] errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(objectError -> {
                            if (objectError instanceof FieldError) {
                                FieldError f = (FieldError) objectError;
                                return f.getField() + " " + objectError.getDefaultMessage();
                            } else {
                                return objectError.getObjectName() + " " + objectError.getDefaultMessage();
                            }
                        }
                )
                .toArray(String[]::new);
        return new ErrorResponseEntity(HttpStatus.BAD_REQUEST, req, errors);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseBody
    public ResponseEntity<ErrorResponse> invalidRequest(HttpServletRequest req, ConstraintViolationException ex) {
        String[] errors = ex.getConstraintViolations()
                .stream()
                .map(constraintViolation -> {

                    Path.MethodNode methodNode = null;
                    Path.ParameterNode parameterNode = null;
                    Path.PropertyNode propertyNode = null;

                    for (Path.Node node : constraintViolation.getPropertyPath()) {
                        switch (node.getKind()) {
                            case METHOD:
                                methodNode = (Path.MethodNode) node;
                                break;
                            case PARAMETER:
                                parameterNode = (Path.ParameterNode) node;
                                break;
                            case PROPERTY:
                                propertyNode = (Path.PropertyNode) node;
                        }
                    }

                    String invalidParam = "A value";

                    if (propertyNode != null) {
                        invalidParam = propertyNode.getName();

                    } else if (methodNode != null && parameterNode != null) {
                        try {

                            List<Class<?>> params = methodNode.getParameterTypes();
                            Method method = constraintViolation.getRootBeanClass().getMethod(
                                    methodNode.getName(),
                                    params.toArray(new Class<?>[params.size()]));

                            invalidParam = findValue(method.getParameterAnnotations()[parameterNode.getParameterIndex()]);

                        } catch (NoSuchMethodException e) {
                            // Leave as "A value"
                        }
                    }

                    return invalidParam + " " + constraintViolation.getMessage();
                })
                .toArray(String[]::new);

        return new ErrorResponseEntity(HttpStatus.BAD_REQUEST, req, errors);
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> hystrixExceptions(HttpServletRequest req, HystrixRuntimeException ex) {

        Throwable cause = ex.getCause();

        if (ex.getFailureType() == HystrixRuntimeException.FailureType.SHORTCIRCUIT) {
            String message = "Circuit Breaker for " + req.getRequestURI() + " is open, please try again on a different node";
            log.error(message);
            return new ErrorResponseEntity(HttpStatus.SERVICE_UNAVAILABLE, req, cause, message);
        }

        String message = "Error whilst processing request:" + req.getRequestURI();
        log.error(message, ex);
        return new ErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, req, cause, message);
    }

    private String findValue(Annotation[] parameterAnnotations) {

        for (Annotation annotation : parameterAnnotations) {
            if (annotation.annotationType().equals(RequestParam.class)) {
                return ((RequestParam) annotation).value();
            }
            if (annotation.annotationType().equals(RequestHeader.class)) {
                return ((RequestHeader) annotation).value();
            }
            if (annotation.annotationType().equals(PathVariable.class)) {
                return ((PathVariable) annotation).value();
            }
        }
        return "A value";
    }


    private class ErrorResponseEntity extends ResponseEntity<ErrorResponse> {

        public ErrorResponseEntity(HttpStatus httpStatus, HttpServletRequest httpServletRequest, Throwable exception, String... errors) {
            super(new ErrorResponse(httpStatus, httpServletRequest, exception, errors), httpStatus);
        }

        public ErrorResponseEntity(HttpStatus httpStatus, HttpServletRequest httpServletRequest, String... errors) {
            super(new ErrorResponse(httpStatus, httpServletRequest, errors), httpStatus);
        }
    }
}
