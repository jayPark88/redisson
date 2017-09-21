package com.ecdn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;

@Controller
public class RedisController {
	
	//test
	private static final Logger logger = Logger.getLogger(RedisController.class.getName());
	//test
	private static long count = 0;
	long countColcur = 1000;
	
	
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
		
		for (int i = 1; i <= 1000; i++) {
//			commands.lrange("4ac12669-296c-4257-bd1a-3bb99d3e1f32", 0, 1);
			commands.hget("0", "38eef4dd-990e-406f-ad4e-bd8565dc1f09");
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
		count = 0;
		for (int i = 1; i <= 1000; i++) {
			
//			RedisFuture<List<String>> future = asyncCommands.lrange("4ac12669-296c-4257-bd1a-3bb99d3e1f32", 0, 1);
			RedisFuture<String> future = asyncCommands.hget("0", "38eef4dd-990e-406f-ad4e-bd8565dc1f09");
			future.thenAccept(res -> {
				count++;
			});
		}
		while(true){
			logger.info(""+count);
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
		
		long startTime = System.currentTimeMillis();
		
		Random randomGenerator = new Random();
		count = 0;
		for (int i = 1; i <= 1000; i++) {
			String uuid = UUID.randomUUID().toString();
			RedisFuture<Long> future = commands.rpush(uuid, "server"+randomGenerator.nextInt(3));
			for(int k=0; k<randomGenerator.nextInt(3)+1; k++) {
				future = commands.rpush(uuid, "server"+randomGenerator.nextInt(3)+1);
			}
			
			future.thenAccept(res -> {
				count++;
			});
		}
		
		while(true){
			logger.info(""+count);
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
	
	@RequestMapping(value = "/lettuceSyncWrite", method = RequestMethod.GET)
	public String lettuce(String jedisKey, String jedisValue, Model model)
			throws InterruptedException, ExecutionException {

		RedisClient redisClient = RedisClient.create(
				"redis-sentinel://dbfrhrfh1rk!@13.124.239.130:26379,dbfrhrfh1rk!@13.124.160.180:26379,dbfrhrfh1rk!@13.124.163.100:26379/0#mymaster");

		StatefulRedisConnection<String, String> connection = redisClient.connect();
		RedisCommands<String, String> commands = connection.sync();
		
		long startTime = System.currentTimeMillis();
		
		Random randomGenerator = new Random();
		
//		for (int i = 1; i <= 1000; i++) {
//			String uuid = UUID.randomUUID().toString();
//			commands.rpush(uuid, "server"+randomGenerator.nextInt(3));
//			for(int k=0; k<randomGenerator.nextInt(3)+1; k++) {
//				commands.rpush(uuid, "server"+randomGenerator.nextInt(3)+1);
//			}
//		}
		for (int i = 1; i <= 1000; i++) {			
			String hashKeySection = String.valueOf(i/1000000);
			String uuid = UUID.randomUUID().toString();
			
			ArrayList<String> arraylist = new ArrayList<String>();
			int random =randomGenerator.nextInt(3)+1;
			for(int k=0; k<=random; k++) {	
				arraylist.add("server"+k);
				if(k==random) {
					commands.hset(hashKeySection, uuid, arraylist.toString());
				}
			}
			
			if(i==100 || i==1000 || i==10000 || i==100000 || i==1000000|| i==10000000) {
				System.out.println("i : "+i+", "+"section : "+hashKeySection+", uuid : "+uuid);
			}	
		}		
		
		Double endTime = (double) System.currentTimeMillis();
		Double time = (endTime - startTime);
		Double wps = (countColcur / time);
		System.out.println("count :" + countColcur);
		System.out.println("time :" + time);
		System.out.println("wps :" + wps * 1000);
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
		long startTime = System.currentTimeMillis();
		
		Random randomGenerator = new Random();
		count = 0;
		for (int i = 4500001; i <= 5000000; i++) {			
			String hashKeySection = String.valueOf(i/100);
			String uuid = UUID.randomUUID().toString();
			
			RedisFuture<Boolean> future = commands.hset(hashKeySection, uuid, "server"+randomGenerator.nextInt(3));
			ArrayList<String> arraylist = new ArrayList<String>();
			int random =randomGenerator.nextInt(3)+1;
			
			for(int k=0; k<=random; k++) {	
				arraylist.add("server"+k);
				if(k==random) {
					future = commands.hset(hashKeySection, uuid, arraylist.toString());
				}
			}
			
			if(i==100 || i==1000 || i==10000 || i==100000 || i==1000000) {
				System.out.println("i : "+i+", "+"section : "+hashKeySection+", uuid : "+uuid);
			}		
			
			future.thenAccept(res -> {
				count++;
			});
		}		
		while(true){
			logger.info(""+count);
			if(count==500000){				
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
		redisClient.shutdown();

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
