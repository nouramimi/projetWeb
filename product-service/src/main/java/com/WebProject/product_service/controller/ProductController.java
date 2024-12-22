package com.WebProject.product_service.controller;

import com.WebProject.product_service.dto.ProductRequest;
import com.WebProject.product_service.dto.ProductResponse;
import com.WebProject.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest)
    {
        productService.createProduct(productRequest);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){
        return productService.getAllProducts();
    }
    @GetMapping("/{productCode}")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkProductExists(@PathVariable String productCode) {
        return productService.productExists(productCode);
    }


}
