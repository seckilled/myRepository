package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



/**
 * Created by Administrator on 2018/8/21.
 */
public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
  //引入JedisPool 有点类似于数据库连接池的connectonPool
    private JedisPool jedisPool;
    public  RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }
    //Google的protobuffer需要我们自己写schema,然后转换成一个文件来告诉序列化,但是这样做非常不友好
    //所以protoStuff帮你动态来实现这个过程,而且几乎没损失
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);//获取seckill的字节码
    /**
     * 关于schema的解释
     * seckill.class是拿到类的字节码的对象,通过反射拿到字节码属性方法
     *RuntimeSchema就是基于这个class去做了这样一个模式
     * 当你要去创建一个对象的时候,它会根据一个模式来赋予你相应的值
     * @param seckillId
     * @return
     */
    public Seckill getSeckill(long seckillId){
        //redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();

            try {
               String key = "seckill:" + seckillId;//redis的命名规则
                //并没有实现内部序列化操作
                // get->byte[]->反序列化->Object(Seckill)
                //将一个对象转化成一个字节数组,传到redis中
                //采用自定义序列化
                //protostuff:pojo  有get和set方法的这样一个对象
                //从Jedis中获取序列化的字节数组
              byte[] bytes = jedis.get(key.getBytes());//redis存取
                //从缓存中获取到
                if(bytes != null){
                    //创建一个空对象
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    /**
                     * seckill被反序列化
                     * Protostuff可以根据这个字节数组,seckill这个空对象
                     * 中的数据是根据schema传入的
                     * 这样写比原生的JDK序列化空间,一般情况下可压缩到原来的十分之一到五分之一
                     * 的范围
                     */
                    return seckill;
                }

            }  finally {
                jedis.close();
            }
        }catch (Exception e){

            logger.error(e.getMessage(),e);
        }
        return  null;
    }
    public String putSeckill(Seckill seckill){
        //set Object(Seckill)->序列化->byte[]
      try {
         Jedis jedis = jedisPool.getResource();
          try {
              String key = "seckill:" + seckill.getSeckillId();//?
              /**
               * schema模型存放POJO的结构
               * LinkedBuffer.allocate()创建一个缓存器
               * 当前对象特别大的时候,它会有一个缓冲的过程
               */
              byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
                      LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
              //超时缓存
              //Redis存储数据可以是永久的或是时限的
              //对于数据库缓存来说,一般使用超时机制来保证数据缓存和数据库的完整性
              int timeout = 60 * 60;//缓存一小时
              String result = jedis.setex(key.getBytes(),timeout,bytes);//redis存储
              return  result;
          } finally {
              jedis.close();

          }
      }catch (Exception e){
          logger.error(e.getMessage(),e);

      }
        return null;
    }
}
