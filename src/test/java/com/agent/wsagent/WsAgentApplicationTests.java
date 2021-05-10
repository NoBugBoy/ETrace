package com.agent.wsagent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

// @SpringBootTest
class WsAgentApplicationTests {

    @Test
    void contextLoads() {
         File f = new File("/Users/yujian/Desktop/a.html");
        try( FileInputStream inputStream = new FileInputStream(f);) {
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(bufferedInputStream));
                while (br.read() != -1 ){
                    System.out.println(Math.abs(hash(br.readLine()) % 100));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    int hash(String line){
        int h = 0;
        int base = 31;
        for (int i = 0; i < line.length(); i++) {
            h = base * h + line.charAt(i);
        }
        return h;
    }

}
