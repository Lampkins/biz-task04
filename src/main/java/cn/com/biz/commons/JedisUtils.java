package cn.com.biz.commons;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Jedis连接工具类
 * @author liuchengrui
 * @date 2020/12/1 9:13
 */
@Slf4j
public class JedisUtils {

    /**
     * 存放redis配置内容
     */
    private static Properties properties;

    private static Jedis jedis;

    static {
        // 加载配置文件
        properties = new Properties();
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        try (InputStream in = new FileInputStream(path+"redis-config.properties")) {
            properties.load(in);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 获取配置的Jedis对象
     * @return Jedis操作对象
     * @author liuchengrui
     * @date 2020/12/2 9:14
     */
    public static Jedis getJedis() {
        String host = properties.getProperty("redis.host");
        int port = Integer.parseInt(properties.getProperty("redis.port"));
        String password = properties.getProperty("redis.auth.password");
        try {
            jedis = new Jedis(host, port);
            jedis.auth(password);
            return jedis ;
        } catch (JedisConnectionException | JedisDataException e) {
            log.warn(e.getMessage(), e);
            return null;
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 关闭Jedis
     * @param jedis 待关闭的Jedis对象
     */
    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
