package com.vincent.config;

import com.vincent.enums.VincentEvent;
import com.vincent.enums.VincentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;


@Configuration
@EnableStateMachine(name="orderSingleMachine")
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<VincentState, VincentEvent> {

	@Override
	public void configure(StateMachineStateConfigurer<VincentState, VincentEvent> states) throws Exception {
		states
				.withStates()
				.initial(VincentState.UNPAID)
				.states(EnumSet.allOf(VincentState.class));
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<VincentState, VincentEvent> transitions) throws Exception {
		transitions
				.withExternal()
					.source(VincentState.UNPAID)
					.target(VincentState.WAITING_FOR_RECEIVE)
					.event(VincentEvent.PAY)
					.and()
				.withExternal()
					.source(VincentState.WAITING_FOR_RECEIVE)
					.target(VincentState.DONE)
					.event(VincentEvent.RECEIVE);
	}
}