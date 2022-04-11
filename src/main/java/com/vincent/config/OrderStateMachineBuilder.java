package com.vincent.config;

import com.vincent.enums.VincentEvent;
import com.vincent.enums.VincentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * @author wang_cheng
 * @date 2022/04/08 14:48
 * @desc
 **/
@Component
@Slf4j
public class OrderStateMachineBuilder {

    private final static String MACHINEID = "orderMachine";

    /**
     *
     * @param beanFactory
     * @return
     * @throws Exception
     */
    public StateMachine<VincentState, VincentEvent> build(BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<VincentState, VincentEvent> builder = StateMachineBuilder.builder();
        System.out.println("构建订单状态机");

        builder.configureConfiguration()
                .withConfiguration()
                // MACHINEID是状态机配置类和事件实现类的关联
                .machineId(MACHINEID)
                .beanFactory(beanFactory);

        // 初始状态
        builder.configureStates()
                .withStates()
                .initial(VincentState.UNPAID)
                .states(EnumSet.allOf(VincentState.class));

        // 状态扭转
        builder.configureTransitions()
                .withExternal()
                .source(VincentState.UNPAID).target(VincentState.WAITING_FOR_RECEIVE)
                .event(VincentEvent.PAY).action(action())
                .and()
                .withExternal()
                .source(VincentState.WAITING_FOR_RECEIVE).target(VincentState.DONE)
                .event(VincentEvent.RECEIVE);

        return builder.build();
    }


    @Bean
    public Action<VincentState, VincentEvent> action() {
        return context -> System.out.println(context);
    }
}
