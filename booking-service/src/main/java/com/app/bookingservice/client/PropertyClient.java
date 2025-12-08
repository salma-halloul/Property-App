package com.app.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "property-service", url = "http://localhost:8081/api/properties")
public interface PropertyClient {

    @GetMapping("/exists/{id}")
    boolean checkPropertyExists(@PathVariable("id") Long id);
}

