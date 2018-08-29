package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/8/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
    private long id = 1000;
    @Autowired
    private  RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;
    @Test
    public void testSeckill() throws Exception {
        //get and put
        Seckill seckill = redisDao.getSeckill(id);
        if(seckill == null){
            seckill = seckillDao.queryById(id);//返回一个seckill秒杀对象
            if(seckill != null){
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                 seckill =redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }

}