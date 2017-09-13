package com.controller;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import redis.clients.jedis.Jedis;
 
@Controller
public class HomeController {
     
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(String jedisKey, String jedisValue,Model model) {
        
    	Config config = new Config();
    	config.useSentinelServers().setPassword("dbfrhrfh1rk!")
    	    .setMasterName("mymaster")
    	    .addSentinelAddress("redis://13.124.239.130:26379");
    	//192.168.1.4

    	RedissonClient redisson = Redisson.create(config);
        return "home";
    }
 
}