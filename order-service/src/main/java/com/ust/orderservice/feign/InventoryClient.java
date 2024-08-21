package com.ust.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/products")
    ProductAvailability isProductAvailable(@RequestParam String skuCode, @RequestParam int quantity);

    @PutMapping("/products/{skuCode}/{quantity}/update")
    Product updateProductQuantity(@PathVariable String skuCode, @PathVariable int quantity);

    @GetMapping("/products/{skuCode}")
    Product getProductBySkuCode(@PathVariable String skuCode);
}
