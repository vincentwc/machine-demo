package com.vincent.action;

import com.vincent.enums.ComplexFormEvents;
import com.vincent.enums.ComplexFormStates;
import com.vincent.pojo.Form;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @author wang_cheng
 * @date 2022/04/11 17:25
 * @desc
 **/
public class ComplexFormChoiceAction implements Action<ComplexFormStates, ComplexFormEvents> {


    @Override
    public void execute(StateContext<ComplexFormStates, ComplexFormEvents> stateContext) {
        System.out.println("into ComplexFormChoiceAction");
        Form form = stateContext.getMessage().getHeaders().get("form", Form.class);
        System.out.println(form);
        System.out.println(stateContext.getStateMachine().getState());
    }
}
