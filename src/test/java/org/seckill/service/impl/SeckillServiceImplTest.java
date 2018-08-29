package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/8/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)//启动Junit时加载springIOC容器
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})//告诉Junit spring配置文件位置
public class SeckillServiceImplTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired  //自动注入依赖类对象
    private SeckillService seckillService;
    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill>  list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void testGetById() throws Exception {
             long id = 1000;
             Seckill seckill = seckillService.getById(id);
             logger.info("seckill = {}",seckill);
    }

    @Test
    public void testExportSeckillUrl() throws Exception {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer ={}",exposer);
    }

    @Test
    public void testExecuteSeckill() throws Exception {
            long id = 1001;
            long phone = 9432808123L;
            String md5 = "3ecbb3fabce2bf6684748e5520b5c6c7";
            System.out.print(md5);

        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
            logger.info("result={}",seckillExecution);
        } catch (SeckillCloseException e) {
            e.printStackTrace();
        } catch (RepeatKillException e) {
            e.printStackTrace();
        }
        //出现过的异常:SeckillException: seckill inner error:nested exception is org.apache.ibatis.exceptions.
        //打印结果:result=SeckillExecution{seckillId=1000, state=1, stateInfo='秒杀成功',
        // successKilled=SuccessKilled{seckillId=1000, userPhone=1699878697, state=0, create_time=Mon Aug 13 18:24:50 CST 2018}}

    }
    @Test
    public  void  executeSeckillProcedure(){
        long seckillId = 1001;
        long phone = 12345467843l;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
           SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            logger.info(execution.getStateInfo());
        }
    }
}