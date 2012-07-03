package com.thompson234.bg95.service;

import com.thompson234.bg95.model.SearchResult;

public interface SearchService {

    void buildIndex();
    SearchResult search(String query);
}
