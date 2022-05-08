package com.test4.pbl5api4.repository;

import com.test4.pbl5api4.model.CommentModel;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<CommentModel, String> {
    
}