package com.vincent.guard;

import com.vincent.enums.ComplexFormEvents;
import com.vincent.enums.ComplexFormStates;
import com.vincent.pojo.Form;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

/**
 * @author wang_cheng
 * @date 2022/04/11 17:13
 * @desc
 **/
public class ComplexFormCheckChoiceGuard implements Guard<ComplexFormStates, ComplexFormEvents> {


    @Override
    public boolean evaluate(StateContext<ComplexFormStates, ComplexFormEvents> stateContext) {
        boolean returnValue;
        Form form = stateContext.getMessage().getHeaders().get("form", Form.class);
        if (form == null || form.getFormName() == null) {
            returnValue = false;
        } else {
            returnValue = true;
        }
        return returnValue;
    }
}
