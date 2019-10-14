package com.springboot.demo.basic.database.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源
 *
 * @author zhengbinggui
 * @version 2018/10/18
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Configuration
public class DruidConfig {

    @Bean (name = "dataSource")
    @ConfigurationProperties (prefix = "spring.datasource")
    public DruidDataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }
}
