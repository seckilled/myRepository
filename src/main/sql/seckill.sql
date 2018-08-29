-- 秒杀执行存储过程
DELIMITER $$ -- 默认为;,转换成$$,作用:告诉mysql解释器,执行分隔符为$$
-- 定义存储过程
-- param:in 输入参数 ; out 输出参数
-- 'seckill'.'execute_seckill'->存储过程名
-- declare表示在begin中定义的数据块,只在begin中其作用
-- row_count():返回上一条修改类型sql(delete,update,insert)的修改行数
-- row_count():0:未修改数据;>0:修改的行数;<0:sql错误
-- SUCCESS(1,"秒杀成功"), END(0,"秒杀结束"),REPEAT_KILL(-1,"重复秒杀"),INNER_ERROR(-2,"系统异常"),DATA_REWRITE(-3,"数据篡改");
-- 状态标识:-1:无效 0:成功 1:已付款 2:已发货',

DROP PROCEDURE IF EXISTS execute_seckill;



-- 秒杀执行存储过程

DELIMITER $$ -- console ";" 转换为 "$$"



-- 定义存储过程

-- 参数：in 输入参数; out 输出参数

CREATE PROCEDURE `seckill`.`execute_seckill`(

  IN v_seckill_id BIGINT ,

  IN v_phone BIGINT ,

  IN v_kill_time TIMESTAMP ,

  OUT r_result INT

)

  BEGIN

    -- insert_count 临时变量定义(需要在事物开始之前定义）

    DECLARE insert_count INT DEFAULT 0 ;

    START TRANSACTION ;

    INSERT IGNORE INTO success_killed(

    seckill_id ,

    user_phone ,

    create_time

    ) VALUES (

      v_seckill_id ,

      v_phone ,

      v_kill_time

    ) ;

    -- row_count():返回上一条修改类型sql(delete,insert,upodate)的影响行数

    -- row_count: 0:未修改数据; >0:表示修改的行数; <0:sql错误/未执行修改sql

    SELECT ROW_COUNT() INTO insert_count ;

    IF (insert_count = 0) THEN

      ROLLBACK ;

      -- 设置满足业务逻辑需要的返回结果 r_result

      SET r_result = - 1 ;

    ELSEIF (insert_count < 0) THEN

      ROLLBACK ;

      SET r_result = - 2 ;

    ELSE

      UPDATE seckill

      SET number = number - 1

      WHERE

        seckill_id = v_seckill_id

        AND end_time > v_kill_time

        AND start_time < v_kill_time

        AND number > 0 ;

      -- 再次使用 row_count()

      SELECT ROW_COUNT() INTO insert_count ;

      IF (insert_count = 0) THEN

        ROLLBACK ;

        SET r_result = 0 ;

      ELSEIF (insert_count < 0) THEN

        ROLLBACK ;

        SET r_result = - 2 ;

      ELSE

        COMMIT ;

        SET r_result = 1 ;

      END IF ;

    END IF ;

  END ;

$$

-- 代表存储过程定义结束


DELIMITER ;
SET @r_result = -3;
-- 执行存储过程
call execute_seckill(1003,12345678900,now(),@r_result);

-- 获取结果
SELECT  @r_result;