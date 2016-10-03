package module.redis;

import module.Module;
import module.redis.pooler.RedisPooler;
import redis.clients.jedis.Jedis;

/**
 * Created by onkar on 2/27/14.
 */
public class RedisModule implements Module {
	RedisPooler pooler;

	public RedisModule() {
		boot();
	}

	@Override
	public void boot() {
		pooler= RedisPooler.getInstance();
		initialise();
	}

	@Override
	public void initialise() {

	}

	public Jedis getConnection(){
		return pooler.getConnection();
	}

	public void returnConnection(Jedis jedis){
		pooler.returnConnection(jedis);
	}
}
