package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.stream.Collectors;

@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ReservationClientApplication.class, args);
    }
}


@RestController
@RequestMapping("/reservations")
class ReservationApiGatewayRestController {

    private final RestTemplate restTemplate;

    @Autowired
    ReservationApiGatewayRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/names")
    public Collection<String> names() {

        ParameterizedTypeReference<Resources<Reservation>> ptr =
                new ParameterizedTypeReference<Resources<Reservation>>() {
                };

        ResponseEntity<Resources<Reservation>> responseEntity = this.restTemplate.exchange("http://reservation-service/reservations",
                HttpMethod.GET, null, ptr);

        return responseEntity.getBody().getContent()
                .stream()
                .map(Reservation::getReservationName)
                .collect(Collectors.toList());
    }
}

class Reservation {
    private String reservationName;

    public String getReservationName() {
        return reservationName;
    }
}