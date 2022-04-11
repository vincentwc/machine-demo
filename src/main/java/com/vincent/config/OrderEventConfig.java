package com.vincent.config;

import com.vincent.enums.VincentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnStateChanged;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

/**
 * @author wang_cheng
 * @date 2022/04/08 09:38
 * @desc
 * 这个id对应的就是OrderStateMachineBuilder 里面的MACHINEID
 **/
@Slf4j
@WithStateMachine(id = "orderMachine")
public class OrderEventConfig {

    @OnTransition(target = "UNPAID")
    public void create() {
        log.info("---订单创建，待支付---");
    }

    /**
     * UNPAID->WAITING_FOR_RECEIVE 执行的动作
     */
    @OnTransition(source = "UNPAID", target = "WAITING_FOR_RECEIVE")
    @OnStateChanged
    public void pay() {
        log.info("---用户完成支付，待收货---");
    }

    /**
     * WAITING_FOR_RECEIVE->DONE 执行的动作
     */
    @OnTransition(source = "WAITING_FOR_RECEIVE", target = "DONE")
    public void receive(Message<VincentEvent> message) {
        System.out.println("传递的参数：" + message.getHeaders().get("order"));
        System.out.println("传递的参数：" + message.getHeaders().get("obj"));
        log.info("---用户已收货，订单完成---");
    }
}
