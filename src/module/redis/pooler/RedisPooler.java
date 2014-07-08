package module.redis.pooler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import system.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by onkar on 2/27/14.
 */
public class RedisPooler {
	private JedisPool jedisPool;

	private static RedisPooler ourInstance = new RedisPooler();

	public static RedisPooler getInstance() {
		return ourInstance;
	}

	private RedisPooler() {
		Properties p=new Properties();
		try {
			p.load(new FileInputStream("config/redis.properties"));
			String host=p.getProperty("host");
			jedisPool=new JedisPool(new JedisPoolConfig(),host);
		} catch (IOException e) {
			Log.logger.error(e.getMessage());
		}

	}

	public Jedis getConnection(){
		return jedisPool.getResource();
	}

	public void returnConnection(Jedis jedis){
		jedisPool.returnResource(jedis);
	}
}
