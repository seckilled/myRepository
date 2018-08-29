//存放主要交互逻辑js代码
//JavaScript 模块化 java语言中典型的就是分包
//seckill.detail.int(params);
//package.class.method(params);
var seckill={
    //封装秒杀相关ajax的地址URL
    URL:{
       now:function(){
           return '/seckill/time/now';
       },
        exposer:function(seckillId){
            return '/seckill/'+seckillId+'/exposer';
        },
        execution:function(seckillId,md5){
            return '/seckill/'+seckillId+'/'+ md5+'/execution';
        }
    },
    //验证手机号
    validatePhone :function(phone){
        if (phone && phone.length == 11 && !isNaN(phone)){//!isNaN(phone)表示phone为数字
            return true;
        }else {
            return false;
        }
    },
    handlerSeckill:function(seckillId,node){
        //获取秒杀地址,控制显示逻辑,执行秒杀
        node.hide()
        .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>')//按钮 用js方法封装bootstrap按钮
        $.post(seckill.URL.exposer(seckillId),{},function(result){
              //function是post返回成功后的回调,在回调函数中执行交互流程,这里的result是controller exposer方法返回封装的
            //json数据  //?内部原理
            if(result && result['success']){
                var  exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    //获取秒杀地址  这段逻辑不是很清晰???
                    var  md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId,md5);
                    console.log("killUrl:"+killUrl);
                    //绑定一次点击事件,防止用户重复click
                    $('#killBtn').one('click',function(){
                        //执行秒杀请求操作
                        //1:先禁用按钮
                        $(this).addClass('disabled');
                        //2.发送秒杀请求执行这个秒杀
                        $.post(killUrl,{},function(result){

                            if(result && result['success']){

                                var  killResult = result['data'];
                                var  state =killResult['state'];
                                var stateInfo =killResult['stateInfo'];
                                console.log(stateInfo);
                                //3:显示秒杀结果
                               node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }


                        })
                    });
                    node.show();

                }else {
                    //未开启秒杀 从exposer中去获取数据
                    var  now = exposer['now'];
                    var  start = exposer['start'];
                    var  end = exposer['end'];
                    //再执行倒计时功能
                    seckill.countdown(seckillId,now,start,end)
                }
            }else {
                console.log("result =" + result);
            }
        });
    },
    //计时交互函数,为什么将其封装成一个单独的函数?
    //1.将逻辑统一抽取出来放在一个方法里面,降低init()方法的代码量
    //2.这个函数到了后面会重用
    countdown:function(seckillId,nowTime,startTime,endTime){
        var seckillBox = $('#seckill-box');//js中获取id的一种方法,在倒计时span面板中显示 其id=seckill-box
        //时间判断
        if(nowTime > endTime){
            //秒杀结束
            seckillBox.html('秒杀结束!');
        }else if(nowTime < startTime){
            //秒杀未开始,计时时间绑定
            var killTime = new Date(startTime+1000);//killTime是新建的js的日期类型,传入startTime,加一秒是因为客户端的计时服务,防止时间的偏移
            seckillBox.countdown(killTime,function(event){
                //设置时间格式
                var  format = event.strftime('秒杀倒计时:%D天  %H时  %M分 %S秒');
                seckillBox.html(format);
                //时间完成后,回调事件
            }).on('finish.countdown',function(){
                //获取秒杀地址,控制显示逻辑,执行秒杀
                seckill.handlerSeckill(seckillId,seckillBox);
            });
        }else {
            //秒杀开始
            seckill.handlerSeckill(seckillId,seckillBox);

        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证和登录,计时交互
            //规划交互流程
            //cookie中phone数据
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //验证手机号
            if(!seckill.validatePhone(killPhone)){
                //绑定phone
                //控制输出
                var killPhoneModal = $('#killPhoneModal');
                //显示弹出层
                killPhoneModal.modal({
                    show:true,//显示弹出层
                    backdrop:'static',//禁止位置关闭
                    keyboard:false,//禁止键盘关闭
                });
               $('#killPhoneBtn').click(function(){
                   var inputPhone = $('#killPhoneKey').val();
                   if(seckill.validatePhone(inputPhone)){
                       //电话写入cookie
                       $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                       //刷新页面
                       window.location.reload();
                   }else {
                       $('#killPhoneMessage').hide().html('<lable class="lable lable-danger">手机号错误!</lable>').show(300);
                   }
               });
            }
            //已经登录
            //计时交互
            $.get(seckill.URL.now(),{},function(result){
             //给出参数,给出回调函数function,将回调的结果当做参数传递到这个方法中
                 if(result && result['success']){
                     var nowTime = result['data'];
                     //时间判断,统一封装到一个countdown函数中做处理
                     seckill.countdown(seckillId,nowTime,startTime,endTime);
                 }else {
                     console.log('result:'+result);
                 }
            })
        }
    }
}
