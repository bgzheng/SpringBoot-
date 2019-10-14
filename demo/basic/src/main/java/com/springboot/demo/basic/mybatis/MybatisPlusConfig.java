package com.springboot.demo.basic.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus 配置类
 *
 * @author zhengbinggui
 * @version 2018/10/26
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Configuration
@MapperScan("com.springboot.demo.business.mapper.**")
public class MybatisPlusConfig {

}
