package tech.yiren.ystart.lowcode.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.yiren.ystart.lowcode.dto.ResultBean;
import tech.yiren.ystart.lowcode.exception.bizException.BizException;

//@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResultBean<?> bizExceptionHandler(BizException e) {
        System.out.println("bizExceptionHandler" + e);
        return new ResultBean<>(e);
    }


    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBean<?> exceptionHandler(Exception e) {
        System.out.println("exceptionHandler" + e);
        return new ResultBean<BizException>(e);
    }
}
