package com.agent.wsagent.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/{id}")
@Component
@Slf4j
public class WebsocketCenter {
    @Value("${server.port}")
    private       String port;
    public static volatile String pid = "";
    public static final String AGENT= "java";
    public static final String CLIENT = "ws";
    public   static  final SessionMap stringSessionMap = new SessionMap();
    public static final   List<String>         jpss       = new ArrayList<>();
    @OnOpen
    public void onOpen(Session session,@PathParam("id") String id) throws IOException {
        if(id.equals(CLIENT)){
            stringSessionMap.put(CLIENT,session);
            session.getBasicRemote().sendText("连接成功，等待发送指令");
        }else if(AGENT.equals(id)){
            stringSessionMap.put(AGENT,session);
        }
    }
    public static void sendToAgent(String command){
        try {
            Session session = stringSessionMap.get(AGENT);
            if(session == null){
              sendError(stringSessionMap.get(CLIENT));
            }else{
                session.getBasicRemote().sendText(command);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void sendToClient(String command){
        try {
            Session session = stringSessionMap.get(CLIENT);
            session.getBasicRemote().sendText(command);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException {
        if("reconnection".equals(message) || AGENT.equals(message)){
            stringSessionMap.put(AGENT,session);
            sendToClient("agent已经准备就绪");
        }
        if(message.startsWith("mg")){
            String msg = message.substring(2);
            sendToClient(msg);
            return;
        }
        if(message.startsWith("ds")){
            sendToClient(message);
            return;
        }
        CommandEnum.select(message);

    }
    @OnError
    public void onError(Session session, Throwable error) {
    }

    public static void  sendError(Session session){
        try {
            session.getBasicRemote().sendText("未Attach或目标进程已关闭");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String getJarPath(){
        ApplicationHome h    = new ApplicationHome(WebsocketCenter.class);
        return h.getSource().getParentFile().toString();
    }

}
