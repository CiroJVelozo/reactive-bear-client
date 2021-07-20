package com.guru.reactivebearclient.client;

import com.guru.reactivebearclient.config.WebClientConfig;
import com.guru.reactivebearclient.model.BeerDto;
import com.guru.reactivebearclient.model.BeerPagedList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

class BeerClientImpTest {

   BeerClient beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImp(new WebClientConfig().webClient());
    }

    @Test
    void listBeer() {
     Mono<BeerPagedList> beerPagedListMono = beerClient.listBeer(null,null,null,
             null, null);

     BeerPagedList beerPagedList = beerPagedListMono.block();
     Assertions.assertNotNull(beerPagedList);
     System.out.println(beerPagedList.toList());
    }

   @Test
   void listBeerPagesizeTen() {
    Mono<BeerPagedList> beerPagedListMono = beerClient.listBeer(1,10,null,
            null, null);

    BeerPagedList beerPagedList = beerPagedListMono.block();
    Assertions.assertNotNull(beerPagedList);
    Assertions.assertEquals(10,beerPagedList.getContent().size());
   }

   @Test
   void listBeerNoRecords() {
    Mono<BeerPagedList> beerPagedListMono = beerClient.listBeer(10,20,null,
            null, null);

    BeerPagedList beerPagedList = beerPagedListMono.block();
    Assertions.assertNotNull(beerPagedList);
    Assertions.assertEquals(0,beerPagedList.getContent().size());
   }

    @Test
    void getBeerById() {
     Mono<BeerPagedList> beerPagedListMono = beerClient.listBeer(null,null,null,
             null, null);
     BeerPagedList beerPagedList = beerPagedListMono.block();
     UUID beerId = beerPagedList.getContent().get(0).getId();
     Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId,false);
     BeerDto beerDto = beerDtoMono.block();
     Assertions.assertEquals(beerId,beerDto.getId());
    }

    @Test
    void getBeerByIdOnHandTrue() {
     Mono<BeerPagedList> beerPagedListMono = beerClient.listBeer(null,null,null,
             null, null);
     BeerPagedList beerPagedList = beerPagedListMono.block();
     UUID beerId = beerPagedList.getContent().get(0).getId();
     Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId,true);
     BeerDto beerDto = beerDtoMono.block();
     Assertions.assertEquals(beerId,beerDto.getId());

    }

    @Test
    void getBeerByUPC() {
     Mono<BeerPagedList> beerPagedListMono = beerClient.listBeer(null,null,null,
             null, null);
     BeerPagedList beerPagedList = beerPagedListMono.block();
     String beerUpc = beerPagedList.getContent().get(0).getUpc();
     Mono<BeerDto> beerDtoMono = beerClient.getBeerByUPC(beerUpc);
     BeerDto beerDto = beerDtoMono.block();
     Assertions.assertEquals(beerUpc,beerDto.getUpc());
    }

    @Test
    void createBeer() {
      BeerDto beerDto = BeerDto.builder()
              .beerName("Dogfishhead 90 Min IPA")
              .beerStyle("IPA")
              .upc("2374890029387")
              .price(new BigDecimal("10.99"))
              .build();

      Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);
      ResponseEntity responseEntity = responseEntityMono.block();
      Assertions.assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
    }

    @Test
    void updateBeer() {
     Mono<BeerPagedList> beerPagedListMono = beerClient.listBeer(null,null,null,
             null, null);
     BeerPagedList beerPagedList = beerPagedListMono.block();
     BeerDto beerDto = beerPagedList.getContent().get(0);

     BeerDto updateBeerDto = BeerDto.builder()
             .beerName("Beer is not good")
             .beerStyle(beerDto.getBeerStyle())
             .upc(beerDto.getUpc())
             .price(beerDto.getPrice())
             .build();

     Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(beerDto.getId(),updateBeerDto);
     ResponseEntity responseEntity = responseEntityMono.block();
     Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    void beerExceptionHandler() {
     Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());
     ResponseEntity responseEntity = responseEntityMono.onErrorResume(throwable -> {
      if(throwable instanceof WebClientResponseException){
             WebClientResponseException webClientResponseException = (WebClientResponseException) throwable;
             return Mono.just(ResponseEntity.status(webClientResponseException.getStatusCode()).build());
      }else {
        throw  new RuntimeException(throwable);
      }
     }).block();

     Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void beerNotFound() {
     Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());
     Assertions.assertThrows(WebClientException.class,()->{
      ResponseEntity responseEntity = responseEntityMono.block();
      Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
     });

    }

    @Test
    void deleteBeer() {
     Mono<BeerPagedList> beerPagedListMono = beerClient.listBeer(null,null,null,
             null, null);
     BeerPagedList beerPagedList = beerPagedListMono.block();
     UUID beerId = beerPagedList.getContent().get(0).getId();
     Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(beerId);
     ResponseEntity responseEntity = responseEntityMono.block();
     Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void functionalTestGetBeerById() {
     beerClient.listBeer(null,null,null,
             null, null)
             .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
             .map(beerId -> beerClient.getBeerById(beerId,false))
             .flatMap(mono -> mono)
             .subscribe(beerDto -> {
              System.out.println(beerDto.getBeerName());
             });
    }
}