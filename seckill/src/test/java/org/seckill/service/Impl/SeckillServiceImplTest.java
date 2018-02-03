package org.seckill.service.Impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Create By ChanJi on 2018/1/28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceImplTest {
    private final Logger logger = LoggerFactory.getLogger((this.getClass()));

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckill() throws Exception {
        List<Seckill> list = seckillService.getSeckill();
        logger.info("List={}", list);

    }

    @Test
    public void getById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    //集成测试代码逻辑的完整性，注意可重复执行
    public void exportSeckillUrl() throws Exception {
        //id= 1000时，开始时间<now ;exposer=Exposer{exposed=true, md5='a6226c043ee30dc76d9b40aec13420b3', seckillId=1000, now=0, start=0, end=0}
        //id = 1001,开始时间 > now; exposer=Exposer{exposed=false, md5='null', seckillId=1001, now=1517147327260, start=1526918400000, end=1527004800000}
        long id = 1001;
        long phone = 13912952335L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            try {
                logger.info("exposer={}", exposer);
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, exposer.getMd5());
                logger.info("result={}", seckillExecution);
            } catch (RepeatKillException e) {
                logger.info(e.getMessage());
            } catch (SeckillCloseException e2) {
                logger.info(e2.getMessage());
            }
        } else {
            //秒杀未开启
            logger.warn("exposer={}",exposer);
        }
    }
    @Test
    public void executeSeckillProcedure(){
        long seckillId  =1000;
        long phone = 13912952336L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            logger.info(execution.getStateInfo());
        }

    }
}