package com.demo.netty.tomcat.sevlets;

import com.demo.netty.tomcat.http.GPRequest;
import com.demo.netty.tomcat.http.GPResponse;
import com.demo.netty.tomcat.http.GPservlet;

import java.io.UnsupportedEncodingException;

/**
 * MyServlet
 * <p>
 * liwenbin
 * 2019/4/5 9:56
 */
public class MyServlet extends GPservlet {
    @Override
    public void doGet(GPRequest request, GPResponse response) throws UnsupportedEncodingException {
        response.write(request.getParameters("name"), 200);
    }

    @Override
    public void doPost(GPRequest request, GPResponse response) {
        try {
            doGet(request, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
