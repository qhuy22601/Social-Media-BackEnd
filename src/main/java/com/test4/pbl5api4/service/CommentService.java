package com.test4.pbl5api4.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.test4.pbl5api4.model.CommentModel;
import com.test4.pbl5api4.model.PostModel;
import com.test4.pbl5api4.repository.CommentRepository;
import com.test4.pbl5api4.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private PostService postService;

    public ResponseObjectService insertComment(CommentModel inputComment, String inputPostId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostModel> optPost = postRepo.findById(inputPostId);
        if (optPost.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("ko tim thay post id: " + inputPostId);
            responseObj.setPayload(null);
            return responseObj;
        } else {
            inputComment.setCreatedAt(Instant.now());
            commentRepo.save(inputComment);
            PostModel targetPost = optPost.get();
            List<CommentModel> commentList = targetPost.getComment();
            if (commentList == null) {
                commentList = new ArrayList<>();
            }
            commentList.add(inputComment);
            targetPost.setComment(commentList);
            postService.updatePostByComment(targetPost);
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            responseObj.setPayload(inputComment);
            return responseObj;
        }
    }

    public ResponseObjectService getComments(String inputPostId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostModel> optTargetPost = postRepo.findById(inputPostId);
        if (optTargetPost.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("that bai");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            PostModel targetPost = optTargetPost.get();
            List<CommentModel> commentList = targetPost.getComment();
            if (commentList.size() > 0) {
                responseObj.setStatus("thanh cong");
                responseObj.setMessage("thanh cong");
                responseObj.setPayload(commentList);
                return responseObj;
            } else {
                responseObj.setStatus("thanh cong");
                responseObj.setMessage("Post id " + inputPostId + " ko cos comment nao");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }

}
