package com.silu.tcpserver;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by piguangtao on 15/5/31.
 */
public class Startup {
    public static void main(String[] args) {
        final AbstractXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Runtime.getRuntime().addShutdownHook(new Thread("shutdown-hook"){
            public void run(){
                context.close();
            }
        });
    }
}
