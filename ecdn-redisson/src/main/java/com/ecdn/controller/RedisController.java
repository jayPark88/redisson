package com.ecdn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
		
		Random randomGenerator = new Random();
		Map<String, String> hash = new HashMap<String, String>();
		for (int i = 1; i < 100001; i++) {
			for(int k=0; k<randomGenerator.nextInt(3)+1; k++) {
				hash.put("server"+k, "value"+k);
			}
			RedisFuture<String> future = commands.hmset(UUID.randomUUID().toString(), hash);
			hash= new HashMap<String, String>();
			System.out.println("wwww : "+hash);
			future.thenAccept(res -> {
				count++;
			});
		}		
		while(true){
			if(count>=100000){
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
	@RequestMapping(value = "/lettuceHashingDeleteSync", method = RequestMethod.GET)
	public String lettuceHashingDeleteSync(String jedisKey, String jedisValue, Model model)
			throws InterruptedException, ExecutionException {

		RedisClient redisClient = RedisClient.create(
				"redis-sentinel://dbfrhrfh1rk!@13.124.239.130:26379,dbfrhrfh1rk!@13.124.160.180:26379,dbfrhrfh1rk!@13.124.163.100:26379/0#mymaster");

		StatefulRedisConnection<String, String> connection = redisClient.connect();
		RedisAsyncCommands<String, String> commands = connection.async();

		commands.flushCommands();
		// later
		connection.close();

		System.out.println("complete!!!");
		System.out.println("Connected to Redis using Redis Sentinel");

		return "lettuce";
	}
		//test Area
}
