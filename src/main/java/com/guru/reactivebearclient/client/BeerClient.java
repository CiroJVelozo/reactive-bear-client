package com.guru.reactivebearclient.client;

import com.guru.reactivebearclient.model.BeerDto;
import com.guru.reactivebearclient.model.BeerPagedList;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Null;
import java.util.UUID;

public interface BeerClient {

    Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand);

    Mono<BeerPagedList> listBeer(Integer pageNumber, Integer pageSize, String beerName, String beerStyle, Boolean showInventoryOnHand);

    Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto);

    Mono<ResponseEntity<Void>> updateBeer(@Null UUID id, BeerDto beerDto);

    Mono<ResponseEntity<Void>> deleteBeerById(UUID beerId);

    Mono<BeerDto> getBeerByUPC(String upc);
}
