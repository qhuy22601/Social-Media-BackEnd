package com.test4.pbl5api4.controller;

import com.test4.pbl5api4.model.DoubleIdObjectModel;
import com.test4.pbl5api4.model.IdObjectModel;
import com.test4.pbl5api4.model.PostModel;
import com.test4.pbl5api4.repository.PostRepository;
import com.test4.pbl5api4.service.PostService;
import com.test4.pbl5api4.service.ResponseObjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepo;
    @Autowired
    private PostService postService;

    @PostMapping("/insertpost")
    public ResponseEntity<ResponseObjectService> insertPost(@RequestBody PostModel inputPost) {
        return new ResponseEntity<ResponseObjectService>(postService.insertPost(inputPost), HttpStatus.OK);
    }

    @PostMapping("/myposts")
    public ResponseEntity<ResponseObjectService> findPostByUserId(@RequestBody IdObjectModel inputUserId) {
        return new ResponseEntity<ResponseObjectService>(postService.findPostByUserId(inputUserId), HttpStatus.OK);
    }

    @PostMapping("/followingposts")
    public ResponseEntity<ResponseObjectService> findPostByFollowing(@RequestBody IdObjectModel inputUserId) {
        return new ResponseEntity<ResponseObjectService>(postService.findPostByFollowing(inputUserId), HttpStatus.OK);
    }

    // currently not in use, post is update via comment controller
    // @PutMapping("/updatebycomment")
    // public ResponseEntity<ResponseObjectService> updateByComment(@RequestBody PostEntity inputPost) {
    //     return new ResponseEntity<ResponseObjectService>(postService.updatePostByComment(inputPost), HttpStatus.OK);
    // }

    @PostMapping("/likepost")
    public ResponseEntity<ResponseObjectService> likePost(@RequestBody DoubleIdObjectModel doubleId) {
        return new ResponseEntity<ResponseObjectService>(postService.updatePostByLike(doubleId), HttpStatus.OK);
    }

    @PostMapping("/sharepost")
    public ResponseEntity<ResponseObjectService> sharePost(@RequestBody DoubleIdObjectModel doubleId) {
        return new ResponseEntity<ResponseObjectService>(postService.updatePostByShare(doubleId), HttpStatus.OK);
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<HttpStatus> delpost(@PathVariable("id") String id){
        postRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
