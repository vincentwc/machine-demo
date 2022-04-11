package com.vincent.config;

import com.vincent.enums.VincentEvent;
import com.vincent.enums.VincentState;
import com.vincent.pojo.Order;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

/**
 * @author wang_cheng
 * @date 2022/04/11 16:48
 * @desc 订单状态机的伪持久化
 **/
@Component
public class OrderStateMachinePersist implements StateMachinePersist<VincentState, VincentEvent, Order> {


    @Override
    public void write(StateMachineContext<VincentState, VincentEvent> stateMachineContext, Order order) throws Exception {
        // 不做持久化工作
    }

    @Override
    public StateMachineContext<VincentState, VincentEvent> read(Order contextObj) throws Exception {
        StateMachineContext<VincentState, VincentEvent> result = new DefaultStateMachineContext<VincentState, VincentEvent>(
                VincentState.valueOf(contextObj.getState()),
                null,
                null,
                null,
                null,
                "orderMachine");
        return result;
    }
}
