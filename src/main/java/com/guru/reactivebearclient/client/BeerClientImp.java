package com.guru.reactivebearclient.client;

import com.guru.reactivebearclient.config.WebClientProperties;
import com.guru.reactivebearclient.model.BeerDto;
import com.guru.reactivebearclient.model.BeerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImp implements BeerClient{

    private final WebClient webClient;

    @Override
    public Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand) {
        return webClient.get()
                        .uri(uriBuilder -> {
                           return uriBuilder.path(WebClientProperties.BEER_V1_PATH_GET_BY_ID)
                                      .queryParamIfPresent("showInventoryOnHand",Optional.ofNullable(showInventoryOnHand))
                                      .build(id);
                        }).retrieve()
                          .bodyToMono(BeerDto.class);

    }

    @Override
    public Mono<BeerPagedList> listBeer(Integer pageNumber, Integer pageSize, String beerName, String beerStyle, Boolean showInventoryOnHand) {
        return webClient.get()
                .uri(uriBuilder -> {
                    return uriBuilder.path(WebClientProperties.BEER_V1_PATH)
                            .queryParam("pageNumber", Optional.ofNullable(pageNumber))
                            .queryParam("pageSize", Optional.ofNullable(pageSize))
                            .queryParam("beerName", Optional.ofNullable(beerName))
                            .queryParam("beerStyle", Optional.ofNullable(beerStyle))
                            .queryParam("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
                            .build();
                })
                .retrieve()
                .bodyToMono(BeerPagedList.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto) {
        return webClient.post().uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER_V1_PATH).build())
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> updateBeer(UUID beerId, BeerDto beerDto) {
        return webClient.put().uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER_V1_PATH_GET_BY_ID).build(beerId))
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteBeerById(UUID beerId) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER_V1_PATH_GET_BY_ID).build(beerId))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<BeerDto> getBeerByUPC(String upc) {
            return webClient.get().uri(uriBuilder -> {
                return uriBuilder.path(WebClientProperties.BEER_V1_UPC_PATH)
                                 .build(upc);
            }).retrieve()
              .bodyToMono(BeerDto.class);
    }
}
