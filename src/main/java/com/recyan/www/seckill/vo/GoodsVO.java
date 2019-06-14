package com.recyan.www.seckill.vo;

import com.recyan.www.seckill.domain.Goods;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsVO extends Goods {

	private Double seckillPrice;
	private Integer stockCount;
	private Date startTime;
	private Date endTime;

}
