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
 * Create By ChanJi on 2018/1/30
 *
 */
@Controller //@Service @Component 将controller放入容器当中
@RequestMapping("/seckill") // /url/模块/资源/{id}/细分  /seckill/list
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    /**
     * 获得商品列表
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        //获取列表页
        List<Seckill> list = seckillService.getSeckill();
        logger.info("listAll:", list);
        model.addAttribute("list", list);
        //list.jsp +model = ModelView
        return "list"; // /WEB-INF/jsp/"list".jsp
    }

    /**
     * 获得商品秒杀详情
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckilled}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckilled") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     * 获得秒杀暴露接口
     * @param seckillId
     * @return
     */
    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}) //设置conentTpye和编码
    @ResponseBody //返回json类型的数据
    public SeckillResult exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    /**
     * 执行秒杀
     * @param seckillId
     * @param md5
     * @param phone
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone) {
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        try {
            //调用存储过程
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            return new SeckillResult(true, execution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_REEOR);
            return new SeckillResult(true, execution);
        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult(true, execution);
        } catch (SeckillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return  new SeckillResult(true, execution);
        }

    }


    /**
     * 获得时间
     * @return
     */
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now  = new Date();
        return  new SeckillResult(true,now.getTime());
    }
}