package com.ust.inventoryservice.service;

import com.ust.inventoryservice.domain.Product;
import com.ust.inventoryservice.payload.OrderRequest;
import com.ust.inventoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public boolean isProductAvailableWithReqQuantity(String skuCode, int quantity) {
        return productRepository.existsBySkuCodeAndQuantityGreaterThanEqual(skuCode, quantity);
    }

    public int getProductQuantity(String skuCode) {
        return productRepository.findQuantityBySkuCode(skuCode);
    }

    @Transactional
    public void updateProductQuantity(String skuCode, int quantity) {
        int currentQuantity = productRepository.findQuantityBySkuCode(skuCode);
        productRepository.findBySkuCode(skuCode).ifPresent(product -> {
            product.setQuantity(currentQuantity - quantity);
            productRepository.save(product);
        });
    }

    @Transactional
    public void addProductQuantity(String skuCode, int quantity) {
        int currentQuantity = productRepository.findQuantityBySkuCode(skuCode);
        productRepository.findBySkuCode(skuCode).ifPresent(product -> {
            product.setQuantity(currentQuantity + quantity);
            productRepository.save(product);
        });
    }

    public Optional<Product> getProductBySkuCode(String skuCode) {
        return productRepository.findBySkuCode(skuCode);
    }

    public boolean isProductExists(List<String> skuCodes) {
        return productRepository.existsAllBySkuCodeIn(skuCodes);
    }


    @Transactional(rollbackOn = RuntimeException.class, value = REQUIRES_NEW)
    public void processOrder(OrderRequest message) {
        AtomicBoolean allProductsExists = new AtomicBoolean(true);
        message.orderItems().forEach(orderItem -> {
            if (!isProductAvailableWithReqQuantity(orderItem.skuCode(), orderItem.quantity())) {
                allProductsExists.set(false);
            }
        });
        log.info("All products exists: {}", allProductsExists.get());
        if(allProductsExists.get()) {
            message.orderItems().forEach(orderItem -> {
                updateProductQuantity(orderItem.skuCode(), orderItem.quantity());
            });
        }
    }
}
