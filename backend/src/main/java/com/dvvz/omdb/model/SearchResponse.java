package com.dvvz.omdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchResponse {
    @JsonProperty("Search")
    private List<MovieResponse> movies;

    @JsonProperty("totalResults")
    private String totalResults;

    public void setMovies(List<MovieResponse> movies) {
        this.movies = movies;
    }

    public List<MovieResponse> getMovies() {
        return movies;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getTotalResults() {
        return totalResults;
    }


}
