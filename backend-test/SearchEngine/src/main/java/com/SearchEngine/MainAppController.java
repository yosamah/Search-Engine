package com.SearchEngine;


import com.SearchEngine.database.WordsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
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
    List<WordsEntity> search(@RequestParam String searchedWord){
        return this.mainAppService.search(searchedWord);
    }
}
