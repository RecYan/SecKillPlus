package com.recyan.www.seckill.controller;

import com.recyan.www.seckill.result.Result;
import com.recyan.www.seckill.service.SeckillUserService;
import com.recyan.www.seckill.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
	@Autowired
	private SeckillUserService seckillUserService;

	/**
	 * @Valid 校验loginVO[mobile、password]
	 * @param response
	 * @param loginVO
	 * @return
	 */
	@PostMapping("/do_login")
	@ResponseBody
	public Result<String> doLogin(HttpServletResponse response, @Valid LoginVO loginVO) {
		log.info("[用户登录]" + loginVO.toString());

		// 登录
		String token = seckillUserService.login(response, loginVO);
		return Result.success(token);
	}
}
