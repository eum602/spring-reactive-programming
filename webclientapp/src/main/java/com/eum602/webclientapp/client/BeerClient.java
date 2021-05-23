package com.eum602.webclientapp.client;

import com.eum602.webclientapp.model.BeerDto;
import com.eum602.webclientapp.model.BeerPagedList;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BeerClient {
    Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand);

    Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName,
                                  String beerStyle, Boolean showInventoryOnHand);

    Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto);

    Mono<ResponseEntity<Void>> updateBeer(UUID beerId, BeerDto beerDto);

    Mono<ResponseEntity<Void>> deleteBeerById(UUID id);

    Mono<BeerDto> getBeerByUPC(String upc);
}
