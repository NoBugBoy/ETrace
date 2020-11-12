package com.agent.wsagent.comands;

import com.agent.wsagent.functions.Function1;
import com.agent.wsagent.functions.Function2;

import java.util.function.Function;

public interface CommandConstructor extends FunctionKey {

    Function<String,String>         buildTopCommand   = r -> String.format(TOP,r);
    Function1<String,String,String> buildStackCommand = (r1,r2) -> String.format(STACK,r1,r2);
    Function1<String,String,String> buildExportCommand = (r1,r2) -> String.format(EXPORT,r1,r2);
    Function1<String,String,String> buildTimeCommand = (r1,r2) -> String.format(TIME,r1,r2);
    Function2<String,String,String,String> buildCompileCommand = (r1,r2,r3) -> String.format(COMPILE,r1,r2,r3);
    Function<String,String> buildRedefinedCommand = r -> String.format(REDEFINED,r);

}
