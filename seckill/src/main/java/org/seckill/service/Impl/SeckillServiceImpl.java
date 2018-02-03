package org.seckill.service.Impl;


import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create By ChanJi on 2018/1/28
 */
//@Component 当不知道是dao、service、还是controller的时候
//@Sercice
//@Dao
//@Controller
@Component
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //注入Service依赖
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;
    //用于混淆md5
    private final String slat = "dfagfasdf34efdf5434q234e466**($*))";

    @Override
    public List<Seckill> getSeckill() {
        List<Seckill> list = seckillDao.queryAll(0, 4);
        // logger.info("SeckillList",list.toString());
        return list;
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {

        //优化点：缓存优化:超时的基础上维护一致性
        /**
         *get from cache
         * if null
         *  get db
         *  else
         *      put cache
         * logic
         */
        //1、访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        //redis为空
        if (seckill == null) {
            //访问数据库
            seckill = seckillDao.queryById(seckillId);
            //如果数据库中也没有
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //放入redis
                redisDao.putSeckill(seckill);
            }
        }


        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() ||
                nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    //生成md5
    private String getMd5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点
     * 1：开发团队达成一致约定，明确标注事务方法的编程风格
     * 2：保证事务方法的执行时间尽可能的短，不要穿插其他网络操作，PRPC/HTTP请求或者剥离到事务方法外部
     * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务
     *
     */
    public SeckillExecution executeSeckill(long seckillId, long userphone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill date rewrite");
        }
        //秒杀逻辑：减库存，记录购买行为
        /**
         * 优化逻辑，先记录购买行为，然后再减库存
         * insert购买行为的操作GC和网络演示可以同时进行，
         * 但是减库存是一个行级锁，将它放于insert的后面
         * 这样就只有减库存这个网络延时
         */
        Date nowTime = new Date();
        //超时，数据库连接断了等等其他的异常处理
        try {
            //记录购买行为
            int instertCount = successKilledDao.insertSuccessKilled(seckillId, userphone);
            //唯一：seckilled,userphone
            if (instertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //减库存
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有跟新到记录
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(seckillId, userphone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }

            //当只catch Exception的时候不能够捕捉到SeckillCloseException 和RepeatKillException异常
            //所以在此之前要先捕获异常
        } catch (SeckillCloseException e1) {
            throw e1;

        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译器异常，转化为运行期异常，spring会帮我们做rollback
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(Long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStatEnum.DATE_REWRITE);
        }
        Date killTime = new Date();
        Map map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);

        try {
            seckillDao.killByProcedure(map);

            //获取result，result站在执行之后值会改变
            //添加MapUtils的工具类jar包 commons-collections
            //将map 的key为result的值取出，并且设置默认值为-2设置
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWithSecKill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, sk);
            } else {
                //根据result的值获取秒杀状态
                return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
        }
    }
}
