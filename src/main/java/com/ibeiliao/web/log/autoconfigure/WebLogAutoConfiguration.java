package com.ibeiliao.web.log.autoconfigure;

import com.ibeiliao.web.log.WebLogConst;
import com.ibeiliao.web.log.WebLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能：自动注入Web 日志拦截器
 *
 * @author liaoming
 * @since 2018年05月19日
 */
@Configuration
@EnableConfigurationProperties(WebLoggingProperties.class)
public class WebLogAutoConfiguration {

    @Autowired
    private WebLoggingProperties webLoggingProperties;


    /**
     * 日志拦截过滤器
     */
    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new WebLoggingFilter());
        registration.addUrlPatterns(this.getUrlPatterns());
        registration.addInitParameter(WebLogConst.EXCLUDE_URL, webLoggingProperties.getExcludeMappingPath());
        registration.addInitParameter(WebLogConst.ENABLE_PRINT_LOG, webLoggingProperties.getEnable().toString());
        registration.addInitParameter(WebLogConst.PRINT_HEADER, webLoggingProperties.getPrintHeader());
        registration.setName("loggingFilter");
        return registration;
    }

    private String[] getUrlPatterns() {
        String mappingPath = webLoggingProperties.getMappingPath();
        if (mappingPath.contains(WebLogConst.SEMICOLON)) {
            return mappingPath.split(WebLogConst.SEMICOLON);
        }
        return new String[]{mappingPath};
    }
}
