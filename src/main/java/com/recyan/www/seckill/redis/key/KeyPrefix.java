package com.recyan.www.seckill.redis.key;

public interface KeyPrefix {
	
	public int expireSeconds();

	public String getPrefix();
}
