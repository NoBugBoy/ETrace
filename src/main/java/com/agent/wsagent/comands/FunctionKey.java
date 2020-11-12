package com.agent.wsagent.comands;

public interface FunctionKey extends BaseKey{
    String TOP = "top" + space + placeholder;
    String REDEFINED = "redefine" + space + placeholder;
    String STACK = "top" + space + placeholder + space + placeholder;
    String EXPORT = "export" + space + placeholder  + " > "  + placeholder;
    String TIME = "time" + space + placeholder + space  + placeholder;
    String COMPILE = "compile" + space + placeholder + space  + placeholder + space + placeholder;
}
