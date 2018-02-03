package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Create By ChanJi on 2018/1/31
 */
public class RedisDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JedisPool jedisPool;
    public RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }

    /**
     * 反序列化的模式
     */
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
    public Seckill getSeckill(long seckillId){
        //redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                //并没有实现序列化操作
                //get->byte[]->反序列化->Object(Seckil)
                //序列化最好的protostuff
                //采用自定义的序列化
                //protostuff:pojo
                byte[] bytes = jedis.get(key.getBytes());
                if(bytes !=null){
                    Seckill seckill = schema.newMessage();
                    //bytes为内容，seckill为空对象，schema为反序列化的格式
                    ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                    //seckill被反序列化，压缩为原来的十分之一到五分之一，压缩速度为两个数量级那么快
                    return seckill;
                }

            }finally {
                jedis.close();
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        return  null;
    }
    public String putSeckill(Seckill seckill){
        //set Obeject(Seckill) ->序列化->byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
                //将seckill对象按照schema的模式，序列化，并设置默认缓冲区大小
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //一小时
                int timeout = 60 * 60;
                //返回结果
                String result =jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.info(e.getMessage(),e);
        }
        return null;
    }


}
