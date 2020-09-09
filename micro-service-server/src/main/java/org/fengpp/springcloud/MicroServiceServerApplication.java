package org.fengpp.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Fengpp
 * @CreateDate 2020-8-15 17:11:32
 */
@SpringBootApplication
@EnableEurekaServer
//服务注册中心入口类
public class MicroServiceServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroServiceServerApplication.class, args);
    }
}
