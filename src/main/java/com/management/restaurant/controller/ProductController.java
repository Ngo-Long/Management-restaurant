package com.management.restaurant.controller;

import java.util.Objects;

import com.management.restaurant.domain.DiningTable;
import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.User;
import com.management.restaurant.service.UserService;
import com.management.restaurant.util.SecurityUtil;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.turkraft.springfilter.boot.Filter;
import com.management.restaurant.domain.Product;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.service.ProductService;
import com.management.restaurant.service.RestaurantService;
import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.InfoInvalidException;

/**
 * REST controller for managing products.
 * This class accesses the {@link Product} entity
 */
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    public ProductController(
        ProductService productService,
        RestaurantService restaurantService,
        UserService userService)
    {
        this.productService = productService;
        this.userService = userService;
        this.restaurantService = restaurantService;
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

        if (this.restaurantService.fetchRestaurantById(product.getRestaurant().getId()) == null) {
            throw new InfoInvalidException("Nhà hàng không tồn tại!");
        }

        if (this.productService.isExistByNameAndRestaurant(product.getName(), product.getRestaurant())) {
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

        if (this.restaurantService.fetchRestaurantById(product.getRestaurant().getId()) == null) {
            throw new InfoInvalidException("Nhà hàng không tồn tại!");
        }

        Product currentProduct = this.productService.fetchProductById(product.getId());
        if (currentProduct == null) {
            throw new InfoInvalidException("Sản phẩm không tồn tại!");
        }

        boolean isNameExist = this.productService.isExistByNameAndRestaurant(
            product.getName(), product.getRestaurant());
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

    /**
     * {@code GET  /products/by-restaurant} : Fetch filter products by restaurant.
     *
     * @param pageable the pagination information.
     * @param spec the filtering criteria to apply to the product list.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in the body.
     */
    @GetMapping("/products/by-restaurant")
    @ApiMessage("Fetch filter products by restaurant")
    public ResponseEntity<ResultPaginationDTO> fetchProductsByRestaurant(
        Pageable pageable, @Filter Specification<Product> spec) throws InfoInvalidException {
        log.debug("REST request to get product filter by restaurant");

        // get user current
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userService.fetchUserByUsername(email);
        if (currentUser == null) {
            throw new InfoInvalidException("Không tìm thấy người dùng!");
        }

        // get restaurant current
        Restaurant restaurant = currentUser.getRestaurant();
        if (restaurant == null) {
            throw new InfoInvalidException("Người dùng không thuộc nhà hàng nào!");
        }

        // Create a specification to filter by restaurant id
        Specification<Product> restaurantSpec = (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("restaurant").get("id"), restaurant.getId());

        // Combine specifications if needed
        Specification<Product> finalSpec = spec.and(restaurantSpec);

        return ResponseEntity.ok(this.productService.fetchProducts(finalSpec, pageable));
    }
}
