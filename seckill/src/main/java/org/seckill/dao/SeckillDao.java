package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {
    /**
     * 减库存
     * @param seckillId
     * @param KillTime
     * @return 如果影响行数>1，表示更新的纪录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId,@Param("KillTime") Date KillTime);

    /**
     * 根据Id查询秒杀对象
     * @param SeckillId
     * @return
     */
    Seckill queryById(long SeckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * java不会保存形参所以用mybatis的@Param指定，只有一个参数就不需要指定
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit")int limit);

    /**
     * 使用存储过程执行秒杀
     * 这个方法需要在SeckillDao.xml中配置
     * @param paramMap
     */
    void killByProcedure(Map<String,Object> paramMap);
}
