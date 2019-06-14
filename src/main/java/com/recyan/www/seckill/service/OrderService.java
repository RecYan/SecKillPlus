package com.recyan.www.seckill.service;

import com.recyan.www.seckill.dao.OrderDao;
import com.recyan.www.seckill.domain.OrderInfo;
import com.recyan.www.seckill.domain.SeckillOrder;
import com.recyan.www.seckill.domain.SeckillUser;
import com.recyan.www.seckill.redis.RedisService;
import com.recyan.www.seckill.redis.key.OrderKey;
import com.recyan.www.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
	@Autowired
	private OrderDao orderDao;

	@Autowired
	private RedisService redisService;

	public SeckillOrder getSeckillOrderByUserIdGoodsId(Long userId, long goodsId) {

		return redisService.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodsId, SeckillOrder.class);
	}

	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}

	@Transactional
	public OrderInfo createOrder(SeckillUser seckillUser, GoodsVO goods) {

		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateTime(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getSeckillPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(seckillUser.getId());
		orderDao.insert(orderInfo);

		SeckillOrder seckillOrder = new SeckillOrder();
		seckillOrder.setOrderId(orderInfo.getId());
		seckillOrder.setUserId(1L);
		seckillOrder.setGoodsId(goods.getId());
		orderDao.insertSeckillOrder(seckillOrder);

		//秒杀订单写入redis
		redisService.set(OrderKey.getSeckillOrderByUidGid, "" + seckillUser.getId() + "_" + goods.getId(),
				seckillOrder);

		return orderInfo;
	}

	public void deleteOrders() {
		orderDao.deleteOrders();
		orderDao.deleteSeckillOrders();
	}

}
