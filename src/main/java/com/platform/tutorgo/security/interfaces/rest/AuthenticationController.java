package com.platform.tutorgo.security.interfaces.rest;

import com.platform.tutorgo.security.domain.services.UserCommandService;
import com.platform.tutorgo.security.interfaces.rest.resources.AuthenticatedUserResource;
import com.platform.tutorgo.security.interfaces.rest.resources.SignInResource;
import com.platform.tutorgo.security.interfaces.rest.resources.SignUpResource;
import com.platform.tutorgo.security.interfaces.rest.resources.UserResource;
import com.platform.tutorgo.security.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.platform.tutorgo.security.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.platform.tutorgo.security.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.platform.tutorgo.security.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication Management Endpoints")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticatedUserResource> signIn(@RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedUser = userCommandService.handle(signInCommand);
        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                authenticatedUser.get().getLeft(), authenticatedUser.get().getRight());
        return ResponseEntity.ok(authenticatedUserResource);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var user = userCommandService.handle(signUpCommand);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);
    }
}
