package com.vincent.controller;

import com.vincent.config.ComplexFormStateMachineBuilder;
import com.vincent.config.FormStateMachineBuilder;
import com.vincent.config.OrderStateMachineBuilder;
import com.vincent.enums.ComplexFormEvents;
import com.vincent.enums.ComplexFormStates;
import com.vincent.enums.FormEvents;
import com.vincent.enums.FormStates;
import com.vincent.enums.VincentEvent;
import com.vincent.enums.VincentState;
import com.vincent.pojo.Form;
import com.vincent.pojo.Order;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wang_cheng
 * @date 2022/04/08 14:23
 * @desc
 **/
@RestController
@RequestMapping("/statemachine")
public class StateMachineController {

    @Resource
    private StateMachine orderSingleMachine;

    @Resource
    private BeanFactory beanFactory;

    @Resource
    private OrderStateMachineBuilder orderStateMachineBuilder;

    @Resource
    private FormStateMachineBuilder formStateMachineBuilder;

    @Resource(name = "orderMemoryPersister")
    private StateMachinePersister<VincentState, VincentEvent, String> orderMemorypersister;

    @Resource(name = "orderRedisPersister")
    private StateMachinePersister<VincentState, VincentEvent, String> orderRedisPersister;

    @Resource(name = "orderPersister")
    private StateMachinePersister<VincentState, VincentEvent, Order> persister;

    @Resource
    private ComplexFormStateMachineBuilder complexFormStateMachineBuilder;


    /**
     * 单状态机单实例的情况
     *
     * @EnableStateMachine(name="orderSingleMachine")这个注解 OrderSingleEventConfig状态机配置类
     * @WithStateMachine(name="orderSingleMachine") 这个注解 跟上一个状态机配置的关联关系
     */
    @GetMapping("/testOrderSingleState")
    public void testOrderSingleState() {

        // 创建流程
        orderSingleMachine.start();

        // 触发PAY事件
        orderSingleMachine.sendEvent(VincentEvent.PAY);

        // 触发RECEIVE事件
        orderSingleMachine.sendEvent(VincentEvent.RECEIVE);

        // 获取最终状态
        System.out.println("最终状态：" + orderSingleMachine.getState().getId());
    }


    /**
     * 单状态机多实例的情况，事件携带参数
     *
     * @param orderId
     * @throws Exception
     */
    @GetMapping("/testOrderState")
    public void testOrderState(String orderId) throws Exception {
        StateMachine<VincentState, VincentEvent> orderSingleMachine = orderStateMachineBuilder.build(beanFactory);

        // 创建流程
        orderSingleMachine.start();

        // 触发PAY事件
        orderSingleMachine.sendEvent(VincentEvent.PAY);

        // 触发RECEIVE事件  携带信息时，使用下面的方式
//        orderSingleMachine.sendEvent(VincentEvent.RECEIVE);

        Order order = new Order(orderId, "547568678", "广东省深圳市", "13435465465", "RECEIVE");
        Message<VincentEvent> message = MessageBuilder
                .withPayload(VincentEvent.RECEIVE)
                .setHeader("order", order)
                .setHeader("obj", "vincent")
                .build();
        orderSingleMachine.sendEvent(message);

        // 获取最终状态
        System.out.println("最终状态：" + orderSingleMachine.getState().getId());
    }


    /**
     * 多状态机多实例的情况【form跟order状态机同时存在是】，事件携带参数
     *
     * @throws Exception
     */
    @RequestMapping("/testFormState")
    public void testFormState() throws Exception {

        StateMachine<FormStates, FormEvents> stateMachine = formStateMachineBuilder.build(beanFactory);
        System.out.println(stateMachine.getId());

        // 创建流程
        stateMachine.start();

        stateMachine.sendEvent(FormEvents.WRITE);

        stateMachine.sendEvent(FormEvents.CONFIRM);

        stateMachine.sendEvent(FormEvents.SUBMIT);

        // 获取最终状态
        System.out.println("最终状态：" + stateMachine.getState().getId());
    }

    /**
     * 保存状态机【内存】
     *
     * @param id
     * @throws Exception
     */
    @GetMapping("/testMemoryPersister")
    public void tesMemorytPersister(String id) throws Exception {
        StateMachine<VincentState, VincentEvent> stateMachine = orderStateMachineBuilder.build(beanFactory);
        stateMachine.start();

        //发送PAY事件
        stateMachine.sendEvent(VincentEvent.PAY);
        Order order = new Order(id, "547568678", "广东省深圳市", "13435465465", "RECEIVE");

        //持久化stateMachine
        orderMemorypersister.persist(stateMachine, order.getOrderId());
    }

    /**
     * 取出状态机【内存】
     *
     * @param id
     * @throws Exception
     */
    @GetMapping("/testMemoryPersisterRestore")
    public void testMemoryRestore(String id) throws Exception {
        StateMachine<VincentState, VincentEvent> stateMachine = orderStateMachineBuilder.build(beanFactory);
        orderMemorypersister.restore(stateMachine, id);
        System.out.println("恢复状态机后的状态为：" + stateMachine.getState().getId());

    }

    /**
     * 保存状态机【redis】
     *
     * @param id
     * @throws Exception
     */
    @RequestMapping("/testRedisPersister")
    public void testRedisPersister(String id) throws Exception {
        StateMachine<VincentState, VincentEvent> stateMachine = orderStateMachineBuilder.build(beanFactory);
        stateMachine.start();

        Order order = new Order(id, "12345678", "广东省深圳市", "13435465465", "RECEIVE");
        //发送PAY事件
        Message<VincentEvent> message = MessageBuilder
                .withPayload(VincentEvent.PAY)
                .setHeader("order", order)
                .build();
        stateMachine.sendEvent(message);
        //持久化stateMachine
        orderRedisPersister.persist(stateMachine, order.getOrderId());
    }

    /**
     * 取出状态机【redis】
     *
     * @param id
     * @throws Exception
     */
    @RequestMapping("/testRedisPersisterRestore")
    public void testRestore(String id) throws Exception {
        StateMachine<VincentState, VincentEvent> stateMachine = orderStateMachineBuilder.build(beanFactory);
        orderRedisPersister.restore(stateMachine, id);
        System.out.println("恢复状态机后的状态为：" + stateMachine.getState().getId());
    }


    /**
     * 伪持久化和中间段状态机
     * @param id
     * @throws Exception
     */
    @RequestMapping("/testOrderRestore")
    public void testOrderRestore(String id) throws Exception {
        StateMachine<VincentState, VincentEvent> stateMachine = orderStateMachineBuilder.build(beanFactory);
        //订单
        Order order = new Order(id, "12345678", "广东省深圳市", "13435465465", "RECEIVE");
        order.setState(VincentState.WAITING_FOR_RECEIVE.toString());
        //恢复 用restore过了一手，就已经是一个到达order指定状态的状态机
        persister.restore(stateMachine, order);
        //查看恢复后状态机的状态
        System.out.println("恢复后的状态：" + stateMachine.getState().getId());
    }

    /**
     *
     * @throws Exception
     */
    @RequestMapping("/testComplexFormState")
    public void testComplexFormState() throws Exception {

        StateMachine<ComplexFormStates, ComplexFormEvents> stateMachine = complexFormStateMachineBuilder.build(beanFactory);
        System.out.println(stateMachine.getId());

        Form form1 = new Form();
        form1.setId("111");
        form1.setFormName(null);

        Form form2 = new Form();
        form2.setId("222");
        form2.setFormName("好的表单");

        Form form3 = new Form();
        form3.setId("333");
        form3.setFormName(null);

        // 创建流程
        System.out.println("-------------------form1------------------");

        stateMachine.start();
        Message message = MessageBuilder
                .withPayload(ComplexFormEvents.WRITE)
                .setHeader("form", form1)
                .build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());

        message = MessageBuilder
                .withPayload(ComplexFormEvents.CHECK
                ).setHeader("form", form1)
                .build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());

        message = MessageBuilder
                .withPayload(ComplexFormEvents.DEAL)
                .setHeader("form", form1)
                .build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());

        message = MessageBuilder
                .withPayload(ComplexFormEvents.SUBMIT)
                .setHeader("form", form1)
                .build();
        stateMachine.sendEvent(message);
        System.out.println("最终状态：" + stateMachine.getState().getId());

        System.out.println("-------------------form2------------------");
        stateMachine = complexFormStateMachineBuilder.build(beanFactory);
        stateMachine.start();
        message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form2).build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());
        message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form2).build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());
        message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form2).build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());
        message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form2).build();
        stateMachine.sendEvent(message);
        System.out.println("最终状态：" + stateMachine.getState().getId());

        System.out.println("-------------------form3------------------");
        stateMachine = complexFormStateMachineBuilder.build(beanFactory);
        stateMachine.start();
        message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form3).build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());
        message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form3).build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());
        form3.setFormName("好的表单");
        message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form3).build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());
        message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form3).build();
        stateMachine.sendEvent(message);
        message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form3).build();
        stateMachine.sendEvent(message);
        System.out.println("当前状态：" + stateMachine.getState().getId());
        message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form3).build();
        stateMachine.sendEvent(message);
        System.out.println("最终状态：" + stateMachine.getState().getId());
    }


}
