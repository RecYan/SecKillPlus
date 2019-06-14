package com.recyan.www.seckill.access;

import com.alibaba.fastjson.JSON;
import com.recyan.www.seckill.domain.SeckillUser;
import com.recyan.www.seckill.redis.AccessKey;
import com.recyan.www.seckill.redis.RedisService;
import com.recyan.www.seckill.result.CodeMsg;
import com.recyan.www.seckill.result.Result;
import com.recyan.www.seckill.service.SeckillUserService;
import com.recyan.www.seckill.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private SeckillUserService userService;

	@Autowired
	private RedisService redisService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			SeckillUser seckillUser = getUser(request, response);
			UserContext.setUser(seckillUser);

			HandlerMethod method = (HandlerMethod) handler;
			AccessLimit accessLimit = method.getMethodAnnotation(AccessLimit.class);
			if (accessLimit == null) {
				return true;
			}
			//在seconds时间内最多点击maxCount次
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			boolean needLogin = accessLimit.needLogin();
			String key = request.getRequestURI();
			if (needLogin) {
				if (seckillUser == null) {
					render(response, CodeMsg.SERVER_ERROR);
					return false;
				}
				key += "_" + seckillUser.getId();
			}

			// 《改善既有代码的设计》
			AccessKey accessKey = AccessKey.withExpire(seconds);
			// 查询访问的次数
			Integer count = redisService.get(accessKey, key, Integer.class);
			if (count == null) {
				redisService.set(accessKey, key, 1);
			} else if (count < maxCount) {
				redisService.incr(accessKey, key);
			} else {
				render(response, CodeMsg.ACCESS_LIMIT_REACHED);
				return false;
			}
		}

		return true;
	}

	//展示错误信息
	private void render(HttpServletResponse response, CodeMsg serverError) throws IOException {
		//让浏览器用utf-8来解析返回的json数据
		response.setContentType("application/json;charset=UTF-8");
		OutputStream outputStream = response.getOutputStream();
		String str = JSON.toJSONString(Result.error(serverError));
		outputStream.write(str.getBytes("UTF-8"));
		outputStream.flush();
		outputStream.close();
	}

	//加载用户
	private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {

		//兼容手机端 请求带cookie
		String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
		//客户端访问服务端自带的cookie [token=XXXXXXXX]
		String cookieToken = CookieUtil.getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);

		if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		//优先级 先取参数中cookie再去客户端上传的cookie
		String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
		return userService.getByToken(response, token);
	}
}
