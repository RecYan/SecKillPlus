package com.recyan.www.seckill.redis;

import com.recyan.www.seckill.redis.key.BasePrefix;

public class AccessKey extends BasePrefix {
	private AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static AccessKey access = new AccessKey(5, "access");

	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}
}
