package com.recyan.www.seckill.controller;

import com.recyan.www.seckill.domain.OrderInfo;
import com.recyan.www.seckill.domain.SeckillUser;
import com.recyan.www.seckill.result.CodeMsg;
import com.recyan.www.seckill.result.Result;
import com.recyan.www.seckill.service.GoodsService;
import com.recyan.www.seckill.service.OrderService;
import com.recyan.www.seckill.vo.GoodsVO;
import com.recyan.www.seckill.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private GoodsService goodsService;

	@GetMapping("/detail")
	public Result<OrderDetailVO> seckill(SeckillUser seckillUser, @RequestParam("orderId") long orderId) {
		if (seckillUser == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}

		OrderInfo order = orderService.getOrderById(orderId);
		if (order == null) {
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}

		long goodsId = order.getGoodsId();
		GoodsVO goodsVO = goodsService.getGoodsVOById(goodsId);
		OrderDetailVO orderDetailVO = new OrderDetailVO();
		orderDetailVO.setOrder(order);
		orderDetailVO.setGoods(goodsVO);

		return Result.success(orderDetailVO);
	}
}
