package com.vincent.guard;

import com.vincent.enums.ComplexFormEvents;
import com.vincent.enums.ComplexFormStates;
import com.vincent.pojo.Form;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

/**
 * @author wang_cheng
 * @date 2022/04/11 17:17
 * @desc
 **/
public class ComplexFormDealChoiceGuard implements Guard<ComplexFormStates, ComplexFormEvents> {


    @Override
    public boolean evaluate(StateContext<ComplexFormStates, ComplexFormEvents> stateContext) {
        System.out.println("ComplexFormDealChoiceGuard!!!!!!!!!!!!!");
        boolean returnValue;
        Form form = stateContext.getMessage().getHeaders().get("form", Form.class);

        if (form == null || (form.getFormName() == null)||(form.getFormName().indexOf("坏") > -1)) {
            returnValue = false;
        } else {
            returnValue = true;
        }

        System.out.println(form.toString()+" is "+returnValue);
        return returnValue;

    }
}
