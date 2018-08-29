package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/8.
 */
public interface SeckillDao {
    /**
     * 减库存
     * @param seckillId
     * @param killTime 相当于数据库表中的create_time字段
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);

    /**
     * 通过Id来查询秒杀对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     *通过偏移量查询所有秒杀对象
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
    /**
     * 使用存储过程来执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String,Object>paramMap);
}
