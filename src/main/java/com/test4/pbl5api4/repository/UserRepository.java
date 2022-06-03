package com.test4.pbl5api4.repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

import com.test4.pbl5api4.model.UserModel;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {

    Optional<UserModel> findByEmail(String email);

//    @Query("FROM user u WHERE u.lastName=:search OR u.firstName =:search")
//    Page<UserModel> searchByName(Pageable pageable, @Param("search") String search);
//
//    @Query("{ 'name' : ?0 }")
//    List<UserModel> findUsersByName(String name);
}
