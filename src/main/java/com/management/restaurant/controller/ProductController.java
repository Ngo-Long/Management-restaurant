package com.management.restaurant.controller;

import java.util.Objects;
import jakarta.validation.Valid;

import com.turkraft.springfilter.boot.Filter;

import com.management.restaurant.domain.Product;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.service.ProductService;
import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * REST controller for managing products.
 * This class accesses the {@link Product} entity
 */
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(RestaurantController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * {@code POST  /products} : Create a new product.
     *
     * @param product the product to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new products,
     *         or with status {@code 400 (Bad Request)} if the products name already exists.
     * @throws InfoInvalidException if the product name already exists or if the input information is invalid.
     */
    @PostMapping("/products")
    @ApiMessage("Create a product")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product)
            throws InfoInvalidException {
        log.debug("REST request to save product : {}", product);

        if (this.productService.isNameExist(product.getName())) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        Product newProduct = this.productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    /**
     * {@code PUT  /products/:id} : Update an existing product.
     *
     * @param product the product details to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated product in the body,
     *         or with status {@code 400 (Bad Request)} if the product is invalid or if the name already exists.
     * @throws InfoInvalidException if the product does not exist or if the product name is already taken.
     */
    @PutMapping("/products")
    @ApiMessage("Update a product")
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product)
            throws InfoInvalidException {
        log.debug("REST request to update product : {}", product);

        Product currentProduct = this.productService.fetchProductById(product.getId());
        if (currentProduct == null) {
            throw new InfoInvalidException("Sản phẩm không tồn tại!");
        }

        boolean isNameExist = this.productService.isNameExist(product.getName());
        if (isNameExist && !Objects.equals(currentProduct.getName(), product.getName())) {
            throw new InfoInvalidException("Tên đã tồn tại, vui lòng sử dụng tên khác!");
        }

        Product dataProduct = this.productService.updateProduct(product);
        return ResponseEntity.ok().body(dataProduct);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" product.
     *
     * @param id the id of the products to delete.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @DeleteMapping("/products/{id}")
    @ApiMessage("Delete a product")
    public ResponseEntity<Void> deleteProductById(@PathVariable("id") Long id) throws InfoInvalidException {
        log.debug("REST request to delete product: {}", id);

        Product currentProduct = this.productService.fetchProductById(id);
        if (currentProduct == null) {
            throw new InfoInvalidException("Sản phẩm không tồn tại!");
        }

        this.productService.deleteProductById(id);
        return ResponseEntity.ok(null);
    }

    /**
     * {@code GET  /products/:id} : get the "id" product.
     *
     * @param id the id of the products to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the products,
     *         or with status {@code 400 (Bad Request)}.
     */
    @GetMapping("/products/{id}")
    @ApiMessage("Get product by id")
    public ResponseEntity<Product> fetchProductById(@PathVariable("id") long id) throws InfoInvalidException {
        log.debug("REST request to get product : {}", id);

        Product dataProduct = this.productService.fetchProductById(id);
        if (dataProduct == null) {
            throw new InfoInvalidException("Nhà hàng không tồn tại!");
        }

        return ResponseEntity.ok().body(dataProduct);
    }

    /**
     * {@code GET  /products} : Fetch filter products.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the product list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in the body.
     */
    @GetMapping("/products")
    @ApiMessage("Fetch filter products")
    public ResponseEntity<ResultPaginationDTO> fetchProducts(Pageable pageable, @Filter Specification<Product> spec) {
        log.debug("REST request to get product filter");
        return ResponseEntity.ok(this.productService.fetchProducts(spec, pageable));
    }
}
