package com.demo.netty.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * GPRequest
 * <p>
 * liwenbin
 * 2019/4/5 9:55
 */
public class GPRequest {
    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public GPRequest() {
    }

    public GPRequest(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public String geturl() {
        return request.getUri();
    }

    public String getMethod() {
        return request.getMethod().name();
    }

    public Map<String, List<String>> getParameters() {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        Map<String, List<String>> parameters = decoder.parameters();
        return parameters;
    }

    public String getParameters(String name) {
        Map<String, List<String>> parameters = getParameters();
        List<String> strings = parameters.get(name);
        if (strings != null) {
            return strings.get(0);
        } else {
            return null;
        }
    }


}
