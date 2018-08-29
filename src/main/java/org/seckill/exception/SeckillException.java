package org.seckill.exception;

/**秒杀业务相关的异常
 * Created by Administrator on 2018/8/10.
 */
public class SeckillException extends  RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
