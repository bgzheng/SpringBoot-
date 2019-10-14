package com.springboot.demo.basic.database.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库监控配置
 *
 * @author zhengbinggui
 * @version 2018/10/18
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties (prefix = "spring.datasource.monitor")
public class DataMonitorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataMonitorConfig.class);

    /**
     * 请求url
     */
    private String urlMappings;

    /**
     * 过滤url
     */
    private String webStatFilter;

    /**
     * IP白名单 (没有配置或者为空，则允许所有访问)
     */
    private String allow;

    /**
     * IP黑名单 (存在共同时，deny优先于allow)
     */
    private String deny;

    /**
     * 用户名
     */
    private String loginUsername;

    /**
     * 密码
     */
    private String loginPassword;

    /**
     * 放行资源
     */
    private String exclusions;

    /**
     * 禁用HTML页面上的“Reset All”功能
     */
    private String resetEnable;

    @Bean
    public ServletRegistrationBean druidServlet() {
        LOGGER.info("init Druid Servlet Configuration.");
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean ();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings(this.getUrlMappings());
        Map<String, String> initParameters = new HashMap<>(5);
        initParameters.put("loginUsername", this.getLoginUsername());
        initParameters.put("loginPassword", this.getLoginPassword());
        initParameters.put("resetEnable", this.getResetEnable());
        initParameters.put("allow", this.getAllow());
        initParameters.put("deny", this.getDeny());
        servletRegistrationBean.setInitParameters(initParameters);
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean ();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns(this.getWebStatFilter());
        filterRegistrationBean.addInitParameter("exclusions", this.getExclusions());
        return filterRegistrationBean;
    }
}
