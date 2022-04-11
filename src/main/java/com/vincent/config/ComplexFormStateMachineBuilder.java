package com.vincent.config;

import com.vincent.action.ComplexFormChoiceAction;
import com.vincent.enums.ComplexFormEvents;
import com.vincent.enums.ComplexFormStates;
import com.vincent.guard.ComplexFormCheckChoiceGuard;
import com.vincent.guard.ComplexFormDealChoiceGuard;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * @author wang_cheng
 * @date 2022/04/11 17:05
 * @desc
 **/
@Component
public class ComplexFormStateMachineBuilder {

    private final static String MACHINEID = "complexFormMachine";

    /**
     * 构建状态机
     *
     * @param beanFactory
     * @return
     * @throws Exception
     */
    public StateMachine<ComplexFormStates, ComplexFormEvents> build(BeanFactory beanFactory) throws Exception {
        StateMachineBuilder.Builder<ComplexFormStates, ComplexFormEvents> builder = StateMachineBuilder.builder();

        System.out.println("构建复杂表单状态机");

        builder.configureConfiguration()
                .withConfiguration()
                .machineId(MACHINEID)
                .beanFactory(beanFactory);

        //
        builder.configureStates()
                .withStates()
                .initial(ComplexFormStates.BLANK_FORM)
                .choice(ComplexFormStates.CHECK_CHOICE)
                .choice(ComplexFormStates.DEAL_CHOICE)
                .states(EnumSet.allOf(ComplexFormStates.class));

        builder.configureTransitions()
                .withExternal()
                    .source(ComplexFormStates.BLANK_FORM).target(ComplexFormStates.FULL_FORM)
                    .event(ComplexFormEvents.WRITE)
                    .and()
                .withExternal()
                    .source(ComplexFormStates.FULL_FORM).target(ComplexFormStates.CHECK_CHOICE)
                    .event(ComplexFormEvents.CHECK)
                    .and()

//                .withExternal()
//                    .source(ComplexFormStates.FULL_FORM).target(ComplexFormStates.CHECK_CHOICE)
//                    .event(ComplexFormEvents.CHECK)
                // action可以插入多个，也就是多个单独的业务需要在这里执行
//                    .action(new ComplexFormChoiceAction(),new ComplexFormChoiceAction())
                // guard在这里恢复了本来的作用，保护状态变化，所以只能插入一个
//                    .guard(new ComplexFormCheckChoiceGuard())
//                    .and()
                /**
                 * withChoice()和跟随它的first()，last()
                 * 这两个代表的就是分支判断时TRUE和FALSE的状态流程去处,Guard就是用来做判断的
                 */
                .withChoice()
                    .source(ComplexFormStates.CHECK_CHOICE)
                    .first(ComplexFormStates.CONFIRM_FORM, new ComplexFormCheckChoiceGuard(),new ComplexFormChoiceAction())
                    .last(ComplexFormStates.DEAL_FORM,new ComplexFormChoiceAction())
                    .and()
                .withExternal()
                    .source(ComplexFormStates.CONFIRM_FORM).target(ComplexFormStates.SUCCESS_FORM)
                    .event(ComplexFormEvents.SUBMIT)
                    .and()
                .withExternal()
                    .source(ComplexFormStates.DEAL_FORM).target(ComplexFormStates.DEAL_CHOICE)
                    .event(ComplexFormEvents.DEAL)
                    .and()
                .withChoice()
                    .source(ComplexFormStates.DEAL_CHOICE)
                    .first(ComplexFormStates.FULL_FORM, new ComplexFormDealChoiceGuard())
                    .last(ComplexFormStates.FAILED_FORM);
        return builder.build();
    }
}
