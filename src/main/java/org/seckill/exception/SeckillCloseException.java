package org.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by Administrator on 2018/8/10.
 */
public class SeckillCloseException extends  RuntimeException{
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
