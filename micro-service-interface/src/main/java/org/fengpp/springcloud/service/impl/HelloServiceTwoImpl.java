package org.fengpp.springcloud.service.impl;

import org.fengpp.springcloud.annotation.SPIUse;
import org.fengpp.springcloud.service.SPIHelloService;

public class HelloServiceTwoImpl implements SPIHelloService {
    @Override
    @SPIUse(name = "helloSPITwo")
    public void helloSPI() {
        System.out.println("Hello SPI Two！");
    }
}
