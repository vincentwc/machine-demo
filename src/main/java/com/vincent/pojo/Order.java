package com.vincent.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wang_cheng
 * @date 2022/04/08 15:58
 * @desc
 **/
@Data
@AllArgsConstructor
public class Order {

    private String orderId;
    private String addrCode;
    private String addr;
    private String phoneNum;
    private String state;
}
