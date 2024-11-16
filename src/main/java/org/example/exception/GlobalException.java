package org.example.exception;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.example.entity.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result sqlHandler(DataAccessException e) {
      log.info(e.getLocalizedMessage());
      return Result.error("操作异常,请联系管理员");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgumentNotValid(MethodArgumentNotValidException e) {
        log.info(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return Result.error(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result handleInvalidJson(HttpMessageNotReadableException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        log.info(ex.getLocalizedMessage());
        return Result.error("Invalid JSON format");
    }

    @ExceptionHandler(TimeoutException.class)
    public Result handleTimeout(TimeoutException e) {
        log.info(e.getLocalizedMessage());
        return Result.error("请求超时");
    }

    @ExceptionHandler(InterruptedException.class)
    public Result handleInterrupted(InterruptedException e) {
        log.info(e.getLocalizedMessage());
        return Result.error("系统错误");
    }

    @ExceptionHandler(ExecutionException.class)
    public Result handleExecution(ExecutionException e) {
        log.info(e.getLocalizedMessage());
        return Result.error("系统错误");
    }

}
