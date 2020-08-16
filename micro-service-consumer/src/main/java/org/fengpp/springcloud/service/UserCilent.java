package org.fengpp.springcloud.service;

import org.fengpp.springcloud.vo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "MICRO-SERVICE-PROVIDER", fallback = HystrixClientFallback.class)
public interface UserCilent {
    @RequestMapping("/provider/user/{id}")
        //user/1
    User getUser(@PathVariable("id") Long id);
}

@Component
class HystrixClientFallback implements UserCilent {
    @Override
    public User getUser(Long id) {
        System.out.println("熔断，默认回调函数");
        User u = new User(1L, "admin");
        return u;
    }
}
