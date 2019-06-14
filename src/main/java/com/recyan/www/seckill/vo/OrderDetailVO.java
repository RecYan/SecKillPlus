package com.recyan.www.seckill.vo;


import com.recyan.www.seckill.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVO {
    private GoodsVO goods;
    private OrderInfo order;
}
