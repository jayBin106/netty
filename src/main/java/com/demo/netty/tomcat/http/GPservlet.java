package com.demo.netty.tomcat.http;

import java.io.UnsupportedEncodingException;

/**
 * GPservlet
 * <p>
 * liwenbin
 * 2019/4/5 9:53
 *
 * 抽象方法
 */
public abstract class GPservlet {
    public abstract void doGet(GPRequest request, GPResponse response) throws UnsupportedEncodingException;

    public abstract void doPost(GPRequest request, GPResponse response);
}
