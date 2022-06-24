package com.test4.pbl5api4.repository;

import java.util.List;
import java.util.Optional;

import com.test4.pbl5api4.model.UserModel;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {

    //  Criteria criteria;


    Optional<UserModel> findByEmail(String email);

//    @Query("select from user u where u.username like %:username%")
//    Optional<UserModel> searchByName(@Param("search") String search);


//    @Query("select  u from UserModel u where u.name like %:name%")
//    List<UserModel> testLike (@Param("name") String name);

    List<UserModel> findUserModelByUsernameIsLike(String name);

    @Query("{'username' : :#{#username}}")
    List<UserModel> findNamedParameters(@Param("username")String username);



}
