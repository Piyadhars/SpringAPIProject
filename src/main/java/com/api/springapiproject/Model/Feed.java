package com.api.springapiproject.Model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Feed {
    private List<String> Title;
    private List<String> Description;
    private List<String> Image;
    private List<List<String>> Tag;


}
