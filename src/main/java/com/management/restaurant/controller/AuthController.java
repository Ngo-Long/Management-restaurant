package com.management.restaurant.controller;

import java.util.List;
import java.util.ArrayList;

import com.management.restaurant.domain.enumeration.TableEnum;
import jakarta.validation.Valid;

import com.management.restaurant.util.SecurityUtil;
import com.management.restaurant.util.annotation.ApiMessage;
import com.management.restaurant.util.error.IdInvalidException;

import com.management.restaurant.service.UserService;
import com.management.restaurant.domain.User;
import com.management.restaurant.domain.request.ReqLoginDTO;
import com.management.restaurant.domain.response.ResLoginDTO;
import com.management.restaurant.domain.response.user.ResCreateUserDTO;

import com.management.restaurant.repository.ProductRepository;
import com.management.restaurant.repository.RestaurantRepository;
import com.management.restaurant.repository.DiningTableRepository;

import com.management.restaurant.domain.Product;
import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.DiningTable;
import com.management.restaurant.domain.enumeration.ProductCategoryEnum;
import com.management.restaurant.domain.response.user.ResRegisterUserDTO;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @Value("${restaurant.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final UserService userService;
    private final RestaurantRepository restaurantRepository;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ProductRepository productRepository;
    private final DiningTableRepository diningTableRepository;

    public AuthController(
            UserService userService,
            RestaurantRepository restaurantRepository,
            SecurityUtil securityUtil,
            PasswordEncoder passwordEncoder,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            ProductRepository productRepository,
            DiningTableRepository diningTableRepository
    ) {
        this.userService = userService;
        this.restaurantRepository = restaurantRepository;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.productRepository = productRepository;
        this.diningTableRepository = diningTableRepository;
    }

    public ResponseEntity<ResLoginDTO> createResponseLogin(String email, User currentUser) {
        // issue new token/set refresh token as cookies
        ResLoginDTO resLoginDto = new ResLoginDTO();

        // create new userLogin
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
            currentUser.getId(),
            currentUser.getEmail(),
            currentUser.getName(),
            currentUser.getRole(),
            currentUser.getRestaurant()
        );
        resLoginDto.setUser(userLogin);

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, resLoginDto);
        resLoginDto.setAccessToken(access_token);

        // update refresh token for user
        String new_refresh_token = this.securityUtil.createRefreshToken(email, resLoginDto);
        this.userService.updateUserToken(email, new_refresh_token);

        // create cookies for refresh token
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDto);
    }

    @PostMapping("/auth/login")
    @ApiMessage("User login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto)
            throws IdInvalidException {
        // nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // set thông tin người dùng đăng nhập vào context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // get data user login
        User currentUserDB = this.userService.fetchUserByUsername(loginDto.getUsername());
        if (currentUserDB == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        return createResponseLogin(authentication.getName(), currentUserDB);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Fetch user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
        @CookieValue(name = "refresh_token", defaultValue = "") String refresh_token)
            throws IdInvalidException {
        if (refresh_token.equals("")) {
            throw new IdInvalidException("Bạn không có refresh token ở cookie!");
        }

        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUserDB = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUserDB == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ!");
        }

        return createResponseLogin(email, currentUserDB);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Fetch a account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() throws IdInvalidException {
        // get info user
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userService.fetchUserByUsername(email);
        if (currentUser == null) {
            throw new IdInvalidException("Không tìm thấy người dùng!");
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        userLogin.setId(currentUser.getId());
        userLogin.setEmail(currentUser.getEmail());
        userLogin.setName(currentUser.getName());
        userLogin.setRole(currentUser.getRole());
        userLogin.setRestaurant(currentUser.getRestaurant());

        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        userGetAccount.setUser(userLogin);

        return ResponseEntity.ok().body(userGetAccount);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ!");
        }

        // update refrech token == null
        this.userService.updateUserToken(email, null);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody ResRegisterUserDTO userDTO)
        throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(userDTO.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + userDTO.getEmail() + " đã tồn tại, " +
                "vui lòng sử dụng email khác!");
        }

        boolean isRestaurantExist = this.restaurantRepository.existsByName(userDTO.getRestaurant().getName());
        if (isRestaurantExist) {
            throw new IdInvalidException("Tên nhà hàng " + userDTO.getEmail() + " đã tồn tại, " +
                "vui lòng sử dụng tên khác!");
        }

        // hash password
        String hashPassword = this.passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(hashPassword);

        // create user and restaurant
        User user = this.userService.registerUser(userDTO);
        ResCreateUserDTO res = this.userService.convertToResCreateUserDTO(user);

        // create sample data restaurant
        Restaurant restaurant = user.getRestaurant();
        createSampleDataForRestaurant(restaurant);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    public void createSampleDataForRestaurant(Restaurant restaurant) {
        // create dining table list
        List<DiningTable> tables = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            DiningTable table = new DiningTable();
            table.setName("Bàn " + i);
            table.setLocation("Tầng 1");
            table.setSeats(4);
            table.setStatus(TableEnum.AVAILABLE);
            table.setRestaurant(restaurant);
            tables.add(table);
        }
        diningTableRepository.saveAll(tables);

        // create product list
        List<Product> products = new ArrayList<>();
        products.add(new Product("Coca", 15000.0, 6000.0, ProductCategoryEnum.DRINK, "Lon", 100, restaurant));
        products.add(new Product("Pepsi", 15000.0, 6000.0, ProductCategoryEnum.DRINK, "Lon", 100, restaurant));
        products.add(new Product("Mì cay hải sản", 55000.0, 30000.0, ProductCategoryEnum.FOOD, "Tô", 50, restaurant));
        products.add(new Product("Mì cay tôm", 50000.0, 25000.0, ProductCategoryEnum.FOOD, "Tô", 50, restaurant));
        products.add(new Product("Mì cay sò", 45000.0, 10000.0, ProductCategoryEnum.FOOD, "Tô", 50, restaurant));

        productRepository.saveAll(products);
    }
}
