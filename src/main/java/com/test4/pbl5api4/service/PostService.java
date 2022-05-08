package com.test4.pbl5api4.service;

import java.time.Instant;
import java.util.*;

import com.test4.pbl5api4.model.DoubleIdObjectModel;
import com.test4.pbl5api4.model.IdObjectModel;
import com.test4.pbl5api4.model.PostByFollowing;
import com.test4.pbl5api4.model.PostModel;
import com.test4.pbl5api4.model.UserModel;
import com.test4.pbl5api4.repository.PostRepository;
import com.test4.pbl5api4.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private UserRepository userRepo;

    private List<PostModel> list;
    
    public ResponseObjectService insertPost(PostModel inputPost) {
        ResponseObjectService responseObj = new ResponseObjectService();
        inputPost.setCreatedAt(Instant.now());
        responseObj.setStatus("thanh cong");
        responseObj.setMessage("thanh cong");
        responseObj.setPayload(postRepo.save(inputPost));
        return responseObj;
    }

//    public ResponseObjectService delPost(PostModel inputPost) {
//        ResponseObjectService responseObj = new ResponseObjectService();
//        responseObj.setPayload(userRepo.findAll());
//        Optional<List<PostModel>> listPost = postRepo.findAll();
//        responseObj.setStatus("thanh cong");
//        responseObj.setMessage("thanh cong");
//        return responseObj;
//    }

    public ResponseObjectService findPostByUserId(IdObjectModel inputUserId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<List<PostModel>> userPostsOpt = postRepo.findByUserIdOrderByCreatedAtDesc(inputUserId.getId());
        if (userPostsOpt.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("ko tim thay post nao cua user id: " + inputUserId.getId());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            List<PostModel> userPosts = userPostsOpt.get();
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            responseObj.setPayload(userPosts);
            return responseObj;
        }
    }
    
    public ResponseObjectService findPostByFollowing(IdObjectModel inputUserId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findById(inputUserId.getId());
        if (optUser.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("ko tim thay post nao cua user id: " + inputUserId.getId());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            UserModel user = optUser.get();
            if (user.getFollowing() != null) {
                // neu nguoi dung theo doi ai thi lay id cua nguoi kia 
                List<String> followingIds = new ArrayList<>();
                for (String id : user.getFollowing()) {
                    followingIds.add(id);
                }
                // lay het bai viet bang id user
                List<PostByFollowing> listPosts = new ArrayList<>();
                for (String followingId : followingIds) {
                    // lau nguoi thero doi bang id
                    UserModel followingUser = new UserModel();
                    Optional<UserModel> optFollowingUser = userRepo.findById(followingId);
                    if (optFollowingUser.isPresent()) {
                        followingUser = optFollowingUser.get();
                    }

                    followingUser.setPassword("");
                    
                    // lay ra bai viet tuongw tuwj
                    Optional<List<PostModel>> followingPostsOpt = postRepo.findByUserId(followingId);
                    if (followingPostsOpt.isPresent()) {
                        // neu tai khoan co bai viet nao thi gom no lai
                        List<PostModel> followingPosts = followingPostsOpt.get();
                        if (followingPosts != null) {
                            for (PostModel item : followingPosts) {
                                listPosts.add(new PostByFollowing(followingUser, item));
                            }
                        }
                    }
                }
                Collections.sort(listPosts, (o1, o2) -> o2.getPost().getCreatedAt().compareTo(o1.getPost().getCreatedAt()));
                responseObj.setStatus("thanh cong");
                responseObj.setMessage("thanh cong");
                responseObj.setPayload(listPosts);
                return responseObj;
            } else {
                responseObj.setStatus("that bai");
                responseObj.setMessage("user id: " + inputUserId.getId() + " ko co nguoi theo doi");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }

    public ResponseObjectService updatePostByComment(PostModel inputPost) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostModel> optPost = postRepo.findById(inputPost.getId());
        if (optPost.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("ko the tim thay post id: " + inputPost.getId());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            // inputPost.setCreatedAt(Instant.now());
            postRepo.save(inputPost);
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("ok");
            responseObj.setPayload(inputPost);
            return responseObj;
        }
    }

    public ResponseObjectService updatePostByLike(DoubleIdObjectModel doubleId) {
        // id 1 - post Id, id 2 - nguoiwf like
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostModel> optPost = postRepo.findById(doubleId.getId1());
        if (optPost.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("ko tim thay post id: " + doubleId.getId1());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            PostModel targetPost = optPost.get();
            List<String> likeList = targetPost.getLike();
            if (likeList == null) {
                likeList = new ArrayList<>();
            }
            // like and unlike a post
            if (!likeList.contains(doubleId.getId2())) {
                likeList.add(doubleId.getId2());
            } else {
                likeList.remove(doubleId.getId2());
            }
            targetPost.setLike(likeList);
            postRepo.save(targetPost);
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("update like trong post id: " + targetPost.getId());
            responseObj.setPayload(targetPost);
            return responseObj;
        }
    }

    public ResponseObjectService updatePostByShare(DoubleIdObjectModel doubleId) {
        // id 1 - post Id, id 2 - nguowif chia se
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostModel> optPost = postRepo.findById(doubleId.getId1());
        if (optPost.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("ko tim thay post id: " + doubleId.getId1());
            responseObj.setPayload(null);
            return responseObj;
        } else {
            PostModel targetPost = optPost.get();
            List<String> shareList = targetPost.getShare();
            if (shareList == null) {
                shareList = new ArrayList<>();
            }
            // save va update nguoi da chia sex
            shareList.add(doubleId.getId2());
            targetPost.setShare(shareList);
            postRepo.save(targetPost);
            // reset list nguoiwf da chia se
            targetPost.setUserId(doubleId.getId2());
            targetPost.setId(null);
            targetPost.setContent("Chia sẽ bài: " + targetPost.getContent());
            targetPost.setLike(new ArrayList<>());
            targetPost.setShare(new ArrayList<>());
            targetPost.setComment(new ArrayList<>());
            postRepo.save(targetPost);

            responseObj.setStatus("thanh cong");
            responseObj.setMessage("them bai da chia se: " + targetPost.getId());
            responseObj.setPayload(targetPost);
            return responseObj;
        }
    }
}
