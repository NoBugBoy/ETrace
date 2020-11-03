package com.agent.wsagent.ws;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;

public class Vm {
    private static  VirtualMachine VM;
    protected static void attach(String pid,boolean close,String port) {
        try {
            VM = VirtualMachine.attach(pid);
            if(!close){
                VM.loadAgent(System.getProperty("user.home") + File.separator + ".Dstop" + File.separator + "lib" + File.separator +"agent-1.1.jar",port);
            }
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
    public static boolean isLinux(){
        String property = System.getProperties().getProperty("os.name");
        if(property.contains("Linux")){
            return true;
        }
        return false;
    }
}
