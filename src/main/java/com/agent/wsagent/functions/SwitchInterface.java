package com.agent.wsagent.functions;

import com.agent.wsagent.comands.CommandConstructor;

public interface SwitchInterface extends CommandConstructor {
    void execute(String command);
}
