-- 数据库初始化脚本
-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
USE seckill;
-- 创建秒杀库存表
CREATE TABLE seckill(
  seckill_id bigint not null AUTO_INCREMENT COMMENT  '商品库存id',
  name VARCHAR(120) NOT NULL COMMENT '商品名称',
  number INT NOT NULL COMMENT '库存数量',
  start_time TIMESTAMP NOT NULL COMMENT '秒杀时间开始',
  end_time TIMESTAMP NOT NULL COMMENT '秒杀时间结束',
 create_time TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY(seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT  CHARSET=utf8 COMMENT='秒杀库存表'

-- 初始化数据
insert into
  seckill(name,number,start_time,end_time,create_time)
values
('1000元秒杀iphone7',100,'2018-8-1 00:00:00','2018-11-2 00:00:00',now()),
('200元秒杀小米5' , 100 , '2015-11-01 00:00:00' , '2015-11-02 00:00:00',now()),
('300元秒杀iphone6s' , 100 , '2015-11-01 00:00:00' , '2015-11-02 00:00:00',now());

-- 秒杀成功明细
-- 用户登录认证信息
CREATE TABLE success_seckilled(
 seckill_id BIGINT NOT NULL COMMENT '商品库存id',
  user_phone VARCHAR(11) NOT NULL COMMENT '用户电话',
  state TINYINT NOT NULL DEFAULT 0 COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  create_time TIMESTAMP  NOT NULL COMMENT '创建时间',
  PRIMARY KEY(seckill_id,user_phone),/*联合主键*/
  key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细'