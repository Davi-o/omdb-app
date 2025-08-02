package com.dvvz.omdb.service;

import com.dvvz.omdb.model.MovieResponse;
import com.dvvz.omdb.model.SearchResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootTest
class OmdbServiceTest {

    private RestTemplate restTemplate;
    private MovieResponse expectedMovie;
    private SearchResponse expectedResponse;
    private OmdbService service;

    @BeforeEach
    void setup(){
        restTemplate = Mockito.mock(RestTemplate.class);

        expectedMovie = new MovieResponse();
        expectedMovie.Title = "Matrix";
        expectedMovie.Year = "1993";
        expectedMovie.imdbID = "tt0106062";

        expectedResponse = new SearchResponse();
        expectedResponse.setMovies(List.of(expectedMovie));

        service = new OmdbService(
                restTemplate,
                "https://api.com",
                "123",
                new String[]{"short", "full"}
        );
    }

    @Test
    void shouldReturnMovieWhenCallOmdbApi() {
        Mockito.when(restTemplate.getForObject(
           Mockito.eq("https://api.com?apikey=123&t=matrix"),
           Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie(null, "matrix", null,null,null);

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenImdbIdShouldReturnMovieWhenCallOmdbApi() {
        Mockito.when(restTemplate.getForObject(
           Mockito.eq("https://api.com?apikey=123&i=101"),
           Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie("101", null, null,null,null);

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenPlotTypeShouldReturnMovieWhenCallOmdbApi() {
        Mockito.when(restTemplate.getForObject(
           Mockito.eq("https://api.com?apikey=123&t=matrix&plot=short"),
           Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie(null, "matrix", null,null,"short");

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenValidSearchQueryShouldReturnMovieListWhenCallOmdbApi() {
        SearchResponse expectedResponse = new SearchResponse();
        expectedResponse.setMovies(List.of(expectedMovie));

        Mockito.when(restTemplate.getForObject(
                Mockito.eq("https://api.com?apikey=123&s=matrix&page=1"),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(expectedResponse);

        List<MovieResponse> movies = service.searchMovies("matrix",1);

        Assertions.assertNotNull(movies);
        Assertions.assertEquals(1, movies.size());
        Assertions.assertEquals("Matrix", movies.getFirst().Title);
    }

}