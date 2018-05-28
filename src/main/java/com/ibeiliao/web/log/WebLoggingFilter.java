package com.ibeiliao.web.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 功能：过滤所有请求，并打印请求和响应的具体细节
 *
 * @author liaoming
 * @since 2017年07月13日
 */
public class WebLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(WebLoggingFilter.class);

    private static final String REQUEST_PREFIX = "Request: ";
    private static final String RESPONSE_PREFIX = "Response: ";
    private static final String REQUEST_HEADERS_PREFIX = "Request Headers: ";

    /**
     * 不打印日志的url
     */
    private ArrayList<Pattern> excludeUrl = new ArrayList<>(8);

    /**
     * 是否开启打印日志的功能
     */
    private boolean enable = true;

    /**
     * 打印header
     */
    private  Set<String> printHeader = new HashSet<>(4);

    @Override
    protected void initFilterBean() throws ServletException {
        //初始化 exclude url
        String excludeUrl = getFilterConfig().getInitParameter(WebLogConst.EXCLUDE_URL);
        if(!StringUtils.isEmpty(excludeUrl)) {
            String[] urls = excludeUrl.split(WebLogConst.SEMICOLON);
            for (String url : urls) {
                String temp = url.replaceAll("\\*", ".*");
                Pattern pattern = Pattern.compile(temp);
                this.excludeUrl.add(pattern);
            }
        }

        String enable = getFilterConfig().getInitParameter(WebLogConst.ENABLE_PRINT_LOG);
        if(!StringUtils.isEmpty(enable)) {
            this.enable = Boolean.valueOf(enable);
        }

        String printHeader = getFilterConfig().getInitParameter(WebLogConst.PRINT_HEADER);
        if(!StringUtils.isEmpty(printHeader)) {
            String[] headers = printHeader.split(WebLogConst.SEMICOLON);
            Collections.addAll(this.printHeader, headers);
        }

        super.initFilterBean();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        request = new RequestWrapper(request);
        response = new ResponseWrapper(response);

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("error to filter e={}", e);
        } finally {
            logRequest(request);
            logResponse((ResponseWrapper) response);
        }
    }

    /**
     * 是否不拦截该 request
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if(!this.enable){
            logger.debug("disable web log Filter");
            return true;
        }

        if (isExclude(request)) {
            return true;
        }
        return super.shouldNotFilter(request);
    }

    private void logRequest(final HttpServletRequest request) {

        StringBuilder msg = new StringBuilder();
        msg.append(REQUEST_PREFIX);

        String queryString = request.getQueryString();
        String url = request.getRequestURL() + ((null == queryString) ? "" : ("?" + request.getQueryString()));
        Map<String, String> headers = new HashMap<>(4);

        if(!CollectionUtils.isEmpty(printHeader)) {
            for (String str : printHeader) {
                if (!StringUtils.isEmpty(request.getHeader(str))) {
                    headers.put(str, request.getHeader(str));
                }
            }
        }

        if (request.getMethod() != null) {
            msg.append("method=").append(request.getMethod()).append("; ");
        }

        if (request.getContentType() != null) {
            msg.append("content type=").append(request.getContentType()).append("; ");
        }
        msg.append("url=").append(url).append("\n");
        msg.append(REQUEST_HEADERS_PREFIX).append(headers);


        if (request instanceof RequestWrapper && !isMultipart(request) && !isBinaryContent(request)) {
            RequestWrapper requestWrapper = (RequestWrapper) request;
            try {
                String charEncoding = requestWrapper.getCharacterEncoding() != null ? requestWrapper.getCharacterEncoding() : "UTF-8";
                msg.append("\npayload=").append(new String(requestWrapper.toByteArray(), charEncoding));
            } catch (UnsupportedEncodingException e) {
                logger.warn("Failed to parse request payload", e);
            }

        }
        logger.info(msg.toString());
    }

    private boolean isBinaryContent(final HttpServletRequest request) {
        if (request.getContentType() == null) {
            return false;
        }
        return request.getContentType().startsWith("image") || request.getContentType().startsWith("video") || request.getContentType().startsWith("audio");
    }

    private boolean isMultipart(final HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
    }

    private boolean isMultipartResponse(final HttpServletResponse response) {
        return (response.getContentType() != null && response.getContentType().startsWith("application/msexcel"))||
                (response.getHeader("Content-Disposition")!=null);
    }

    private boolean isExclude(HttpServletRequest request) {
        String url = request.getRequestURI();
        boolean exclude = false;
        for (Pattern p : this.excludeUrl) {
            if (p.matcher(url).matches()) {
                exclude = true;
                break;
            }
        }
        return exclude;
    }

    private void logResponse(final ResponseWrapper response) {
        if (response != null &&  isMultipartResponse(response)){
            logger.info("是 isMultipartResponse 请求");
            return;
        }
        StringBuilder msg = new StringBuilder();
        msg.append(RESPONSE_PREFIX);
        msg.append("status=").append(response.getStatus()).append(";").append("\n");
        try {
            msg.append("payload=").append(new String(response.toByteArray(), response.getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            logger.warn("Failed to parse response payload", e);
        }
        logger.info(msg.toString());
    }

}
