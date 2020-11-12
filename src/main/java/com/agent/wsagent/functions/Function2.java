package com.agent.wsagent.functions;
@FunctionalInterface
public interface Function2<T,T1,T2,R> {
    R apply(T t,T1 t1,T2 T2);
}
