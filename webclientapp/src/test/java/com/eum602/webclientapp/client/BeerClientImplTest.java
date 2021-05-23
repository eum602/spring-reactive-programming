package com.eum602.webclientapp.client;

import com.eum602.webclientapp.config.WebClientConfig;
import com.eum602.webclientapp.model.BeerDto;
import com.eum602.webclientapp.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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
    void deleteBeerById() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,null,null,null);
        BeerPagedList pagedList = beerPagedListMono.block();

        BeerDto beerDto = pagedList.getContent().get(0);

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(beerDto.getId());
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }
}