package com.recyan.www.seckill.access;

import com.recyan.www.seckill.domain.SeckillUser;

// ThreadLocal 保存当前线程秒杀用户信息
public class UserContext {
    private static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<>();

    public static void setUser(SeckillUser user) {
        userHolder.set(user);
    }

    public static SeckillUser getUser() {
        return userHolder.get();
    }
}
