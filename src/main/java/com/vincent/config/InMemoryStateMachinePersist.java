package com.vincent.config;

import com.vincent.enums.VincentEvent;
import com.vincent.enums.VincentState;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wang_cheng
 * @date 2022/04/11 14:21
 * @desc 在内存中持久化状态机
 **/
@Component
public class InMemoryStateMachinePersist implements StateMachinePersist<VincentState, VincentEvent,String> {

    private Map<String,StateMachineContext<VincentState,VincentEvent>> map = new HashMap<>();


    @Override
    public void write(StateMachineContext<VincentState, VincentEvent> stateMachineContext, String s) throws Exception {
        map.put(s,stateMachineContext);
    }

    @Override
    public StateMachineContext<VincentState, VincentEvent> read(String s) throws Exception {
        return map.get(s);
    }
}
