package com.enhui;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class ProducerDebuger {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties properties = initProducerConfig();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            String topic = "test1";
            final ProducerRecord<String, String> msg = new ProducerRecord<>(topic, "key", "value");

            producer.send(
                    msg,
                    (RecordMetadata metadata, Exception exception) -> {
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                        log.info("debug producer::异步 callback: metadata: {}", metadata);
                    });

            producer
                    .send(
                            msg,
                            (RecordMetadata metadata, Exception exception) -> {
                                if (exception != null) {
                                    exception.printStackTrace();
                                }
                                log.info("debug producer::同步 callback: metadata: {}", metadata);
                            })
                    .get();

            printThreadInfo();
        }
    }

    private static Properties initProducerConfig() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-debug:9092");
        properties.setProperty(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "heh-producer-debug");
        return properties;
    }

    private static void printThreadInfo() {
        int threadCount = Thread.activeCount();
        Thread[] threads = new Thread[threadCount];
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        threadGroup.enumerate(threads);
        Set<String> threadNames = Arrays.stream(threads).map(Thread::getName).collect(Collectors.toSet());
        log.info("debug producer::包含全部线程个数为：{}, 明细为：{}", threadCount, threadNames);
    }
}
