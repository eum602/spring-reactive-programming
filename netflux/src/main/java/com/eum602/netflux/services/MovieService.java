package com.eum602.netflux.services;

import com.eum602.netflux.domain.Movie;
import com.eum602.netflux.domain.MovieEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {
    Mono<Movie> getMovieById(String id);

    Flux<Movie> getAllMovies();

    Flux<MovieEvent> streamMovieEvents(String id);
}
