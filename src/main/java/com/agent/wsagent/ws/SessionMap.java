package com.agent.wsagent.ws;

import javax.websocket.Session;
import java.util.HashMap;

/**
 * author yujian
 * description
 * create 2021-05-10 10:42
 **/
public class SessionMap extends HashMap<String, Session> {
    @Override
    public Session get(Object key) {
        Session session = super.get(key);
        if(session!= null && session.isOpen()){
            return session;
        }
        remove(key);
        return null;
    }
}
