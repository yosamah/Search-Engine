package com.SearchEngine.database;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Setter
@Getter
public class WebsiteEntity {

    private String id;
    private String url;
    private Double popularity;
    private List<String> outgoing_links;

    // Constructors

    public WebsiteEntity() {
    }

    public WebsiteEntity(String url, Double popularity, List<String> outgoingLinks) {
        this.url = url;
        this.popularity = popularity;
        this.outgoing_links = outgoingLinks;
    }
}
