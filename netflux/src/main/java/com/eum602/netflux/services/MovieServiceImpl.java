package com.eum602.netflux.services;

import com.eum602.netflux.domain.Movie;
import com.eum602.netflux.domain.MovieEvent;
import com.eum602.netflux.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public Mono<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    @Override
    public Flux<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Flux<MovieEvent> streamMovieEvents(String id) {
        return Flux.<MovieEvent>generate(movieEventSynchronousSink -> {
            movieEventSynchronousSink.next(new MovieEvent(id, new Date()));
        }).delayElements(Duration.ofSeconds(1)); //emit a new movie event every second
    }
}
