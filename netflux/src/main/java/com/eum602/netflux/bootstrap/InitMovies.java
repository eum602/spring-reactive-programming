package com.eum602.netflux.bootstrap;

import com.eum602.netflux.domain.Movie;
import com.eum602.netflux.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class InitMovies implements CommandLineRunner {
    private final MovieRepository movieRepository;
    @Override
    public void run(String... args) throws Exception {
        movieRepository.deleteAll()
                .thenMany(
                        Flux.just("Movie 1", "Aeon Flux", "Interstellar", "Who want bo be a millionaire", "Fathers and Daughters")
                        .map(Movie::new)
                        .flatMap(movieRepository::save)
                ).subscribe(null,null, () -> {
                    movieRepository.findAll().subscribe(System.out::println);
        });
    }
}
