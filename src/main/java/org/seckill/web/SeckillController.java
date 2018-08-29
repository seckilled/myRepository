package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/14.
 */
@Controller//@Service @Component
@RequestMapping("/seckill")// url:/模块/资源/{id}/细分 /seckill/list
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public  String list(Model model){
        //model就和模型驱动是一个道理，仅仅是为了将list给到jsp页面里去，这里相当于这样是list.jsp + model = ModelAndView.
        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list"; //WEB-INF/jsp/"list".jsp
    }

   @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
       //详情页
       if(seckillId == null){
           return  "redirect:/seckill/list";
       }
       Seckill seckill = seckillService.getById(seckillId);
       if(seckill == null){
           return  "forward:/seckill/list";
       }
       model.addAttribute("seckill",seckill);
       return "detail";
   }

    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.POST,
                    produces = "application/json;charset=UTF-8")//produces的作用是告诉浏览器我们的content TYPE,同时指明编码
    @ResponseBody //将返回数据封装成Json类型  ?json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        //暴露秒杀端口
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
           result =  new SeckillResult<Exposer>(true,exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
       return  result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution",method = RequestMethod.POST,
                    produces = "application/json;charset=UTF-8")
    @ResponseBody
    public  SeckillResult<SeckillExecution> execution(@PathVariable("seckillId") Long seckillId, @CookieValue(value = "killPhone",required = false) Long userPhone, @PathVariable("md5") String md5){
          //执行秒杀


        if(userPhone == null){
            return  new SeckillResult<SeckillExecution>(false,"未注册");
        }
        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution  execution = seckillService.executeSeckillProcedure(seckillId,userPhone,md5);
            /**
             * SeckillExecution
             * 秒杀对象id
             * private long seckillId;
             * 秒杀执行结果状态
             * private  int state;
             * 秒杀结果执行状态信息
             * private  String stateInfo;
             * 秒杀成功对象
             * private SuccessKilled successKilled;
             *
             * SuccessKilled
             * private  long seckillId;
             * private  long userPhone;
             * private  short state;
             * private Date createTime;
             * 多对一
             * private  Seckill seckill;
             */

            result = new SeckillResult<SeckillExecution>(true,execution);
        } catch (SeckillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
            result = new SeckillResult<SeckillExecution>(true,execution);
        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.END);
            result = new SeckillResult<SeckillExecution>(true,execution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.REPEAT_KILL);
            result = new SeckillResult<SeckillExecution>(true,execution);
        }
        return  result;
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        //获取系统时间
        Date now = new Date();
        return  new SeckillResult<Long>(true,now.getTime());
    }

}
