
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <%@include file="common/head.jsp"%><!--JSP的静态包含方法-->
</head>
<body>
<div class="container">
    <div class="panel panel-default text-center">
        <div class="panel-heading ">
           <h1>${seckill.name}</h1><!--detail()中model.addAttribute("seckill",seckill);-->
        </div>
        <div class="panel-body">
            <h2 class="text-danger">
                  <!--?显示time图标-->
                <span class="glyphicon glyphicon-time"></span><!--glyhicon是字体图标-->
                <!--?显示倒计时-->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
</div>
<!--登录弹出层,输入电话-->
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>
                </h3>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <!--input标签下各属性的含义?-->
                        <input type="text" name="killPhone" id="killPhoneKey"
                          placeholder="填手机号^^" class="form-control"/>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <!-- 验证信息 -->
                <span id="killPhoneMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                      <span class="glyphicon glyphicon-phone"></span>
                       Submit
                </button>
            </div>
        </div>
    </div>
</div>

</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<!--使用CDN获取公共js http://www.bootcdn.cn/-->
<!--jquery的cookie插件-->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!--jquery的计时插件-->
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>

<!--开始编写交互逻辑-->
<script src="/resources/script/seckill.js"type="text/javascript"></script>
<script type="text/javascript">
    $(function(){
        //使用EL表达式传入参数
        seckill.detail.init({
             seckillId:${seckill.seckillId},
             startTime:${seckill.startTime.time},//毫秒
             endTime:${seckill.endTime.time}
       });
    });
</script>
</html>

