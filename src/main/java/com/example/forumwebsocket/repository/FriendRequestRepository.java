package com.example.forumwebsocket.repository;

import com.example.forumwebsocket.entity.FriendRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {
    public List<FriendRequest> findAllByReceiver_Id(Long receiverId);
}
