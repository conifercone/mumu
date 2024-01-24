package com.sky.centaur.log.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * kafka禁用配置类
 *
 * @author 单开宇
 * @since 2024-01-24
 */
@Configuration
@ConditionalOnProperty(prefix = "log.kafka", name = "enabled", havingValue = "false")
@EnableAutoConfiguration(exclude = {
    KafkaAutoConfiguration.class
})
public class KafkaDisableConfiguration {

}
