package com.management.restaurant.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.management.restaurant.util.error.InfoInvalidException;

import com.management.restaurant.domain.User;
import com.management.restaurant.domain.response.users.ResUserDTO;
import com.management.restaurant.domain.response.ResultPaginationDTO;
import com.management.restaurant.domain.response.users.ResCreateUserDTO;
import com.management.restaurant.domain.response.users.ResUpdateUserDTO;

import com.management.restaurant.service.UserService;
import com.management.restaurant.util.annotation.ApiMessage;

/**
 * REST controller for managing users.
 * This class accesses the {@link com.management.restaurant.domain.User} entity
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

	private final Logger log = LoggerFactory.getLogger(UserController.class);
	
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@code POST  /users} : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param user the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new user, or with status {@code 400 (Bad Request)} if the
     *         login or email is already in use.
     * @throws InfoInvalidException       {@code 400 (Bad Request)} if the login or
     *                                    email is already in use.
     */
    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws InfoInvalidException {
    	log.debug("REST request to save User : {}", user);
    	
		boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
		if (isEmailExist) {
			throw new InfoInvalidException("Email đã tồn tại, vui lòng sử dụng email khác!");
		}
		
		String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

		User newUser = this.userService.createUser(user);
		ResCreateUserDTO res = this.userService.convertToResCreateUserDTO(newUser);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    
    /**
     * {@code PUT /users} : Updates an existing User.
     *
     * @param user the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated user.
     * @throws InfoInvalidException {@code 400 (Bad Request)} if the email is not
     *                                   already in use.
     */    
    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@Valid @RequestBody User user) throws InfoInvalidException {
    	log.debug("REST request to update User : {}", user);
    	
        if (this.userService.fetchUserById(user.getId()) == null) {
        	throw new InfoInvalidException("Người dùng không tồn tại!");
        }
    	
    	User dataUser = this.userService.updateUser(user);
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(dataUser));
    }

    /**
     * {@code DELETE /users/:id} : delete the "id" User.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id) throws InfoInvalidException {
    	log.debug("REST request to delete User: {}", id);
    	
        if (this.userService.fetchUserById(id) == null) {
            throw new InfoInvalidException("Người dùng không tồn tại!");
        }
        
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }
    
    /**
     * {@code GET /users/:id} : get the "id" user.
     *
     * @param id user by id
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the "id" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws InfoInvalidException {
    	log.debug("REST request to get User : {}", id);
    	
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new InfoInvalidException("Người dùng không tồn tại!");
        }

        return ResponseEntity.ok(this.userService.convertToResUserDTO(fetchUser));
    }

    /**
     * {@code GET /users} : get all users with all the details - calling this
     * are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @param spec     the filtering criteria applied to the query
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         all users.
     */
    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchUsers(Pageable pageable, @Filter Specification<User> spec) {
    	log.debug("REST request to get all User");
        return ResponseEntity.ok(this.userService.fetchUsersDTO(spec, pageable));
    }
}