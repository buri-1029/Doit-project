package com.ssafy.doit.service;

import com.ssafy.doit.model.feed.Feed;
import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.store.Product;
import com.ssafy.doit.repository.feed.CommentRepository;
import com.ssafy.doit.repository.feed.FeedRepository;
import com.ssafy.doit.repository.feed.FeedUserRepository;
import com.ssafy.doit.repository.group.GroupHashTagRepository;
import com.ssafy.doit.repository.group.GroupRepository;
import com.ssafy.doit.repository.group.GroupUserRepository;
import com.ssafy.doit.repository.store.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupHashTagRepository groupHashTagRepository;
    @Autowired
    private GroupUserRepository groupUserRepository;
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private FeedUserRepository feedUserRepository;
    @Autowired
    private CommentRepository commentRepository;


    // 관리자에 의한 그룹 삭제
    public void deleteGroupByAdmin(Long groupPk) {
        Optional<Group> groupInfo = groupRepository.findById(groupPk);
        if (groupInfo.isPresent()) {
            groupInfo.ifPresent(selectGroup -> {
                selectGroup.setStatus("false");
                groupRepository.save(selectGroup);
            });
        }
        groupHashTagRepository.deleteByGroupPk(groupPk);
        groupUserRepository.deleteByGroupPk(groupPk);
    }

    // 관리자에 의한 피드 삭제
    public void deleteFeed(Long feedPk) {
        Optional<Feed> feedInfo = feedRepository.findById(feedPk);
        feedInfo.ifPresent(selectFeed -> {
            try {
                s3Service.deleteFile(selectFeed.getMedia());
            } catch (Exception e) {
                e.printStackTrace();
            }
            feedRepository.delete(selectFeed);
        });
        feedUserRepository.deleteByFeedPk(feedPk);
        commentRepository.deleteByFeedPk(feedPk);
    }

}
