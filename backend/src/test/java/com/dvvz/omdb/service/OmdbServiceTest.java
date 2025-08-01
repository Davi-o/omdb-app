package com.dvvz.omdb.service;

import com.dvvz.omdb.model.MovieResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

class OmdbServiceTest {

    private RestTemplate template;
    private MovieResponse expectedMovie;

    @BeforeEach
    void setup(){
        template = Mockito.mock(RestTemplate.class);

        expectedMovie = new MovieResponse(
                "Matrix",
                "N/A",
                "1993",
                "tt0106062"
        );
    }

    @Test
    void shouldReturnMovieWhenCallOmdbApi() {
        Mockito.when(template.getForObject(
           Mockito.eq("https://api.com?apikey=123&t=matrix"),
           Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        OmdbService service = new OmdbService(template,"https://api.com","123");

        MovieResponse result = service.getMovie(null, "matrix", null,null,null);

        Assertions.assertEquals(expectedMovie, result);
    }

}