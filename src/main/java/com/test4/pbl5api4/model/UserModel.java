package com.test4.pbl5api4.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user")
public class UserModel {
    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String avata;

    private String role;

    List<String> following = new ArrayList<>();

    List<String> follower = new ArrayList<>();
}
