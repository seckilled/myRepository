package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/8/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
   @Resource
   private  SuccessKilledDao successKilledDao;
    @Test
    public void testInsertSuccessKilled() throws Exception {
        int insertSuccessKilled = successKilledDao.insertSuccessKilled(1000L,18349266251L);
        System.out.println(insertSuccessKilled);
    }

    /**
     * SuccessKilled{seckillId=1000, userPhone=18349266251, state=0, create_time=Fri Aug 10 08:57:17 CST 2018}
       Seckill{seckillId=1000, name='1000元秒杀iphone7', number=0, current_time=null, start_time=Thu Nov 01 00:00:00 CST 2018, end_time=Fri Nov 02 00:00:00 CST 2018}
     * @throws Exception
     */
    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        SuccessKilled queryByIdWithSeckill = successKilledDao.queryByIdWithSeckill(1000L,18349266251L);
        System.out.println(queryByIdWithSeckill);
        System.out.println(queryByIdWithSeckill.getSeckill());
    }
}