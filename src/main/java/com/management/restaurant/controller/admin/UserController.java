package com.management.restaurant.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.management.restaurant.domain.User;
import com.management.restaurant.service.UserService;

import jakarta.validation.Valid;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link com.management.restaurant.domain.User} entity,
 * and
 * needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship
 * between User and Refresh Token,
 * and send everything to the client side: there would be no View Model and DTO,
 * a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities,
 * because people will
 * quite often do relationships with the user, and we don't want them to get the
 * authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we
 * should not impact our users'
 * application because of this use-case.</li>
 * <li>Not having an outer join causes n+1 requests to the database. This is not
 * a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we
 * do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better
 * than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li>As this manages users, for security reasons, we'd rather have a DTO
 * layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this
 * case.
 */
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User dataUser) {
        return this.userService.create(dataUser);
    }

    /**
     * {@code DELETE /admin/users/:id} : delete the "id" User.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
