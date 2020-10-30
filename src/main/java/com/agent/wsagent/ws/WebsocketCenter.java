package com.agent.wsagent.ws;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/{id}")
@Component
@Slf4j
public class WebsocketCenter {
    @Value("${server.port}")
    private       String port;
    private static volatile String pid = "";
    private static volatile boolean close = false;
    private  static  final Map<String, Session> stringSessionMap = new ConcurrentHashMap<String, Session>();
    private  static  final Map<Session, String> SessionMap = new ConcurrentHashMap<>();
    private static final   List<String>         jpss       = new ArrayList<>();
    @OnClose
    public void OnClose(){
        close = false;
        String s = SessionMap.get(this);
        if(Strings.isNotEmpty(s)){
            stringSessionMap.remove(s);
        }
        SessionMap.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable error){
        OnClose();
    }
    /**
     *
     * @param session
     * @param id ws为web端，java为agent端
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("id") String id) throws IOException {
        if("ws".equals(id)){
            stringSessionMap.put("ws",session);
            session.getBasicRemote().sendText("连接成功，等待发送指令");
        }else if("java".equals(id)){
            stringSessionMap.put("java",session);
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException {
        //找到占cpu大的线程在干什么
        //占有内存大的对象和数量 不stw
        //清理日志不影响输出

        if(message.equals("java")){
            //由agent发送回来
            stringSessionMap.put("java",session);
            Session ws = stringSessionMap.get("ws");
            if(ws!=null){
                ws.getBasicRemote().sendText("agent已经准备就绪");
            }
            return;
        }
        if(message.equals("rejava")){
            log.info("agent已经存在,接收到重连信息，重连成功..");
            close = true;
            stringSessionMap.put("java",session);
            Session ws = stringSessionMap.get("ws");
            if(ws!=null){
                ws.getBasicRemote().sendText("agent已经准备就绪");
            }
            return;
        }
        //mg 开头意味着是agent回复的消息
        if(message.startsWith("mg")){
            String msg = message.substring(2);
            System.out.println(msg);
            Session ws       = stringSessionMap.get("ws");
            ws.getBasicRemote().sendText(msg);
            return;
        }
        if("ip".equals(message)){
            String ip = HttpUtil.get("http://pv.sohu.com/cityjson?ie=utf-8");
            ip = ip.replace("var returnCitySN = ","");
            Session     ws    = stringSessionMap.get("ws");
            ws.getBasicRemote().sendText(ip);
            return;
        }
        if("jps".equalsIgnoreCase(message)){
            InputStreamReader inputStreamReader = null;
            BufferedReader    bufferedReader    = null;
            try {
                Process jps = Runtime.getRuntime().exec("jps -l");
                inputStreamReader = new InputStreamReader(jps.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb  = new StringBuilder();
                String        str ="";
                while((str = bufferedReader.readLine()) !=null){
                    if(str.split(" ").length==2 &&
                        !str.split(" ")[1].equalsIgnoreCase("Launcher") &&
                        !str.split(" ")[1].equalsIgnoreCase("Jps") &&
                        !str.split(" ")[1].startsWith("RemoteMaven")){
                        sb.append(str).append("\n");
                        jpss.add(str.split(" ")[0]);
                    }

                }
                Session     java    = stringSessionMap.get("ws");
                if(java!=null){
                    java.getBasicRemote().sendText(sb.toString());
                }else{
                    sendError(session);
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendError(session);
            }finally {
                if(inputStreamReader!=null){
                    try {
                        inputStreamReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(bufferedReader!=null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return;
        }
        if(message.startsWith("pid")){
            String[] split = message.split("=");
            Session ws = stringSessionMap.get("ws");
            if(split.length == 2){
                try {
                    boolean contains = jpss.contains(split[1]);
                    if(contains){
                        pid = split[1];
                        if (close) {
                            ws.getBasicRemote().sendText("The agent is ready!");
                        }
                        Vm.attach(split[1], close,port);
                    }else{
                        ws.getBasicRemote().sendText("请输入正确的进程ID");
                    }

                } catch (IOException e) {
                   e.printStackTrace();
                }

            }
        }
        if(message.startsWith("cpuTop")){
                String[] split = message.split(" ");
                if(split.length == 2){
                    Session java = stringSessionMap.get("java");
                    if(java!=null){
                        java.getBasicRemote().sendText("top "+split[1]);
                    }else{
                        sendError(session);
                    }
                    return;
            }
        }
        if(message.startsWith("stack")){
            String[] split = message.split(" ");
            if(split.length == 2){
                Session java = stringSessionMap.get("java");
                if(java!=null){
                    java.getBasicRemote().sendText("stack "+split[1]+" "+pid);
                }else{
                    sendError(session);
                }
                return;
            }
        }
        if(message.startsWith("class")){
            Session java = stringSessionMap.get("java");
            if(java!=null){
                java.getBasicRemote().sendText("class");
            }else{
                sendError(session);
            }
            return;
        }
        if(message.startsWith("export")){
            String[] split   = message.split(" ");
            String className = split[1];
            String outputDir = split[2];
            Session java = stringSessionMap.get("java");
            if(java!=null){
                java.getBasicRemote().sendText("export "+className+" > "+outputDir);
            }else{
                sendError(session);
            }
            return;
        }
        if(message.startsWith("time")){
            String[] split   = message.split(" ");
            if(split.length <= 1){
                sendError(session);
                return;
            }
            String className = split[1];
            String methodName = "";
            if(split.length == 3){
                methodName = split[2];
            }

            Session java = stringSessionMap.get("java");
            if(java!=null){
                java.getBasicRemote().sendText("time "+className+" "+methodName);
            }else{
                sendError(session);
            }
            return;
        }
        if(message.startsWith("compile")){
            String[] split   = message.split(" ");
            String outputDir = split[1];
            String javaFile = split[2];
            Session java = stringSessionMap.get("java");
            if(java!=null){
                java.getBasicRemote().sendText("compile "+outputDir+" "+javaFile+" "+getJarPath());
            }else{
                sendError(session);
            }
            return;
        }
        if(message.startsWith("redefine")){
            String[] split   = message.split(" ");
            String classFile = split[1];
            Session java = stringSessionMap.get("java");
            if(java!=null){
                java.getBasicRemote().sendText("redefine "+classFile);
            }else{
                sendError(session);
            }
            return;
        }
        //export com.SS > /Users/yujian/DeskTop/
        //compile /Users/yujian/Desktop/test/ /Users/yujian/Desktop/SS.java
        //redefine /Users/yujian/Desktop/test/com/SS.class

    }
    public void sendError(Session session){
        try {
            session.getBasicRemote().sendText("请先agent到Java进程");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getJarPath(){
        ApplicationHome h    = new ApplicationHome(getClass());
        return h.getSource().getParentFile().toString();
    }

}
