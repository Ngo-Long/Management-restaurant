package com.management.restaurant.config;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.management.restaurant.domain.*;
import com.management.restaurant.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.management.restaurant.domain.enumeration.GenderEnum;

/**
 * CommandLineRunner is an interface provided by Spring Boot
 * that can be used to execute code after the Spring application context
 * is fully initialized but before the application is completely started.
 * This means that any bean implementing this interface will have its run
 * method invoked with the command-line arguments passed to the application.
 */
@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;


    public DatabaseInitializer(
        RoleRepository roleRepository,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        PermissionRepository permissionRepository
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;

    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Tạo nhà hàng", "/api/v1/restaurants", "POST", "RESTAURANTS"));
            arr.add(new Permission("Cập nhật nhà hàng", "/api/v1/restaurants", "PUT", "RESTAURANTS"));
            arr.add(new Permission("Xóa nhà hàng", "/api/v1/restaurants/{id}", "DELETE", "RESTAURANTS"));
            arr.add(new Permission("Tìm nhà hàng theo mã", "/api/v1/restaurants/{id}", "GET", "RESTAURANTS"));
            arr.add(new Permission("Tìm nhà hàng theo bộ lọc", "/api/v1/restaurants", "GET", "RESTAURANTS"));

            arr.add(new Permission("Tạo người dùng", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Cập nhật người dùng", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Xóa người dùng", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Tìm người dùng theo mã", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Tìm người dùng theo bộ lọc", "/api/v1/users", "GET", "USERS"));

            arr.add(new Permission("Tạo chức vụ", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Cập nhật chức vụ", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Xóa chức vụ", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Tìm chức vụ theo mã", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Tìm chức vụ theo bộ lọc", "/api/v1/roles", "GET", "ROLES"));

            arr.add(new Permission("Tạo quyền hạn", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Cập nhật quyền hạn", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Xóa quyền hạn", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Tìm quyền hạn theo mã", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Tìm quyền hạn theo bộ lọc", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Tạo bàn ăn", "/api/v1/dining-tables", "POST", "DININGTABLES"));
            arr.add(new Permission("Cập nhật bàn ăn", "/api/v1/dining-tables", "PUT", "DININGTABLES"));
            arr.add(new Permission("Xóa bàn ăn", "/api/v1/dining-tables/{id}", "DELETE", "DININGTABLES"));
            arr.add(new Permission("Tìm bàn ăn theo mã", "/api/v1/dining-tables/{id}", "GET", "DININGTABLES"));
            arr.add(new Permission("Tìm bàn ăn theo bộ lọc", "/api/v1/dining-tables", "GET", "DININGTABLES"));

            arr.add(new Permission("Tạo hóa đơn", "/api/v1/invoices", "POST", "INVOICES"));
            arr.add(new Permission("Cập nhật hóa đơn", "/api/v1/invoices", "PUT", "INVOICES"));
            arr.add(new Permission("Xóa hóa đơn", "/api/v1/invoices/{id}", "DELETE", "INVOICES"));
            arr.add(new Permission("Tìm hóa đơn theo mã", "/api/v1/invoices/{id}", "GET", "INVOICES"));
            arr.add(new Permission("Tìm hóa đơn theo bộ lọc", "/api/v1/invoices", "GET", "INVOICES"));

            arr.add(new Permission("Tạo đơn hàng", "/api/v1/orders", "POST", "ORDERS"));
            arr.add(new Permission("Cập nhật đơn hàng", "/api/v1/orders", "PUT", "ORDERS"));
            arr.add(new Permission("Xóa đơn hàng", "/api/v1/orders/{id}", "DELETE", "ORDERS"));
            arr.add(new Permission("Tìm đơn hàng theo mã", "/api/v1/orders/{id}", "GET", "ORDERS"));
            arr.add(new Permission("Tìm đơn hàng theo bộ lọc", "/api/v1/orders", "GET", "ORDERS"));

            arr.add(new Permission("Tạo đơn hàng chi tiết", "/api/v1/order-details", "POST", "ORDERDETAILS"));
            arr.add(new Permission("Cập nhật đơn hàng chi tiết", "/api/v1/order-details", "PUT", "ORDERDETAILS"));
            arr.add(new Permission("Xóa đơn hàng chi tiết", "/api/v1/order-details/{id}", "DELETE", "ORDERDETAILS"));
            arr.add(new Permission("Tìm đơn hàng chi tiết theo mã", "/api/v1/order-details/{id}", "GET", "ORDERDETAILS"));
            arr.add(new Permission("Tìm đơn hàng chi tiết theo bộ lọc", "/api/v1/order-details", "GET", "ORDERDETAILS"));

            arr.add(new Permission("Tạo sản phẩm", "/api/v1/products", "POST", "PRODUCTS"));
            arr.add(new Permission("Cập nhật sản phẩm", "/api/v1/products", "PUT", "PRODUCTS"));
            arr.add(new Permission("Xóa sản phẩm", "/api/v1/products/{id}", "DELETE", "PRODUCTS"));
            arr.add(new Permission("Tìm sản phẩm theo mã", "/api/v1/products/{id}", "GET", "PRODUCTS"));
            arr.add(new Permission("Tìm sản phẩm theo bộ lọc", "/api/v1/products", "GET", "PRODUCTS"));

            arr.add(new Permission("Tải xuống tập tin", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Tải lên tập tin", "/api/v1/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            // Create Owner role
            List<Permission> allPermissions = this.permissionRepository.findAll();
            Role ownerRole = new Role();
            ownerRole.setName("OWNER");
            ownerRole.setDescription("Owner full permissions");
            ownerRole.setActive(true);
            ownerRole.setPermissions(allPermissions);
            this.roleRepository.save(ownerRole);

            // Create Admin role (exclude RESTAURANTS permissions)
            List<Permission> adminPermissions = allPermissions.stream()
                .filter(permission -> !permission.getModule().contains("RESTAURANTS"))
                .collect(Collectors.toList());

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Admin full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(adminPermissions);
            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            // create owner user
            User ownerUser = new User();
            ownerUser.setEmail("owner@gmail.com");
            ownerUser.setPassword(this.passwordEncoder.encode("owner"));
            ownerUser.setName("I'm owner");
            ownerUser.setAge(21);
            ownerUser.setGender(GenderEnum.MALE);
            ownerUser.setAddress("Hồ Chí Minh");

            Role ownerRole = this.roleRepository.findByName("OWNER");
            if (ownerRole != null)  ownerUser.setRole(ownerRole);

            this.userRepository.save(ownerUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else {
            System.out.println(">>> END INIT DATABASE");
        }
    }

}
