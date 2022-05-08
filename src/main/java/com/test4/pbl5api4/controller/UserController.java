package com.test4.pbl5api4.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.test4.pbl5api4.model.*;
import com.test4.pbl5api4.repository.UserRepository;
import com.test4.pbl5api4.service.JWTUtil;
import com.test4.pbl5api4.service.ResponseObjectService;
import com.test4.pbl5api4.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/users")
    public ResponseEntity<ResponseObjectService> findAllUsers() {
        return new ResponseEntity<ResponseObjectService>(userService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/users/profile")
    public ResponseEntity<ResponseObjectService> findById(@RequestBody IdObjectModel inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findById(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/follow")
    public ResponseEntity<ResponseObjectService> followUser(@RequestBody DoubleIdObjectModel doubleId) {
        return new ResponseEntity<ResponseObjectService>(userService.followUser(doubleId), HttpStatus.OK);
    }

    @PostMapping("/users/unfollow")
    public ResponseEntity<ResponseObjectService> unfollowUser(@RequestBody DoubleIdObjectModel doubleId) {
        return new ResponseEntity<ResponseObjectService>(userService.unfollowUser(doubleId), HttpStatus.OK);
    }

    @PostMapping("/users/getfollowing")
    public ResponseEntity<ResponseObjectService> findFollowing(@RequestBody IdObjectModel inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findFollowing(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/getfollower")
    public ResponseEntity<ResponseObjectService> findFollower(@RequestBody IdObjectModel inputId) {
        return new ResponseEntity<ResponseObjectService>(userService.findFollower(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/save")
    public ResponseEntity<ResponseObjectService> saveUser(@RequestBody UserModel inputUser) {
        return new ResponseEntity<ResponseObjectService>(userService.saveUser(inputUser), HttpStatus.OK);
    }

    @PostMapping("/users/getava")
    public ResponseEntity<ResponseObjectService> getAva(@RequestBody IdObjectModel inputId){
        return new ResponseEntity<ResponseObjectService>(userService.getAva(inputId.getId()), HttpStatus.OK);
    }

    @PostMapping("/users/signin")
    public ResponseEntity<ResponseObjectService> userSignIn(@RequestBody UserSignInModel inputUser) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(inputUser.getEmail(), inputUser.getPassword()));
            String token = jwtUtil.generateToken(inputUser.getEmail());
            
            Optional<UserModel> optUser = userRepo.findByEmail(inputUser.getEmail());
            UserModel user = optUser.get();
            user.setPassword("");
            return new ResponseEntity<ResponseObjectService>(new ResponseObjectService("thanh cong", "authenticated", new AuthorizedModel(user, token)), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<ResponseObjectService>(new ResponseObjectService("that bai", "unauthenticated", null), HttpStatus.OK);
        }
    }

    @PostMapping("/users/changepass")
    public ResponseEntity<ResponseObjectService> userChangePass(@RequestBody UserModel inpPass){
        return new ResponseEntity<ResponseObjectService>(userService.changePass(inpPass), HttpStatus.OK);
    }

    @PutMapping("/users/update")
    public ResponseEntity<ResponseObjectService> update(@RequestBody UserModel inputUser) {
        return new ResponseEntity<ResponseObjectService>(userService.update(inputUser), HttpStatus.OK);
    }

    @GetMapping("/getdata")
    public ResponseEntity<String> testAfterLogin(Principal p) {
        return ResponseEntity.ok("Welcome. You are: " + p.getName());
    }
//    @GetMapping("/users/{name}")
//    public ResponseEntity<Optional<UserModel>> search(@PathVariable(value="name") String name){
//        return new ResponseEntity<ResponseObjectService>(userService.search(name), HttpStatus.OK);
//    }
}