package org.fengpp.springcloud.service;

import org.fengpp.springcloud.vo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "MICRO-SERVICE-PROVIDER")
public interface UserCilent {
    @RequestMapping("/provider/user/{id}")
        //user/1
    User getUser(@PathVariable("id") Long id);
}
