package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务接口：站在“使用者”的角度设计接口
 * 三个方面：
 * 1、方法定义粒度：非常明确（执行秒杀和需要传入的参数），使用者方便
 * 2、参数，简单
 * 3、放回类型（return 类型/异常）
 *
 */
public interface SeckillService {
    /**
     * 查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckill();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开始时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     * @return Exposer
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     *
     * @param seckillId
     * @param userphone
     * @param md5
     * SeckillException 表示秒杀出错
     * RepeatKillException 表示重复秒杀
     * SeckillCloseException 表示秒杀关闭异常
     */
    SeckillExecution executeSeckill (long seckillId, long userphone, String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;

    SeckillExecution executeSeckillProcedure(Long seckillId,long  userPhone,String md5);


}
