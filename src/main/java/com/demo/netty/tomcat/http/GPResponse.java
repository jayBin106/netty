package com.demo.netty.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.StringUtil;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * GPResponse
 * <p>
 * liwenbin
 * 2019/4/5 9:55
 */
public class GPResponse {
    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public GPResponse() {
    }

    public GPResponse(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public void write(String out, int i) throws UnsupportedEncodingException {
        try {
            if (!StringUtils.isEmpty(out)) {
                HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK, Unpooled.wrappedBuffer(out.getBytes("UTF-8")));
                response.headers().set(CONTENT_TYPE, "text/json");
                response.headers().set(CONTENT_LENGTH, ((DefaultFullHttpResponse) response).content().readableBytes());
                response.headers().set(EXPIRES, 0);
                //判断是否是长链接
                if (HttpHeaders.isKeepAlive(request)) {
                    response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                }
                ctx.write(response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ctx.flush();
        }
    }
}
