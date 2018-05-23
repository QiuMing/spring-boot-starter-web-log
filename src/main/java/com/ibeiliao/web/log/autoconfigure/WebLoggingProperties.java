package com.ibeiliao.web.log.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 功能：web 请求日志配置
 *
 * @author liaoming
 * @since 2018年05月19日
 */
@ConfigurationProperties(prefix = "web.log")
public class WebLoggingProperties {

    /**
     * 是否开启日志拦截,默认开启
     */
    private Boolean enable = true;

    /**
     * 拦截路径，多个使用 ";" 分隔
     */
    private String mappingPath = "";

    /**
     * 不拦截路径，多个使用 ";" 分隔
     */
    private String excludeMappingPath = "";

    /**
     * 需要打印的 header,多个使用 ";" 分隔
     */
    private String printHeader = "";

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getMappingPath() {
        return mappingPath;
    }

    public void setMappingPath(String mappingPath) {
        this.mappingPath = mappingPath;
    }

    public String getExcludeMappingPath() {
        return excludeMappingPath;
    }

    public void setExcludeMappingPath(String excludeMappingPath) {
        this.excludeMappingPath = excludeMappingPath;
    }

    public String getPrintHeader() {
        return printHeader;
    }

    public void setPrintHeader(String printHeader) {
        this.printHeader = printHeader;
    }
}
