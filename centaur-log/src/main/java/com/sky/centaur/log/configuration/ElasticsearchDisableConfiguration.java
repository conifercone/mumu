package com.sky.centaur.log.configuration;

import org.springframework.boot.actuate.autoconfigure.data.elasticsearch.ElasticsearchReactiveHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticsearchRestHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * es禁用配置类
 *
 * @author 单开宇
 * @since 2024-01-24
 */
@Configuration
@ConditionalOnProperty(prefix = "log.elasticsearch", name = "enabled", havingValue = "false")
@EnableAutoConfiguration(exclude = {
    ElasticsearchRestClientAutoConfiguration.class,
    ElasticsearchReactiveHealthContributorAutoConfiguration.class,
    ElasticsearchClientAutoConfiguration.class,
    ElasticsearchRestHealthContributorAutoConfiguration.class
})
public class ElasticsearchDisableConfiguration {

}
