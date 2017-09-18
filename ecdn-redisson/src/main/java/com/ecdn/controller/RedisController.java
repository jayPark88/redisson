package com.ecdn.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RFuture;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.async.RedisStringAsyncCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.cluster.api.sync.RedisAdvancedClusterCommands;

@Controller
public class RedisController {
	
	private static long count = 0;
	long countColcur = 1000;

	@RequestMapping(value = "/redissonSyncWrite", method = RequestMethod.GET)
	public String redissonWriteSync(String jedisKey, String jedisValue, Model model) {
		// redisson
		Config config = new Config();
		config.useSentinelServers().setMasterName("mymaster")
				.addSentinelAddress("redis://13.124.239.130:26379", "redis://13.124.160.180:26379")
				.addSentinelAddress("redis://13.124.163.100:26379").setPassword("dbfrhrfh1rk!").setTimeout(30000);

		RedissonClient redisson = Redisson.create(config);

		long startTime = System.currentTimeMillis();
		long count = 0;
		
		String[] uuid= new String[1200];
		String[] uuidSub= new String[1200];
		for(int j=1; j<=1000; j++){
			uuid[j]=UUID.randomUUID().toString();
		}
		
		for(int k=1; k<=1000; k++){
			uuidSub[k]=UUID.randomUUID().toString();
		}
		
		int[] randomMath= new int[1200];
		for(int l=1; l<=1000; l++){
			randomMath[l]=(int) (Math.random() * 10) + 1;
		}
		
		for (int i = 1; i <= 1000; i++) {
			redisson.getMap(uuid[i]).put(uuidSub[i], "server" + randomMath[i]);
			count = i;

		}
		long endTime = System.currentTimeMillis();
		System.out.println("startTime :" + startTime);
		System.out.println("endTime :" + endTime);
		System.out.println("count :" + count);
		System.out.println("time :" + (endTime - startTime)/(double)1000);
		System.out.println("wps :" + 1000/((endTime - startTime)/(double)1000));
		
		return "home";
	}
	
	@RequestMapping(value = "/lettuceSyncWrite", method = RequestMethod.GET)
	public String lettuce(String jedisKey, String jedisValue, Model model)
			throws InterruptedException, ExecutionException {

		RedisClient redisClient = RedisClient.create(
				"redis-sentinel://dbfrhrfh1rk!@13.124.239.130:26379,dbfrhrfh1rk!@13.124.160.180:26379,dbfrhrfh1rk!@13.124.163.100:26379/0#mymaster");

		StatefulRedisConnection<String, String> connection = redisClient.connect();
		RedisCommands<String, String> commands = connection.sync();

		// perform a series of independent calls
		long startTime = System.currentTimeMillis();
		long count = 0;

		String[] uuid= new String[1200];
		for(int j=1; j<=1000; j++){
			uuid[j]=UUID.randomUUID().toString();
		}
		
		int[] randomMath= new int[1200];
		for(int k=1; k<=1000; k++){
			randomMath[k]=(int) (Math.random() * 10) + 1;
		}
		
		for (int i = 1; i <= 1000; i++) {
			commands.set(uuid[i], "service" + randomMath[i]);
			count = i;
		}
		long endTime = System.currentTimeMillis();
		
		System.out.println("startTime :" + startTime);
		System.out.println("endTime :" + endTime);
		System.out.println("count :" + count);
		System.out.println("time :" + (endTime - startTime)/(double)1000);
		System.out.println("wps :" + 1000/((endTime - startTime)/(double)1000));

		// later
		connection.close();
		System.out.println("complete!!!");
		System.out.println("Connected to Redis using Redis Sentinel");

		return "lettuce";
	}
	
	@RequestMapping(value = "/redissonSyncRead", method = RequestMethod.GET)
	public String redissonSyncRead(String jedisKey, String jedisValue, Model model) {
		// redisson
		Config config = new Config();
		config.useSentinelServers().setMasterName("mymaster")
				.addSentinelAddress("redis://13.124.239.130:26379", "redis://13.124.160.180:26379")
				.addSentinelAddress("redis://13.124.163.100:26379").setPassword("dbfrhrfh1rk!").setTimeout(30000);

		RedissonClient redisson = Redisson.create(config);
		RMap<String, String> map = redisson.getMap("jaypark1");
		
		long startTime = System.currentTimeMillis();
		long count = 0;

		for (int i = 1; i <= 1000; i++) {
			map.get("field1");
			count = i;
		}
		long endTime =  System.currentTimeMillis();
		System.out.println("count :" + count);
		System.out.println("time :" + (endTime - startTime)/(double)1000);
		System.out.println("wps :" + 1000/((endTime - startTime)/(double)1000));

		return "home";
	}
	
	@RequestMapping(value = "/lettuceSyncRead", method = RequestMethod.GET)
	public String lettuceSyncRead(String jedisKey, String jedisValue, Model model)
			throws InterruptedException, ExecutionException {

		RedisClient redisClient = RedisClient.create(
				"redis-sentinel://dbfrhrfh1rk!@13.124.239.130:26379,dbfrhrfh1rk!@13.124.160.180:26379,dbfrhrfh1rk!@13.124.163.100:26379/0#mymaster");

		StatefulRedisConnection<String, String> connection = redisClient.connect();
		RedisCommands<String, String> commands = connection.sync();

		// perform a series of independent calls
		Double startTime = (double) System.currentTimeMillis();
		long count = 0;
		
		for (int i = 1; i <= 950; i++) {
			commands.hget("jaypark1", "field1");
			//get("jaypark1");
			count = i;
		}
		Double endTime = (double) System.currentTimeMillis();
		Double time = (endTime - startTime);
		Double wps = (count / time);
		System.out.println("count :" + count);
		System.out.println("time :" + time);
		System.out.println("wps :" + wps * 1000);

		// later
		connection.close();
		System.out.println("complete!!!");
		System.out.println("Connected to Redis using Redis Sentinel");

		return "lettuce";
	}
	
	@RequestMapping(value = "/redissonAsyncRead", method = RequestMethod.GET)
	public String home(String jedisKey, String jedisValue, Model model) {
		// redisson
		Config config = new Config();
		config.useSentinelServers().setMasterName("mymaster")
				.addSentinelAddress("redis://13.124.239.130:26379", "redis://13.124.160.180:26379")
				.addSentinelAddress("redis://13.124.163.100:26379").setPassword("dbfrhrfh1rk!");

		RedissonClient client = Redisson.create(config);
		//Async test area start
		RMap<Object, Object> map = client.getMap("jaypark1");

		long startTime = System.currentTimeMillis();
		for (int i = 1; i < 1001; i++) {
			RFuture<Object> val1 = map.getAsync("field1");
			val1.thenAccept(res -> {
				System.out.println("?? : "+res);
				count++;
			});
		}
		while(true){
			System.out.println("테스트중 : "+count);
			if(count==1000){
				Double endTime = (double) System.currentTimeMillis();
				Double time = (endTime - startTime);
				Double wps = (countColcur / time);
				System.out.println("count :" + count);
				System.out.println("time :" + time);
				System.out.println("wps :" + wps * 1000);
				break;
			}
		}
		
		return "home";
	}
	
	@RequestMapping(value = "/lettuceAsyncRead", method = RequestMethod.GET)
	public String lettuceAsyncRead(String jedisKey, String jedisValue, Model model)
			throws InterruptedException, ExecutionException {

		RedisClient client = RedisClient.create(
				"redis-sentinel://dbfrhrfh1rk!@13.124.239.130:26379,dbfrhrfh1rk!@13.124.160.180:26379,dbfrhrfh1rk!@13.124.163.100:26379/0#mymaster");

		StatefulRedisConnection<String, String> connection = client.connect();
		//RedisStringAsyncCommands<String, String> async = connection.async();
        RedisAsyncCommands<String, String>asyncCommands = connection.async();

		// perform a series of independent calls
		long startTime = System.currentTimeMillis();
		
		for (int i = 1; i < 1001; i++) {
			
			RedisFuture<String> future = asyncCommands.hget("jaypark1", "field1");
			future.thenAccept(res -> {
				count++;
			});
		}
		while(true){
			System.out.println("테스트중 : "+count);
			if(count==1000){
				Double endTime = (double) System.currentTimeMillis();
				Double time = (endTime - startTime);
				Double wps = (countColcur / time);
				System.out.println("count :" + count);
				System.out.println("time :" + time);
				System.out.println("wps :" + wps * 1000);
				break;
			}
		}
		
		// later
		connection.close();
		System.out.println("complete!!!");
		System.out.println("Connected to Redis using Redis Sentinel");

		return "lettuce";
	}
	
	@RequestMapping(value = "/redissonWriteAsync", method = RequestMethod.GET)
	public String redissonWriteAsync(String jedisKey, String jedisValue, Model model) {
		// redisson
		Config config = new Config();
		config.useSentinelServers().setMasterName("mymaster")
				.addSentinelAddress("redis://13.124.239.130:26379", "redis://13.124.160.180:26379")
				.addSentinelAddress("redis://13.124.163.100:26379").setPassword("dbfrhrfh1rk!").setTimeout(30000);

		RedissonClient client = Redisson.create(config);
		
		long startTime =  System.currentTimeMillis();
		
		String[] uuid= new String[1200];
		String[] uuidSub= new String[1200];
		for(int j=1; j<=1000; j++){
			uuid[j]=UUID.randomUUID().toString();
		}
		
		for(int k=1; k<=1000; k++){
			uuidSub[k]=UUID.randomUUID().toString();
		}
		
		int[] randomMath= new int[1200];
		for(int l=1; l<=1000; l++){
			randomMath[l]=(int) (Math.random() * 10) + 1;
		}
		
		
		for (int i = 1; i <= 1000; i++) {
			RMap<Object, Object> server1List = client.getMap(uuid[i].toString());
			RFuture<Object> val1 = server1List.putAsync(uuidSub[i].toString(), "server" + randomMath[i]);
			val1.thenAccept(res -> {
				count++;
			});
		}
		
		System.out.println("테드스  "+count);
		//Async test area End
		while(true){
			if(count==1000){
				Double endTime = (double) System.currentTimeMillis();
				Double time = (endTime - startTime);
				Double wps = (countColcur / time);
				System.out.println("count :" + countColcur);
				System.out.println("time :" + time);
				System.out.println("wps :" + wps * 1000);
				break;
			}
		}

		return "home";
	}
	
	@RequestMapping(value = "/lettuceWriteAsync", method = RequestMethod.GET)
	public String lettuceWriteAsync(String jedisKey, String jedisValue, Model model)
			throws InterruptedException, ExecutionException {

		RedisClient redisClient = RedisClient.create(
				"redis-sentinel://dbfrhrfh1rk!@13.124.239.130:26379,dbfrhrfh1rk!@13.124.160.180:26379,dbfrhrfh1rk!@13.124.163.100:26379/0#mymaster");

		StatefulRedisConnection<String, String> connection = redisClient.connect();
		RedisAsyncCommands<String, String> commands = connection.async();

		// perform a series of independent calls
		List<RedisFuture<?>> futures = Lists.newArrayList();
		long startTime = System.currentTimeMillis();
		
		String[] uuid= new String[1200];
		String[] uuidSub= new String[1200];
		for(int j=1; j<=1000; j++){
			uuid[j]=UUID.randomUUID().toString();
		}
		
		for(int k=1; k<=1000; k++){
			uuidSub[k]=UUID.randomUUID().toString();
		}
		
		int[] randomMath= new int[1200];
		for(int l=1; l<=1000; l++){
			randomMath[l]=(int) (Math.random() * 10) + 1;
		}
		
		for (int i = 1; i < 1001; i++) {
			int n = (int) (Math.random() * 10) + 1;
			RedisFuture<String> future = commands.set(uuidSub[i], "server-" + randomMath[i]);
			future.thenAccept(res -> {
				count++;
			});
		}		
		while(true){
			if(count==1000){
				Double endTime = (double) System.currentTimeMillis();
				Double time = (endTime - startTime);
				Double wps = (countColcur / time);
				System.out.println("count :" + countColcur);
				System.out.println("time :" + time);
				System.out.println("wps :" + wps * 1000);
				break;
			}
		}

		// later
		connection.close();

		System.out.println("complete!!!");
		System.out.println("Connected to Redis using Redis Sentinel");

		return "lettuce";
	}
	
	//test Area
	@RequestMapping(value = "/lettuceHashingWriteAsync", method = RequestMethod.GET)
	public String lettuceHashingWriteAsync(String jedisKey, String jedisValue, Model model)
			throws InterruptedException, ExecutionException {

		RedisClient redisClient = RedisClient.create(
				"redis-sentinel://dbfrhrfh1rk!@13.124.239.130:26379,dbfrhrfh1rk!@13.124.160.180:26379,dbfrhrfh1rk!@13.124.163.100:26379/0#mymaster");

		StatefulRedisConnection<String, String> connection = redisClient.connect();
		RedisAsyncCommands<String, String> commands = connection.async();

		// perform a series of independent calls
		List<RedisFuture<?>> futures = Lists.newArrayList();
		long startTime = System.currentTimeMillis();
		
		for (int i = 61000001; i < 62000001; i++) {
			RedisFuture<Boolean> future = commands.hset("jaypark"+i, "field"+i, "value"+i);
			future.thenAccept(res -> {
				count++;
			});
		}		
		while(true){
			System.out.println("테스트중1 : "+count);
			if(count==1000000){
				Double endTime = (double) System.currentTimeMillis();
				Double time = (endTime - startTime);
				Double wps = (countColcur / time);
				System.out.println("count :" + count);
				System.out.println("time :" + time);
				System.out.println("wps :" + wps * 1000);
				break;
			}
		}

		// later
		connection.close();

		System.out.println("complete!!!");
		System.out.println("Connected to Redis using Redis Sentinel");

		return "lettuce";
	}
	//test Area
	
	//test Area
		@RequestMapping(value = "/lettuceHashingDeleteSync", method = RequestMethod.GET)
		public String lettuceHashingDeleteSync(String jedisKey, String jedisValue, Model model)
				throws InterruptedException, ExecutionException {

			RedisClient redisClient = RedisClient.create(
					"redis-sentinel://dbfrhrfh1rk!@13.124.239.130:26379,dbfrhrfh1rk!@13.124.160.180:26379,dbfrhrfh1rk!@13.124.163.100:26379/0#mymaster");

			StatefulRedisConnection<String, String> connection = redisClient.connect();
			for (int i = 1; i < 1001; i++) {
				connection.sync().flushall();
				
			}
			//connection.sync().flushall();
			// later
			connection.close();

			System.out.println("complete!!!");
			System.out.println("Connected to Redis using Redis Sentinel");

			return "lettuce";
		}
		//test Area
}
