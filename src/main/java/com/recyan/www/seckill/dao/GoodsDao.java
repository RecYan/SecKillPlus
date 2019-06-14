package com.recyan.www.seckill.dao;

import java.util.List;

import com.recyan.www.seckill.domain.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.recyan.www.seckill.vo.GoodsVO;

@Mapper
public interface GoodsDao {

    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_time, sg.end_time from seckill_goods sg left join goods g on sg.goods_id=g.id")
    List<GoodsVO> listGoodsVO();

    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_time, sg.end_time from seckill_goods sg left join goods g on sg.goods_id=g.id where g.id=#{goodsId}")
    GoodsVO getGoodsVOByGoodsId(@Param("goodsId") long goodsId);

    @Update("update seckill_goods set stock_count=stock_count-1 where goods_id=#{goodsId} and stock_count > 0")
    int reduceStock(SeckillGoods g);

    @Update("update seckill_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    int resetStock(SeckillGoods g);
}
