package com.eum602.reactiveprogramming;

import com.eum602.reactiveprogramming.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

class PersonRepositoryImplTest {

    PersonRepositoryImpl personRepository;

    @BeforeEach
    void setUp() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);
        Person person = personMono.block(); //blocks until getting the response

        System.out.println("Blocking: " + person.toString());
    }

    @Test
    void getByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.subscribe(person -> {
            System.out.println("Non blocking: " + person.toString());
        });
    }

    @Test
    void getByIdSubscribeNotFoud() {
        Mono<Person> personMono = personRepository.getById(90);
        personMono.subscribe(person -> {
            System.out.println("Non blocking: " + person.toString());
        });
    }

    @Test
    void getByIdMapFunction() {
        Mono<Person> personMono = personRepository.getById(1);

        personMono.map(person -> {
            System.out.println(person.toString());
            return person.getFirstName();
        }).subscribe(firstName ->{//handles the back pressure
            System.out.println("from map: " + firstName);
        });
    }

    @Test
    void fluxTestBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        Person person = personFlux.blockFirst(); //in a blocking manner
        //only catches the first person

        System.out.println(person.toString());
    }

    @Test
    void testFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();
        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFluxToListMono() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> personListMono = personFlux.collectList();//collects the flux in a list

        personListMono.subscribe(list ->{
            list.forEach(person -> {
                System.out.println(person.toString());
            });
        });
    }

    @Test
    void testFindPersonById() {
        Flux<Person> personFlux = personRepository.findAll();

        final  Integer id = 3;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();
        //next-> emits a Mono

        personMono.subscribe(person -> {
            System.out.println(person.toString());//only prints the object with id=3
        });
    }

    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        final  Integer id = 8;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();
        //next-> emits a Mono

        personMono.subscribe(person -> {
            System.out.println(person.toString());//returns nothing since "8" index does not exist
        });
    }

    @Test
    void testFindPersonByIdNotFoundWithException() {

        Flux<Person> personFlux = personRepository.findAll();

        final  Integer id = 8;
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).single();
        //single -> throws an exception when the object is not found or the "id" is out of bounds
        personMono
                .doOnError(throwable -> {
                    System.out.println("Somehting happened, here is expected to have some recovery operations");
                })
                //.onErrorReturn(Person.builder().build())
                .onErrorReturn(Person.builder().id(id).build())//returning an object with null values
                //on error simply returning null
                .subscribe(person -> {
                    System.out.println(person.toString());//only prints the object with id=3
                });

    }
}