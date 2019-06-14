package com.recyan.www.seckill.controller;

import com.recyan.www.seckill.domain.SeckillUser;
import com.recyan.www.seckill.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

	@GetMapping("/info")
	@ResponseBody
	public Result<SeckillUser> doLogin(SeckillUser seckillUser) {
		return Result.success(seckillUser);
	}
}
