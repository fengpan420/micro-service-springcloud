package org.fengpp.springcloud.controller;

import org.fengpp.springcloud.service.UserCilent;
import org.fengpp.springcloud.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/consumer")
public class UserController {

    @Autowired
    private UserCilent userCilent;

    //多个方法调用只需改一处就ok
    public static  final String URL_PREFIX ="http://MICRO-SERVICE-PROVIDER";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/user/{id}")
    public User getUser(@PathVariable("id") Long id) {
        //调用远程服务 http请求
//        String url = URL_PREFIX + "/provider/user/" + id;
//        return restTemplate.getForObject(url, User.class);
        return userCilent.getUser(id);
    }
}
