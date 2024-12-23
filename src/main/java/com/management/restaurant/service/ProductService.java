package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;

import com.management.restaurant.domain.Restaurant;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.Product;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.repository.ProductRepository;

/**
 * Service class for managing products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        return this.productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        Product currentProduct = this.fetchProductById(product.getId());
        if (currentProduct == null) {
            return null;
        }

        currentProduct.setName(product.getName());
        currentProduct.setSellingPrice(product.getSellingPrice());
        currentProduct.setCostPrice(product.getCostPrice());
        currentProduct.setCategory(product.getCategory());
        currentProduct.setUnit(product.getUnit());
        currentProduct.setQuantity(product.getQuantity());
        currentProduct.setSold(product.getSold());
        currentProduct.setImage(product.getImage());
        currentProduct.setShortDesc(product.getShortDesc());
        currentProduct.setDetailDesc(product.getDetailDesc());
        currentProduct.setActive(product.isActive());
        currentProduct.setRestaurant(product.getRestaurant());

        return this.productRepository.save(currentProduct);
    }

    public void deleteProductById(long id) {
        this.productRepository.deleteById(id);
    }

    public Boolean isExistByNameAndRestaurant(String name, Restaurant restaurant) {
        return this.productRepository.existsByNameAndRestaurant(name, restaurant);
    }

    public List<Product> fetchProductByNameAndRestaurant(String name, Restaurant restaurant) {
        return this.productRepository.findByNameAndRestaurant(name, restaurant);
    }

    public Product fetchProductById(Long id) {
        Optional<Product> productOptional = this.productRepository.findById(id);
        return productOptional.orElse(null);
    }

    public ResultPaginationDTO fetchProducts(Specification<Product> spec, Pageable pageable) {
        Page<Product> pageProduct = this.productRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageProduct.getTotalPages());
        meta.setTotal(pageProduct.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageProduct.getContent());

        return rs;
    }

}
