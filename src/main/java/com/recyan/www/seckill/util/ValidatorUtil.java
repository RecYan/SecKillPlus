package com.recyan.www.seckill.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//手机号码验证 数字1开头1-10的11为数字数字
public class ValidatorUtil {

	private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

	public static boolean isMobile(String src) {
		if (StringUtils.isEmpty(src)) {
			return false;
		}
		Matcher m = mobile_pattern.matcher(src);
		return m.matches();
	}

//	public static void main(String[] args) {
//		System.out.println(isMobile("15573313526"));
//	}
}
