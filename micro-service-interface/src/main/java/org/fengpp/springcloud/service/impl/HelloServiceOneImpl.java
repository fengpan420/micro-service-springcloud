package org.fengpp.springcloud.service.impl;

import org.fengpp.springcloud.service.SPIHelloService;

public class HelloServiceOneImpl implements SPIHelloService {
    @Override
    public void helloSPI() {
        System.out.println("Hello SPI One！");
    }
}
