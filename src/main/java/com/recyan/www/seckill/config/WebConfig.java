package com.recyan.www.seckill.config;

import com.recyan.www.seckill.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private UserArgumentResolver userArgumentResolver;

	@SuppressWarnings("unused")
	@Autowired
	private LoginInterceptor loginInterceptor;

	@Autowired
	private AccessInterceptor accessInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

		argumentResolvers.add(userArgumentResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// registry.addInterceptor(loginInterceptor);
		registry.addInterceptor(accessInterceptor);
	}

}
