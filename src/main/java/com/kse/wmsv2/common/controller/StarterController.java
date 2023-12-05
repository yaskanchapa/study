package com.kse.wmsv2.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class StarterController {

    @GetMapping("/starter")
    public String printMessage(){
        return "環境構築成功";
    }
}
