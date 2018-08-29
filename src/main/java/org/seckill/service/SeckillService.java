package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 *业务接口:站在使用者角度设计接口
 * 初学者常犯的小问题:关注细节,关注接口方法的实现
 * 这样设计的接口往往非常的冗余
 * 三个方面:方法定义粒度(方法定义明确),参数(简练直接),返回类型(return 类型/异常)
 */
public interface SeckillService {
    /**
     * 查询所有秒杀记录
     * 列表页面展示所有的秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param secillId
     * @return
     */
    Seckill getById(long secillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException,SeckillCloseException,RepeatKillException;
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5)
            throws SeckillException,SeckillCloseException,RepeatKillException;

}
