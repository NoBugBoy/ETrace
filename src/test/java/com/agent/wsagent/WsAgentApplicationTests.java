package com.agent.wsagent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ConcurrentHashMap;

// @SpringBootTest
class WsAgentApplicationTests {

    @Test
    void contextLoads() {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("mykey","123");
    }

}
