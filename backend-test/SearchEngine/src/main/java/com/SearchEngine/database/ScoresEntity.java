package com.SearchEngine.database;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ScoresEntity {
    private String _id;
    private List<Double> score;
    private String url;
    private String original;
}
