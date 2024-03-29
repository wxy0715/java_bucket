package com.wxy.study.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

public class ConsumerSample {

	private final static String TOPIC_NAME = "wxy-topic";

	public static void main(String[] args) {
		//helloworld();

		//commitedOffset();		// 手动提交offset

		//commitedOffsetWithPartition();		// 手动对每个Partition进行提交

		//commitedOffsetWithPartition2();		// 手动订阅某个或某些分区，并提交offset

		controlOffset();	// 手动指定offset的起始位置，及手动提交offset

		//controlPause();		// 流量控制
	}


	/**工作里这种用法，有，但是不推荐*/
	private static void helloworld() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "49.232.142.65:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "true");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		// 消费订阅哪一个Topic或者几个Topic
		consumer.subscribe(Arrays.asList(TOPIC_NAME));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
			for (ConsumerRecord<String, String> record : records) {
				System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
						record.partition(), record.offset(), record.key(), record.value());
			}
		}
	}

	/** 手动提交offset*/
	private static void commitedOffset() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "49.232.142.65:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer(props);
		// 消费订阅哪一个Topic或者几个Topic
		consumer.subscribe(Arrays.asList(TOPIC_NAME));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
			for (ConsumerRecord<String, String> record : records) {
				// 想把数据保存到数据库，成功就成功，不成功...
				// TODO record 2 db
				System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
						record.partition(), record.offset(), record.key(), record.value());
				// 如果失败，则回滚， 不要提交offset
			}

			// 如果成功，手动通知offset提交
			consumer.commitAsync();
		}
	}


	/**手动提交offset,并且手动控制partition */
	private static void commitedOffsetWithPartition() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "49.232.142.65:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer(props);
		// 消费订阅哪一个Topic或者几个Topic
		consumer.subscribe(Arrays.asList(TOPIC_NAME));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
			// 每个partition单独处理
			for (TopicPartition partition:records.partitions()){
				List<ConsumerRecord<String, String>> records1 = records.records(partition);
				for (ConsumerRecord<String, String> record : records1) {
					System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
							record.partition(), record.offset(), record.key(), record.value());
				}
				long lastOffset = records1.get(records1.size() -1).offset();
				// 单个partition中的offset，并且进行提交
				Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
				offset.put(partition,new OffsetAndMetadata(lastOffset+1));
				// 提交offset
				consumer.commitSync(offset);
				System.out.println("=============partition - "+ partition +" end================");
			}
		}
	}

	/**手动提交offset,并且手动控制partition,更高级*/
	private static void commitedOffsetWithPartition2() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "49.232.142.65:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer(props);

		// jiangzh-topic - 0,1两个partition
		TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);
		TopicPartition p1 = new TopicPartition(TOPIC_NAME, 1);

		// 消费订阅哪一个Topic或者几个Topic
//        consumer.subscribe(Arrays.asList(TOPIC_NAME));

		// 消费订阅某个Topic的某个分区
		consumer.assign(Arrays.asList(p0));

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
			// 每个partition单独处理
			for (TopicPartition partition : records.partitions()) {
				List<ConsumerRecord<String, String>> pRecord = records.records(partition);
				for (ConsumerRecord<String, String> record : pRecord) {
					System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
							record.partition(), record.offset(), record.key(), record.value());

				}
				long lastOffset = pRecord.get(pRecord.size() - 1).offset();
				// 单个partition中的offset，并且进行提交
				Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
				offset.put(partition, new OffsetAndMetadata(lastOffset + 1));
				// 提交offset
				consumer.commitSync(offset);
				System.out.println("=============partition - " + partition + " end================");
			}
		}
	}


	/**手动指定offset的起始位置，及手动提交offset*/
	private static void controlOffset() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "49.232.142.65:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer(props);

		// jiangzh-topic - 0,1两个partition
		TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);

		// 消费订阅某个Topic的某个分区
		consumer.assign(Arrays.asList(p0));

		while (true) {
			// 手动指定offset起始位置
            /*
                1、人为控制offset起始位置
                2、如果出现程序错误，重复消费一次
             */
            /*
                1、第一次从0消费【一般情况】
                2、比如一次消费了100条， offset置为101并且存入Redis
                3、每次poll之前，从redis中获取最新的offset位置
                4、每次从这个位置开始消费
             */
			consumer.seek(p0, 200);

			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
			// 每个partition单独处理
			for (TopicPartition partition : records.partitions()) {
				List<ConsumerRecord<String, String>> pRecord = records.records(partition);
				for (ConsumerRecord<String, String> record : pRecord) {
					System.err.printf("patition = %d , offset = %d, key = %s, value = %s%n",
							record.partition(), record.offset(), record.key(), record.value());

				}
				long lastOffset = pRecord.get(pRecord.size() - 1).offset();
				// 单个partition中的offset，并且进行提交
				Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
				offset.put(partition, new OffsetAndMetadata(lastOffset + 1));
				// 提交offset
				consumer.commitSync(offset);
				System.out.println("=============partition - " + partition + " end================");
			}
		}
	}


	/**流量控制 - 限流*/
	private static void controlPause() {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "49.232.142.65:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		KafkaConsumer<String, String> consumer = new KafkaConsumer(props);

		// jiangzh-topic - 0,1两个partition
		TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);
		TopicPartition p1 = new TopicPartition(TOPIC_NAME, 1);

		// 消费订阅某个Topic的某个分区
		consumer.assign(Arrays.asList(p0, p1));
		long totalNum = 40;
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
			// 每个partition单独处理
			for (TopicPartition partition : records.partitions()) {
				List<ConsumerRecord<String, String>> pRecord = records.records(partition);
				long num = 0;
				for (ConsumerRecord<String, String> record : pRecord) {
					System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
							record.partition(), record.offset(), record.key(), record.value());
                    /*
                        1、接收到record信息以后，去令牌桶中拿取令牌
                        2、如果获取到令牌，则继续业务处理
                        3、如果获取不到令牌， 则pause等待令牌
                        4、当令牌桶中的令牌足够， 则将consumer置为resume状态
                     */
					num++;
					if (record.partition() == 0) {
						if (num >= totalNum) {
							consumer.pause(Arrays.asList(p0));
						}
					}

					if (record.partition() == 1) {
						if (num == 40) {
							consumer.resume(Arrays.asList(p0));
						}
					}
				}

				long lastOffset = pRecord.get(pRecord.size() - 1).offset();
				// 单个partition中的offset，并且进行提交
				Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
				offset.put(partition, new OffsetAndMetadata(lastOffset + 1));
				// 提交offset
				consumer.commitSync(offset);
				System.out.println("=============partition - " + partition + " end================");
			}
		}
	}
}
