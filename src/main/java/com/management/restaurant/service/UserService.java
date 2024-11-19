package com.management.restaurant.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.management.restaurant.domain.Role;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.management.restaurant.domain.User;
import com.management.restaurant.domain.Restaurant;
import com.management.restaurant.domain.response.user.ResUserDTO;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.domain.response.user.ResCreateUserDTO;
import com.management.restaurant.domain.response.user.ResUpdateUserDTO;

import com.management.restaurant.repository.UserRepository;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestaurantService restaurantService;
    private final RoleService roleService;

    public UserService(
            UserRepository userRepository,
            RestaurantService restaurantService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.restaurantService = restaurantService;
        this.roleService = roleService;
    }

    public User createUser(User user) {
        if (user.getRestaurant() != null) {
        	Restaurant restaurant = this.restaurantService.fetchRestaurantById(user.getRestaurant().getId());
            user.setRestaurant(restaurant != null ? restaurant : null);
        }

        if (user.getRole() != null) {
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
        
        return this.userRepository.save(user);
    }
    
    public User updateUser(User user) {
    	User currentUser = this.fetchUserById(user.getId());
        if (currentUser == null) {
            return null;
        }

        if (user.getRestaurant() != null) {
            Restaurant restaurant = this.restaurantService.fetchRestaurantById(user.getRestaurant().getId());
            currentUser.setRestaurant(restaurant != null ? restaurant : null);
        }

        if (user.getRole() != null) {
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            currentUser.setRole(role != null ? role : null);
        }
        
        currentUser.setName(user.getName());
        currentUser.setAge(user.getAge());
        currentUser.setGender(user.getGender());
        currentUser.setAddress(user.getAddress());

        return this.userRepository.save(currentUser);
    }

    public void updateUserToken(String email, String token) {
        User currentUser = this.fetchUserByUsername(email);
        if (currentUser == null) {
            return;
        }

        currentUser.setRefreshToken(token);
        this.userRepository.save(currentUser);
    }

    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }
    
    public Boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }
    
    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        return null;
    }

    public User fetchUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    /**
     * Convert a {@link User} entity to a {@link ResUserDTO}.
     *
     * @param user the user entity to convert.
     * @return a {@link ResUserDTO} containing the user details.
     */
    public ResUserDTO convertToResUserDTO(User user) {
        // response user DTO
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedDate(user.getCreatedDate());
        res.setLastModifiedDate(user.getLastModifiedDate());

        // response restaurant - user DTO
        ResUserDTO.RestaurantUser restaurantUser = new ResUserDTO.RestaurantUser();
        if (user.getRestaurant() != null) {
            restaurantUser.setId(user.getRestaurant().getId());
            restaurantUser.setName(user.getRestaurant().getName());
            res.setRestaurant(restaurantUser);
        }

        // response role - user DTO
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        return res;
    }

    /**
     * Convert a {@link User} entity to a {@link ResCreateUserDTO} for user creation responses.
     *
     * @param user the user entity to convert.
     * @return a {@link ResCreateUserDTO} containing the user details for creation.
     */
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.RestaurantUser restaurant = new ResCreateUserDTO.RestaurantUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedDate(user.getCreatedDate());

        if (user.getRestaurant() != null) {
            restaurant.setId(user.getRestaurant().getId());
            restaurant.setName(user.getRestaurant().getName());
            res.setRestaurant(restaurant);
        }
       
        return res;
    }

    /**
     * Convert a {@link User} entity to a {@link ResUpdateUserDTO} for user update responses.
     *
     * @param user the user entity to convert.
     * @return a {@link ResUpdateUserDTO} containing the user details for updates.
     */
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
    	ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.RestaurantUser restaurant = new ResUpdateUserDTO.RestaurantUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setLastModifiedDate(user.getLastModifiedDate());

        if (user.getRestaurant() != null) {
            restaurant.setId(user.getRestaurant().getId());
            restaurant.setName(user.getRestaurant().getName());
            res.setRestaurant(restaurant);
        }
       
        return res;
    }

    /**
     * Fetch a paginated list of users based on the given search criteria and pagination information.
     *
     * @param spec the filtering criteria to apply to the user list.
     * @param pageable the pagination information.
     * @return a {@link ResultPaginationDTO} containing the list of users and pagination metadata.
     */
    public ResultPaginationDTO fetchUsersDTO(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(this::convertToResUserDTO)
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

}
