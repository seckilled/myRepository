package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Created by Administrator on 2018/8/8.
 */
public interface SuccessKilledDao {
    /**
     * 插入秒杀成功明细 根据秒杀对象Id和用户电话
     * @param seckillId
     * @param userPhone
     * @return
     */
   int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

    /**
     * 根据id查询秒杀成功明细并携带秒杀对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}
