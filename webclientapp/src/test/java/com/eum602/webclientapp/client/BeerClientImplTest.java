package com.eum602.webclientapp.client;

import com.eum602.webclientapp.config.WebClientConfig;
import com.eum602.webclientapp.model.BeerDto;
import com.eum602.webclientapp.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    }

    @Test
    void updateBeer() {
    }

    @Test
    void deleteBeerById() {
    }
}