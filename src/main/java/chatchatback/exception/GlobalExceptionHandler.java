package chatchatback.exception;

import chatchatback.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *  全局异常处理器
 * */

@Slf4j
@RestControllerAdvice //@ResponseBody + @ControllerAdvice
public class GlobalExceptionHandler {

    //捕获所有异常
    @ExceptionHandler
    public Result doException(Exception e) {
        log.error("程序出错了~",e);
        return Result.error("出错了，联系管理员~");
    }

    //捕获重复异常,通俗语句
    @ExceptionHandler
    public Result handleDuplicateKeyExcepiton(DuplicateKeyException e) {
        log.error("程序出错了",e);
        String message = e.getMessage();
        int i = message.indexOf("Duplicate entry");
        String errMsg = message.substring(i);
        String[] arr = errMsg.split(" ");
        return Result.error(arr[2] + "已存在");
    }

    //捕获自定义异常
    @ExceptionHandler
    public Result doIllegalException(illegalException e) {
        log.error("程序出错了~",e);
        return Result.error(e.getMessage());
    }

    //捕获参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("参数错误");
        return Result.error(errorMessage);
    }
}
