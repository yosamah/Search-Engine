package com.SearchEngine;


import com.SearchEngine.database.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainAppController {
    @Autowired
    MongoDBService mongoDBService;
    @RequestMapping("/get-home")
    String getHome(){
//        this.mongoDBService.updateDetailsScore();
        System.out.println("lolllerr");
        return "hi";
    }
}
