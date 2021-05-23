package com.eum602.webclientapp.client;

import com.eum602.webclientapp.config.WebClientConfig;
import com.eum602.webclientapp.model.BeerDto;
import com.eum602.webclientapp.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp(){
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void listBeers() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void listBeersPageSize10() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1,10,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();
        /*pagedList.forEach(page -> {
            System.out.println("::::::::::::::::::::" + page);
        });*/
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(10); //isEqualTo -> means our content should be limited to.
    }

    @Test
    void listBeersNoRecords() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(10,20,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();
        pagedList.forEach(page -> {
            System.out.println("::::::::::::::::::::" + page);
        });
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(0);
    }

    @Test
    void getBeerById() {

        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();

        UUID beerId = pagedList.getContent().get(0).getId();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId,false);
        beerDtoMono.map(el -> {
            assertThat(el.getQuantityOnHand()).isNull();
            return el.getId();
        }).subscribe(id ->{
            assertThat(id).isEqualTo(beerId);
        });
    }

    @Test
    void functionalTestGetBeerById() throws InterruptedException {
        AtomicReference<String> beerName = new AtomicReference<>();

        CountDownLatch countDownLatch = new CountDownLatch(1);//for testing concurrent applications and multithreaded environments

        beerClient.listBeers(null,null,null,null,null)
                .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
                .map(beerId -> beerClient.getBeerById(beerId,false))
                .flatMap(mono -> mono)
                .subscribe(beerDto -> {
                    System.out.println(beerDto.getBeerName());
                    beerName.set(beerDto.getBeerName());
                    assertThat(beerName.get()).isEqualTo("Mango Bobs");
                    countDownLatch.countDown();
                });
        //Thread.sleep(3000); //adding this in order to avoid the test finishing before the response comes comes.
        countDownLatch.await(); //stops and waits until the countDownLatch has been incremented by the value of "1" -> because in that way was the countDownLatch declared -> new CountDownLatch(1)
        assertThat(beerName.get()).isEqualTo("Mango Bobs");
    }

    @Test
    void getBeerByIdShowInventoryTrue() {

        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();

        UUID beerId = pagedList.getContent().get(0).getId();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId,true);
        beerDtoMono.map(el -> el.getQuantityOnHand()).subscribe(qty ->{
            assertThat(qty).isNotNull();
        });
    }

    @Test
    void getBeerByUPC() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();
        String upc = pagedList.getContent().get(0).getUpc();
        Mono<BeerDto> beerDtoMono = beerClient.getBeerByUPC(upc);
        beerDtoMono.map(el -> el.getUpc()).subscribe(UPC -> {
            assertThat(UPC).isEqualTo(upc);
        });
    }

    @Test
    void createBeer() {
        BeerDto beerDto = BeerDto
                .builder()
                .beerName("Pilsen")
                .beerStyle("ALE")
                .upc("12345678")
                .price(new BigDecimal(10.05))
                .build();
        Mono<ResponseEntity<Void>> responseEntityMono =  beerClient.createBeer(beerDto);
        ResponseEntity responseEntity = responseEntityMono.block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateBeer() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();

        BeerDto beerDto = pagedList.getContent().get(0);

        BeerDto updatedBeer =  BeerDto.builder()
                .beerName("A name updated")
                .beerStyle(beerDto.getBeerStyle())
                .price(beerDto.getPrice())
                .upc(beerDto.getUpc())
                .build();
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(beerDto.getId(), updatedBeer);
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testDeleteBeerHandleException(){
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());

        ResponseEntity<Void> responseEntity =  responseEntityMono
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException){
                        WebClientResponseException exception = (WebClientResponseException) throwable;
                        return Mono.just(ResponseEntity.status(exception.getStatusCode()).build());
                    }else {
                        throw new RuntimeException(throwable);
                    }
                })
                .block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deleteBeerByIdNotFound() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());
        assertThrows(WebClientResponseException.class, () -> {
            ResponseEntity<Void> responseEntity = responseEntityMono.block();
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        });
    }

    @Test
    void deleteBeerById() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();

        BeerDto beerDto = pagedList.getContent().get(0);

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(beerDto.getId());
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }
}