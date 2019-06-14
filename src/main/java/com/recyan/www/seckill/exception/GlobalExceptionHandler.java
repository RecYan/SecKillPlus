package com.recyan.www.seckill.exception;

import com.recyan.www.seckill.result.CodeMsg;
import com.recyan.www.seckill.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ControllerAdvice 注解，可以用于定义@ExceptionHandler、@InitBinder、@ModelAttribute，
 *  并应用到所有@XXXMapping中
 */

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

	//@ExceptionHandler用于捕获Controller中抛出的指定类型的异常
	@ExceptionHandler(value = Exception.class)
	public Result<String> exceptionHandler(HttpServletRequest request, Exception e) {
		e.printStackTrace();
		if (e instanceof GlobalException) {
			GlobalException ex = (GlobalException) e;
			return Result.error(ex.getCodeMsg());
		} else if (e instanceof BindException) {
			BindException ex = (BindException) e;
			List<ObjectError> errors = ex.getAllErrors();
			ObjectError error = errors.get(0);
			String msg = error.getDefaultMessage();
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
		} else {
			return Result.error(CodeMsg.SERVER_ERROR);
		}
	}
}
