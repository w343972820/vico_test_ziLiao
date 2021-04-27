package com.github.houbb.junitperf.vico;

import com.alibaba.fastjson.JSONObject;
import com.github.houbb.junitperf.util.SimpleWss;
import org.databene.contiperf.*;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.nio.ByteBuffer;


public class TestOne {
        @Rule
        public ContiPerfRule i = new ContiPerfRule();
        @Test
        @PerfTest(invocations = 1000, threads = 100)
        @Required(totalTime = 60000)
        public void test1() throws Exception {
            JSONObject json= new JSONObject();
            json.put("sub","market.ethusdt.timetransa");
            json.put("id",null);
            String message = json.toJSONString();
            new SimpleWss(new URI("wss://api.fexvip.co/websocket")) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("onPen"+serverHandshake.toString());

                    this.send(message);
                }

                @Override
                public void onMessage(String s) {
                    System.out.println("onMessage:"+ s );
                }
                @Override
                public void onMessage(ByteBuffer bytes) {
                    // TODO Auto-generated method stub
                    String messa = new String(bytes.array());
                    System.out.println("messa:"+messa);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("close");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("error");
                }
            }.connect();


        }


}
