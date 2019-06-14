package com.recyan.www.seckill.service;

import com.recyan.www.seckill.domain.OrderInfo;
import com.recyan.www.seckill.domain.SeckillOrder;
import com.recyan.www.seckill.domain.SeckillUser;
import com.recyan.www.seckill.redis.RedisService;
import com.recyan.www.seckill.redis.key.SeckillKey;
import com.recyan.www.seckill.util.MD5Util;
import com.recyan.www.seckill.util.UUIDUtil;
import com.recyan.www.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class SeckillService {
	@Autowired
	private GoodsService goodsService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private RedisService redisService;

	//图形验证码运算符
	private static char[] ops = new char[] { '+', '-', '*' };

	@Transactional
	public OrderInfo seckill(SeckillUser seckillUser, GoodsVO goods) {
		// 减库存 下订单 写入秒杀订单
		boolean success = goodsService.reduceStock(goods);

		if (success) {
			// order_info seckill_order
			return orderService.createOrder(seckillUser, goods);
		}
		setGoodsOver(goods.getId());
		return null;

	}

	public long getSeckillResult(Long userId, long goodsId) {

		SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);

		if (seckillOrder != null) {
			// 秒杀成功
			return seckillOrder.getOrderId();
		} else {
			boolean isOver = getGoodsOver(goodsId);
			if (isOver) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(SeckillKey.isGoodsOver, "" + goodsId);
	}

	public void setGoodsOver(Long goodsId) {
		redisService.set(SeckillKey.isGoodsOver, "" + goodsId, true);

	}

	public void reset(List<GoodsVO> goodsVOList) {
		goodsService.resetStock(goodsVOList);
		orderService.deleteOrders();
	}

	//校验路径验证
	public boolean checkPath(SeckillUser seckillUser, long goodsId, String path) {
		if (seckillUser == null || path == null) {
			return false;
		}
		String pathOld = redisService.get(SeckillKey.getSeckillPath, "" + seckillUser.getId() + "_" + goodsId,
				String.class);
		return path.equals(pathOld);
	}

	//生成路径验证
	public String createSeckillPath(SeckillUser seckillUser, long goodsId) {

		String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
		redisService.set(SeckillKey.getSeckillPath, "" + seckillUser.getId() + "_" + goodsId, str);
		return str;
	}

	//生成图形验证码
	public BufferedImage createVerifyCode(SeckillUser seckillUser, long goodsId) {
		if (seckillUser == null || goodsId < 0) {
			return null;
		}
		//设置验证码高度、宽度 与页面保持一致
		int width = 80;
		int height = 32;
		// create the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set the background color and fill it
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border 生成验证码边框
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode + "=", 8, 24);
		g.dispose();
		// 计算验证码值 并存到redis中
		int rnd = calc(verifyCode);
		redisService.set(SeckillKey.getSeckillVerifyCode, seckillUser.getId() + "," + goodsId, rnd);
		// 输出图片
		return image;
	}

	public boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode) {
		if (user == null || goodsId <= 0) {
			return false;
		}
		Integer codeOld = redisService.get(SeckillKey.getSeckillVerifyCode, user.getId() + "," + goodsId,
				Integer.class);
		if (codeOld == null || codeOld - verifyCode != 0) {
			return false;
		}
		redisService.delete(SeckillKey.getSeckillVerifyCode, user.getId() + "," + goodsId);
		return true;
	}

	//使用 ScriptEngine 计算 表达式值
	private static int calc(String exp) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return (Integer) engine.eval(exp);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}



	/**
	 * + - *
	 */
	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
		int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		//随机生成 + - * 运算符
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = "" + num1 + op1 + num2 + op2 + num3;
//		System.out.println(exp);
		return exp;
	}
}
