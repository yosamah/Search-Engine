package com.SearchEngine;


import com.SearchEngine.database.MongoDBService;
import com.SearchEngine.database.MongoDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainAppController {
    @Autowired
    MainAppService mainAppService;
    @RequestMapping("/get-home")
    String getHome(){
//        this.mongoDBService.updateDetailsScore();
        System.out.println("lolllerr");
        return "hi";
    }

    @GetMapping("/search")
    List<MongoDbEntity> search( @RequestParam String searchedWord,
                                @RequestParam int pageNum){
        return this.mainAppService.search(searchedWord, pageNum);
    }
}
