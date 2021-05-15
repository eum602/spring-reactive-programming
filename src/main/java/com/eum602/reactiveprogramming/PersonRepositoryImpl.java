package com.eum602.reactiveprogramming;

import com.eum602.reactiveprogramming.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {
    Person jhon = new Person(1, "Jhon", "Doe");
    Person alice = new Person(2, "Alice", "Martins");
    Person eve = new Person(3, "Eve","Dropper");
    Person bob = new Person(4, "Bob","Smith");

    @Override
    public Mono<Person> getById(Integer id) {
        return Mono.just(jhon);
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(jhon,alice,alice,eve,bob);
    }
}
