package com.agent.wsagent.ws;

import cn.hutool.http.HttpUtil;
import com.agent.wsagent.functions.SwitchInterface;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public enum CommandEnum implements SwitchInterface {
    //
    IP{
        @Override
        public void execute(String command) {
            String ip = HttpUtil.get("http://ip.sb");
            WebsocketCenter.sendToAgent(ip);
        }
    },
    MONITOR{
        @Override
        public void execute(String command) {
            WebsocketCenter.sendToAgent(command);
        }
    },
    THREADS{
        @Override
        public void execute(String command) {
            WebsocketCenter.sendToAgent(command);
        }
    },
    THREADSTACK{
        @Override
        public void execute(String command) {
            WebsocketCenter.sendToAgent(command);
        }
    },
    AOP{
        @Override
        public void execute(String command) {
            String[] split = command.split(" ");
            if(split.length == 3){
                WebsocketCenter.sendToAgent(command);
            }else{
                WebsocketCenter.sendToClient("请补全参数");
            }
        }
    },
    JPS{
        @Override
        public void execute(String command) {
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
                        WebsocketCenter.jpss.add(str.split(" ")[0]);
                    }
                }
                WebsocketCenter.sendToClient(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
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
        }
    },
    PID{
        @Override
        public void execute(String command) {
            String[] split = command.split("=");
            if(split.length == 2){
                boolean contains = WebsocketCenter.jpss.contains(split[1]);
                if(contains){
                    // if(WebsocketCenter.pid == null){
                        WebsocketCenter.pid = split[1];
                    Session session = WebsocketCenter.stringSessionMap.get(WebsocketCenter.AGENT);
                    if(session == null){
                        Vm.attach(split[1], "1");
                    }else{
                        Vm.attach(split[1], "0");
                    }

                    // }
                }else{
                    WebsocketCenter.sendToClient("请输入正确的进程ID");
                }
            }
        }
    },
    CPUTOP{
        @Override
        public void execute(String command) {
            String[] split = command.split(" ");
            if(split.length == 2){
                WebsocketCenter.sendToAgent(buildTopCommand.apply(split[1]));
            }
        }
    },
    STACK{
        @Override
        public void execute(String command) {
            String[] split = command.split(" ");
            if(split.length == 2){
                WebsocketCenter.sendToAgent(buildStackCommand.apply(split[1],WebsocketCenter.pid));
            }
        }
    },
    EXPORT{
        @Override
        public void execute(String command) {
            String[] split   = command.split(" ");
            String className = split[1];
            String  outputDir = split[2];
            WebsocketCenter.sendToAgent(buildExportCommand.apply(className,outputDir));
        }
    },
    TIME{
        @Override
        public void execute(String command) {
            String[] split   = command.split(" ");
            if(split.length > 1){
                String className = split[1];
                String methodName = "";
                if(split.length == 3){
                    methodName = split[2];
                }
                WebsocketCenter.sendToAgent(buildTimeCommand.apply(className,methodName));
            }
        }
    },
    COMPILE{
        @Override
        public void execute(String command) {
            String[] split   = command.split(" ");
            String outputDir = split[1];
            String javaFile = split[2];
            WebsocketCenter.sendToAgent(buildCompileCommand.apply(outputDir,javaFile,WebsocketCenter.getJarPath()));
        }
    },
    REDEFINE{
        @Override
        public void execute(String command) {
            String[] split   = command.split(" ");
            String classFile = split[1];
            WebsocketCenter.sendToAgent(buildRedefinedCommand.apply(classFile));
        }
    },
    JAVAINFO{
        @Override
        public void execute(String command) {
            WebsocketCenter.sendToAgent(command);
        }
    };
    public synchronized static void select(String command){
        if(StringUtils.isEmpty(command)){
            return;
        }
        for (CommandEnum value : values()) {
            if(command.toUpperCase().startsWith(value.name())){
                value.execute(command);
                break;
            }
        }
    }

}
