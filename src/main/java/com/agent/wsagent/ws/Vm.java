package com.agent.wsagent.ws;

import com.agent.wsagent.CheckAgent;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;

public class Vm {
    private static  VirtualMachine VM;
    protected static void attach(String pid,String param) {
        try {
            VM = VirtualMachine.attach(pid);
            // VM.loadAgent("/home/java-agent-1.2.2-jar-with-dependencies.jar",param);
               VM.loadAgent(System.getProperty("user.home") + File.separator + ".Dstop"  + File.separator +"agent-"+ CheckAgent.version+".jar",param);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(VM!=null){
                try {
                    VM.detach();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
