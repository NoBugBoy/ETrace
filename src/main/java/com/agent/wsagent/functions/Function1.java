package com.agent.wsagent.functions;
@FunctionalInterface
public interface Function1<T,T1,R> {
    R apply(T t,T1 t1);
}
