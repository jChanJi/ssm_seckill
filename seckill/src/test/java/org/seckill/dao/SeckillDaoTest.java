package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static java.lang.System.out;
import static org.junit.Assert.*;

/**
 * 配置spring和 junit整合，junit启动时加载spring IOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring 配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() throws Exception {
        long seckillId = 1000;
        Seckill seckill = seckillDao.queryById(seckillId);
        out.println(seckill.toString());
    }
    //java不会保存形参所以用mybatis的@Param指定
    @Test
    public void queryAll() throws Exception {
        List<Seckill> seckills = seckillDao.queryAll(2, 100);
        for (Seckill seckill : seckills) {
            out.println(seckill.toString());
        }
    }
    //org.apache.ibatis.binding.BindingException: Parameter 'seckillId' not found.
    //seckillId是形参，需要nybatis绑定
    @Test
        public void reduceNumber() throws Exception {
        Date killTime = new Date();
        int updateCount  = seckillDao.reduceNumber(1000L,killTime);
        System.out.println(updateCount);
    }
}