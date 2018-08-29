package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.xml.ws.soap.Addressing;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/10.
 */
//@Component @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //注入service依赖
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;
    //Md5加密算法的盐值
    private String slat = "dfdafdsfdsadf3243adx%34";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 7);
    }

    public Seckill getById(long secillId) {
        return seckillDao.queryById(secillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        /**
         * 优化点:缓存优化
         * get from cache
         * if null
         *  put cache
         * else
         *  get cache
         */
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            seckill = seckillDao.queryById(seckillId);//获取seckill对象是为了获取对象的StartTime和EndTime属性
            if (seckill == null) {
                return new Exposer(false, seckillId);
            }else {
                redisDao.putSeckill(seckill);
            }

        }
     //   logger.info("seckill={}",seckill);

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime()
                    , endTime.getTime());
        }
        //转化特定字符串的过程,不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, SeckillCloseException, RepeatKillException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑:记录购买行为+减库存
        Date nowTime = new Date();


        try {
            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeate");
            } else {
                //秒杀成功
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                // SuccessKilled queryByIdWithSeckill = successKilledDao.queryByIdWithSeckill(1000L);
                // <select id="queryByIdWithSeckill" resultType="org.seckill.entity.SuccessKilled">
                //减库存  update的逻辑:只要执行秒杀且系统当前时间在开始时间和结束时间之间 就会减库存
                /**SuccessKilled
                 *    private  long seckillId;
                 private  long userPhone;
                 private  short state;
                 private Date createTime;
                 */
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录,秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                }

                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);

            }

        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常,转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5)  {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
             return  new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object>  map = new HashMap<String, Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程,result被赋值
        try {
            seckillDao.killByProcedure(map);
            //获取result
           int result = MapUtils.getInteger(map,"result",-2);
            if(result == 1){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,sk);
            }else {
                return  new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
          logger.error(e.getMessage(),e);
            return  new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }

    }
}


