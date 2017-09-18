//package com.ecdn.kafka;
//
//import java.util.concurrent.CountDownLatch;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//
//public class Listener {
//	
//	public final CountDownLatch countDownLatch1 = new CountDownLatch(1);
//
//	@KafkaListener(id = "foo", topics = "topic1")
//	public void listen(ConsumerRecord<?, ?> record) {
//		System.out.println("컨슈머 리스너 테스트 중 : "+record.value());
//		countDownLatch1.countDown();
//	}
//}
