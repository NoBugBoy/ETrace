package com.agent.wsagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WsAgentApplication {

    public static void main(String[] args) {
        System.setProperty("-Xbootclasspath/a","$JAVA_HOME/lib/tools.jar");
        SpringApplication.run(WsAgentApplication.class, args);

    }

}
