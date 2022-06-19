package com.test4.pbl5api4.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.test4.pbl5api4.model.ChangePassModel;
import com.test4.pbl5api4.model.DoubleIdObjectModel;
import com.test4.pbl5api4.model.IdObjectModel;
import com.test4.pbl5api4.model.UserModel;
import com.test4.pbl5api4.repository.PostRepository;
import com.test4.pbl5api4.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PostRepository postRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptEncoder;

    @Autowired
    private MongoTemplate mongoTemplate;


    public ResponseObjectService findAll() {
        ResponseObjectService responseObj = new ResponseObjectService();
        responseObj.setPayload(userRepo.findAll());
        responseObj.setStatus("thanh cong");
        responseObj.setMessage("thanh cong");
        return responseObj;
    }



    public ResponseObjectService findById(String id) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findById(id);
        if (optUser.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("user id: " + id + " ko ton tai");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            responseObj.setPayload(optUser.get());
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            return responseObj;
        }
    }

    public ResponseObjectService findFollowing(String id) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findById(id);
        if (optUser.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("user id: " + id + " ko ton tai");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            List<String> followingIds = optUser.get().getFollowing();
            List<UserModel> followingAccounts = new ArrayList<>();
            if (followingIds.size() > 0) {
                for (String followingId : followingIds) {
                    Optional<UserModel> optFollowingUser = userRepo.findById(followingId);
                    if (optFollowingUser.isPresent()) {
                        UserModel followingUser = optFollowingUser.get();
                        followingUser.setPassword("");
                        followingAccounts.add(followingUser);
                    } 
                }
                responseObj.setStatus("thanh cong");
                responseObj.setMessage("thanh cong");
                responseObj.setPayload(followingAccounts);
                return responseObj;
            } else {
                responseObj.setStatus("that bai");
                responseObj.setMessage("User id " + id + " ko theo doi ai");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }



    public ResponseObjectService findFollower(String id) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findById(id);
        if (optUser.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("user id: " + id + " ko ton tai");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            List<String> followerIds = optUser.get().getFollower();
            List<UserModel> followerAccounts = new ArrayList<>();
            if (followerIds.size() > 0) {
                for (String followerId : followerIds) {
                    Optional<UserModel> optFollowerUser = userRepo.findById(followerId);
                    if (optFollowerUser.isPresent()) {
                        UserModel followerUser = optFollowerUser.get();
                        followerUser.setPassword("");
                        followerAccounts.add(followerUser);
                    } 
                }
                responseObj.setStatus("thanh cong");
                responseObj.setMessage("thanh cong");
                responseObj.setPayload(followerAccounts);
                return responseObj;
            } else {
                responseObj.setStatus("that bai");
                responseObj.setMessage("User id " + id + "ko ai theo doi");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }

    public ResponseObjectService saveUser(UserModel inputUser) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findByEmail(inputUser.getEmail());
        if (optUser.isPresent()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("Email  " + inputUser.getEmail() + " da ton tai");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            inputUser.setPassword(bCryptEncoder.encode(inputUser.getPassword()));
            
            // user follows himself so he could get his posts in newsfeed as well
            UserModel user = userRepo.save(inputUser);
            List<String> listFollowing = user.getFollowing();
            if (listFollowing == null) {
                listFollowing = new ArrayList<>();
            }
            listFollowing.add(user.getId());
            user.setFollowing(listFollowing);
            this.updateWithoutPassword(user);
            responseObj.setPayload(user);
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            return responseObj;
        }
    }

    public boolean updateWithoutPassword(UserModel inputUser) {
        Optional<UserModel> optUser = userRepo.findById(inputUser.getId());
        if (optUser.isEmpty()) {
            return false;
        } else {
            UserModel currentUser = optUser.get();
            if (inputUser.getPassword().equals(currentUser.getPassword())) {
                userRepo.save(inputUser);
                return true;
            } else {
                return false;
            }
        }
    }

    public ResponseObjectService update(UserModel inputUser) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findById(inputUser.getId());
        if (optUser.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("user id: " + inputUser.getId() + " ko ton tai");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            UserModel currentUser = optUser.get();
//            if (bCryptEncoder.matches(inputUser.getPassword(), currentUser.getPassword())) {
//                inputUser.setPassword(bCryptEncoder.encode(inputUser.getPassword()));
                currentUser.setFirstName(inputUser.getFirstName());
                currentUser.setLastName(inputUser.getLastName());
//                inputUser.setAvata(inputUser.getAvata());
                responseObj.setPayload(userRepo.save(currentUser));
                responseObj.setStatus("thanh cong");
                responseObj.setMessage("thanh cong");
                return responseObj;
//            } else {
//                responseObj.setStatus("that bai");
//                responseObj.setMessage("mk ko dung");
//                responseObj.setPayload(null);
//                return responseObj;
//            }
        }
    }

    public ResponseObjectService changeName(UserModel inpUser, String id){
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findById(id);
        if( optUser.isEmpty()){
            responseObj.setStatus("that bai");
            responseObj.setMessage("that bai");
            responseObj.setPayload(null);
        }
        else{
            UserModel currentUser = optUser.get();
            currentUser.setFirstName(inpUser.getFirstName());
            currentUser.setLastName(inpUser.getLastName());
            currentUser.setAvata(inpUser.getAvata());
            currentUser.setUserName(inpUser.getUserName());
            currentUser.setBirthDate(inpUser.getBirthDate());
            currentUser.setAddress(inpUser.getAddress());
            currentUser.setPhoneNumber(inpUser.getPhoneNumber());
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            responseObj.setPayload(userRepo.save(currentUser));
        }
        return responseObj;
    }

    public ResponseObjectService changePass(UserModel inpPass){
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findById(inpPass.getId());
        if (optUser.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("user id: " + inpPass.getId() + " ko ton tai");
            responseObj.setPayload(null);
            return responseObj;
        }else{
            inpPass.setPassword(bCryptEncoder.encode(inpPass.getPassword()));
            UserModel user = userRepo.save(inpPass);
            responseObj.setPayload(user);
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            return responseObj;
        }

    }

    public ResponseObjectService search(String search){
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.searchByName(search);
        if(optUser.isEmpty()){
            responseObj.setStatus("that bai");
            responseObj.setMessage("user ko ton tai");
            responseObj.setPayload(null);
            return responseObj;
        }else{
            List<UserModel> searchList = new ArrayList<>();
            responseObj.setPayload(optUser.get());
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            return responseObj;
        }
    }

    public ResponseObjectService followUser(DoubleIdObjectModel doubleId) {
        // id1 - followed user, id2 - follower

        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optFollowedUser = userRepo.findById(doubleId.getId1());
        Optional<UserModel> optFollower = userRepo.findById(doubleId.getId2());
        if (optFollowedUser.isEmpty() || optFollower.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("id ko hop le");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            UserModel followedUser = optFollowedUser.get();
            UserModel follower = optFollower.get();

            // them follower vao list
            List<String> followerList = followedUser.getFollower();
            if (followerList == null) {
                followerList = new ArrayList<>();
            }
            followerList.add(follower.getId());
            followedUser.setFollower(followerList);

            // them following vao list
            List<String> followingList = follower.getFollowing();
            if (followingList == null) {
                followingList = new ArrayList<>();
            }
            followingList.add(followedUser.getId());
            follower.setFollowing(followingList);

            userRepo.save(followedUser);
            userRepo.save(follower);

            responseObj.setStatus("thanh cong");
            responseObj.setMessage(
                    "User id " + follower.getId() + " da theo doi user id " + followedUser.getId());
            responseObj.setPayload(new IdObjectModel(doubleId.getId1()));
            return responseObj;
        }
    }

    public ResponseObjectService unfollowUser(DoubleIdObjectModel doubleId) {
        // id1 - followed user, id2 - follower

        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optFollowedUser = userRepo.findById(doubleId.getId1());
        Optional<UserModel> optFollower = userRepo.findById(doubleId.getId2());
        if (optFollowedUser.isEmpty() || optFollower.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("id hop le");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            UserModel followedUser = optFollowedUser.get();
            UserModel follower = optFollower.get();

            // them vao follower list
            List<String> followerList = followedUser.getFollower();
            if (followerList == null) {
                followerList = new ArrayList<>();
            }
            followerList.remove(follower.getId());
            followedUser.setFollower(followerList);

            // them vao following list
            List<String> followingList = follower.getFollowing();
            if (followingList == null) {
                followingList = new ArrayList<>();
            }
            followingList.remove(followedUser.getId());
            follower.setFollowing(followingList);

            userRepo.save(followedUser);
            userRepo.save(follower);

            responseObj.setStatus("thanh cong");
            responseObj.setMessage(
                    "User id " + follower.getId() + " da huy theo doi user id " + followedUser.getId());
            responseObj.setPayload(new IdObjectModel(doubleId.getId1()));
            return responseObj;
        }
    }

    public ResponseObjectService getAva(String id){
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<UserModel> optUser = userRepo.findById(id);
        if(optUser.isEmpty()) {
            responseObj.setStatus("that bai");
            responseObj.setMessage("user id: " + id + " ko ton tai");
            responseObj.setPayload(null);
            return responseObj;
        }else{
            UserModel user = optUser.get();
            String ava = user.getAvata();
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            responseObj.setPayload(ava);
            return responseObj;
        }
    }

    public ResponseObjectService searchByLastName(String lastName){
        ResponseObjectService responseObj = new ResponseObjectService();

        Query query =  new Query();
        query.addCriteria(Criteria.where("lastName").is(lastName));
        List<UserModel> user = mongoTemplate.find(query, UserModel.class);

        if(user.size()>1){
            responseObj.setStatus("thanh cong");
            responseObj.setMessage("thanh cong");
            responseObj.setPayload(user);
            return responseObj;
        }
        if(user.isEmpty()){
            responseObj.setStatus("that bai");
            responseObj.setMessage("ko tim thay nguoi dung: " + lastName);
            responseObj.setPayload(null);
            return responseObj;
        }
       return responseObj;
    }



    // ****
    // su dungj email de login thay vi user name
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserModel> optUser = userRepo.findByEmail(email);
        User springUser = null;

        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException("ko the tim thay: " + email);
        } else {
            UserModel foundUser = optUser.get();
            String role = foundUser.getRole();
            Set<GrantedAuthority> ga = new HashSet<>();
            ga.add(new SimpleGrantedAuthority(role));
            springUser = new User(foundUser.getEmail(), foundUser.getPassword(), ga);
            return springUser;
        }
    }
}
