package com.test4.pbl5api4.repository;

import java.util.List;
import java.util.Optional;

import com.test4.pbl5api4.model.PostModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<PostModel, String> {
    Optional<List<PostModel>> findByUserId(String id);
    Optional<List<PostModel>> findByUserIdOrderByCreatedAtDesc(String id);

    
}
