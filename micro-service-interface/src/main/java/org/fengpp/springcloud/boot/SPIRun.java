package org.fengpp.springcloud.boot;

import org.fengpp.springcloud.annotation.SPIUse;
import org.fengpp.springcloud.service.SPIHelloService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ServiceLoader;
import java.util.concurrent.*;

public class SPIRun {

    public void spiExcute() {
        ServiceLoader<SPIHelloService> serviceLoaders = ServiceLoader.load(SPIHelloService.class);
        for (SPIHelloService helloService : serviceLoaders) {
            Method[] methods = helloService.getClass().getDeclaredMethods();
            for (Method method : methods) {
                SPIUse spiUse = method.getAnnotation(SPIUse.class);
                if (spiUse != null && "helloSPITwo".equals(spiUse.name())) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " $ " + helloService);
                        method.invoke(helloService);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
    }
}
