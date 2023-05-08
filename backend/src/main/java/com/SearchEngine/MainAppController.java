package com.SearchEngine;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainAppController {

    @RequestMapping("/get-home")
    String getHome(){
        return "hi";
    }
}
