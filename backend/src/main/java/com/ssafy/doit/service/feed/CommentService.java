package com.ssafy.doit.service.feed;


import com.ssafy.doit.model.feed.Comment;
import com.ssafy.doit.model.feed.Feed;
import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.group.GroupUser;
import com.ssafy.doit.model.response.ResComment;

import com.ssafy.doit.model.user.User;
import com.ssafy.doit.repository.*;
import com.ssafy.doit.repository.feed.CommentRepository;
import com.ssafy.doit.repository.feed.FeedRepository;
import com.ssafy.doit.repository.group.GroupRepository;
import com.ssafy.doit.repository.group.GroupUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final GroupRepository groupRepository;
	@Autowired
	private final GroupUserRepository groupUserRepository;
	@Autowired
	private FeedRepository feedRepository;
	@Autowired
	private CommentRepository commentRepository;

	@Transactional
	public void createComment(Long userPk, Comment comment) throws Exception {

		Feed feed = feedRepository.findById(comment.getFeedPk()).get();
		Group group = groupRepository.findById(feed.getGroupPk()).get();
		User user = userRepository.findById(userPk).get();

		Optional<GroupUser> optGU = groupUserRepository.findByGroupAndUser(group, user);
        if (!optGU.isPresent()) {
            throw new Exception("해당 그룹에 가입되어 있지 않아 접근 불가합니다.");
        }

		commentRepository.save(Comment.builder()
									  .content(comment.getContent())
									  .feedPk(comment.getFeedPk())
									  .userPk(userPk)
									  .createDate(LocalDateTime.now())
									  .build());
	}

	@Transactional
	public List<ResComment> commentList(Long feedPk) {
		List<Comment> list = commentRepository.findByFeedPk(feedPk);
		List<ResComment> resList = new ArrayList<>();

		for (Comment comment : list) {
			String nickname = userRepository.findById(comment.getUserPk()).get().getNickname();
			String userImg = userRepository.findById(comment.getUserPk()).get().getImage();
			Long userPk = userRepository.findById(comment.getUserPk()).get().getId();
			resList.add(new ResComment(comment, nickname, userImg, userPk));
		}
		return resList;
	}

	@Transactional
	public void updateComment(Long userPk, Comment commentReq) throws Exception {
		Optional<Comment> comment = commentRepository.findById(commentReq.getCommentPk());
        if (userPk.equals(comment.get().getUserPk())) {
            comment.ifPresent(selectComment -> {
                selectComment.setContent(commentReq.getContent());
                selectComment.setUpdateDate(LocalDateTime.now()
                                                         .toString());
                commentRepository.save(selectComment);
            });
        } else {
            throw new Exception("댓글 작성자가 아닙니다.");
        }
	}

	public void deleteComment(Long userPk, Long commentPk) throws Exception {
		Optional<Comment> comment = commentRepository.findById(commentPk);
		if (userPk.equals(comment.get().getUserPk())) {
			comment.ifPresent(selectComment -> {
				commentRepository.delete(selectComment);
			});
		} else
			throw new Exception("댓글 작성자가 아닙니다.");
	}
}
