package com.agent.wsagent;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
@Slf4j
public class CheckAgent {

    // @PostConstruct
    public void downloadAgent(){
        File file = new File(System.getProperty("user.home") + File.separator + ".Dstop" );
        try {
            if(!file.exists()){
                file.mkdirs();
            }
            File jarFile = new File(System.getProperty("user.home") + File.separator + ".Dstop"  + File.separator +"agent-1.1.jar");
            if(!jarFile.exists()){
                String url = "https://imageyu.oss-cn-beijing.aliyuncs.com/agent-1.1.jar";
                HttpUtil.downloadFile(url, FileUtil.file(file.getAbsolutePath()), new StreamProgress() {

                    @Override
                    public void start() {
                        log.info("开始下载agent.......");
                    }

                    @Override
                    public void progress(long progressSize) {
                    }

                    @Override
                    public void finish() {
                        log.info("下载完成！");
                    }
                });

            }

        } catch (Throwable t) {
        }
    }
}
