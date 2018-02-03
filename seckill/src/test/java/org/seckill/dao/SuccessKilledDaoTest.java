package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring 配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;


    @Test
    public void insertSuccessKilled() throws Exception {
        long id = 1001L;
        long phone = 13912952334L;
        int insertresult =  successKilledDao.insertSuccessKilled(id,phone);
        System.out.println(insertresult);


    }

    @Test
    public void queryByIdWithSecKill() throws Exception {
        long id = 1001L;
        long phone = 13912952334L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(id,phone);
        System.out.println(successKilled);
    }
}