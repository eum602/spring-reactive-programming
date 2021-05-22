package com.eum602.netflux.repositories;

import com.eum602.netflux.domain.Movie;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovieRepository extends ReactiveMongoRepository<Movie, String> { //String because the 'id' used for Movie
    //object are string according to Movie class
}
