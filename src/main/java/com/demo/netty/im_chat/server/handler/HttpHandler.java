package com.demo.netty.im_chat.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * HttpHandler
 * <p>
 * liwenbin
 * 2019/4/5 11:01
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static Logger LOG = Logger.getLogger(HttpHandler.class);

    //获取类路径
    private URL baseURL = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();

    //获取请求的文件
    private File getFileFromRooe(String fileNamme) {
        try {
            String path = baseURL.toURI() + "static/" + fileNamme;
            path = !path.contains("file:") ? path : path.substring(5);
            path = path.replaceAll("//", "/");
            return new File(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    //用来来接http请求  netty只要是方法后加0的，都是实现类的方法，不是接口
    //可以拿到处理后的结果
    //在netty中提供了，非常丰富的工具类
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //获得客户端请求的url，获取的是localhost：8080端口后面的数据
        String uri = request.getUri();
        String page = uri.equals("/") ? "chat.html" : uri;
        RandomAccessFile file = null;//读的方式
        try {
            File fileFromRooe = getFileFromRooe(page);
            file = new RandomAccessFile(fileFromRooe, "r");
        } catch (Exception ex) {
            //重点。。。。。。。。。。。。。，巨坑。。。。。。。。。。。。。。。
            ctx.fireChannelRead(request.retain());
            return;
        }

//        System.out.println(file);
        //写出文件 ！！！！！！

        HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
        String contextType = "text/html;";
        if (uri.endsWith(".css")) {
            contextType = "text/css;";
        } else if (uri.endsWith(".js")) {
            contextType = "text/javascript;";
        } else if (uri.toLowerCase().matches("(jpg|png|gif)$")) {
            String ext = uri.substring(uri.lastIndexOf("."));
            contextType = "image/" + ext;
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contextType + "charset=utf-8;");
        //判断是否是长链接
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));

        //如果不是场链接
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

        file.close();
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel client = ctx.channel();
        LOG.info("Client:" + client.remoteAddress() + "异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
