package com.SearchEngine.database;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class WebsiteEntity {

    private String id;
    private String url;                     // The url of the page
    private Double popularity;              // The popularity of the page
    private List<String> outgoingLinks;    // The outgoing links of the page (other links it points to)

    // Constructors

    public WebsiteEntity() {
    }

    public WebsiteEntity(String url, Double popularity, List<String> outgoingLinks) {
        this.url = url;
        this.popularity = popularity;
        this.outgoingLinks = outgoingLinks;
    }
}
