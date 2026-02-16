package com.wc.aspect;

import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.wc.model.R;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(value = ApiException.class)
    public R handlerApiException(ApiException apiException){
        IErrorCode iErrorCode = apiException.getErrorCode();
        if (iErrorCode != null) {
            return R.fail(iErrorCode);
        }
        return  R.fail(apiException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        if(bindingResult.hasErrors()){
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField()+fieldError.getDefaultMessage());
            }
        }
        return   R.fail(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public  R handlerBindException(BindException e){
        BindingResult bindingResult = e.getBindingResult();
        if(bindingResult.hasErrors()){
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField()+fieldError.getDefaultMessage());
            }
        }
        return   R.fail(e.getMessage());
    }
}