package com.recyan.www.seckill.service;

import com.recyan.www.seckill.dao.SeckillUserDao;
import com.recyan.www.seckill.domain.SeckillUser;
import com.recyan.www.seckill.exception.GlobalException;
import com.recyan.www.seckill.redis.RedisService;
import com.recyan.www.seckill.redis.key.SeckillUserKey;
import com.recyan.www.seckill.result.CodeMsg;
import com.recyan.www.seckill.util.MD5Util;
import com.recyan.www.seckill.util.UUIDUtil;
import com.recyan.www.seckill.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * service只能调service service只能调自己的dao
 */
@Service
public class SeckillUserService {

	public static final String COOKIE_NAME_TOKEN = "token";

	@Autowired
	private SeckillUserDao seckillUserDao;

	@Autowired
	private RedisService redisService;

	public SeckillUser getById(long id) {
		// 取缓存
		SeckillUser user = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
		if (user != null) {
			return user;
		}

		// 取数据库
		user = seckillUserDao.getByid(id);
		if (user != null) {
			redisService.set(SeckillUserKey.getById, "" + id, user);
		}
		return user;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updatePassword(String token, long id, String passwordNew) {
		// 取user
		SeckillUser user = getById(id);
		if (user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		// 更新数据库
		SeckillUser toUpdateUpdate = new SeckillUser();
		toUpdateUpdate.setId(id);
		toUpdateUpdate.setPassword(MD5Util.FormPassToDBPass(passwordNew, user.getSalt()));
		seckillUserDao.update(toUpdateUpdate);
		// 处理缓存
		redisService.delete(SeckillUserKey.getById, "" + id);
		user.setPassword(toUpdateUpdate.getPassword());
		redisService.set(SeckillUserKey.token, token, user);
		return true;
	}

	/**登录验证
	 * 验证手机号、密码
	 *  生成用户token并写入redis
	 * @param response
	 * @param loginVO
	 * @return
	 */
	public String login(HttpServletResponse response, LoginVO loginVO) {
		if (loginVO == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		//取手机号码和登录密码
		String mobile = loginVO.getMobile();
		String formPass = loginVO.getPassword();

		// 判断手机号是否存在
		SeckillUser user = getById(Long.parseLong(mobile));
		if (user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}

		// 验证密码
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		//手动计算第二次加密
		String calcPass = MD5Util.FormPassToDBPass(formPass, saltDB);
		if (!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		String token = UUIDUtil.uuid();
		// 生成cookie
		addCookie(response, token, user);
		return token;
	}

	//根据token标识再去redis中取用户
	public SeckillUser getByToken(HttpServletResponse response, String token) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		SeckillUser user = redisService.get(SeckillUserKey.token, token, SeckillUser.class);
		if (user != null) {
			// 延迟有效时间
			addCookie(response, token, user);
		}

		return user;
	}

	//生成用户唯一token
	private void addCookie(HttpServletResponse response, String token, SeckillUser user) {

		Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
		//token: [tk:user]
		redisService.set(SeckillUserKey.token, token, user);
		cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
