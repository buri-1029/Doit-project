package com.ssafy.doit.service.feed;

import com.ssafy.doit.model.*;
import com.ssafy.doit.model.feed.CommitGroup;
import com.ssafy.doit.model.feed.CommitUser;
import com.ssafy.doit.model.feed.Feed;
import com.ssafy.doit.model.feed.FeedUser;
import com.ssafy.doit.model.group.Group;
import com.ssafy.doit.model.group.GroupUser;
import com.ssafy.doit.model.response.ResMyFeed;
import com.ssafy.doit.model.response.ResponseFeed;
import com.ssafy.doit.model.response.ResponseUser;
import com.ssafy.doit.model.user.User;
import com.ssafy.doit.repository.*;
import com.ssafy.doit.repository.feed.*;
import com.ssafy.doit.repository.group.GroupRepository;
import com.ssafy.doit.repository.group.GroupUserRepository;
import com.ssafy.doit.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {

	@Autowired
	private S3Service s3Service;
	@Autowired
	private final GroupRepository groupRepository;
	@Autowired
	private final GroupUserRepository groupUserRepository;
	@Autowired
	private final FeedRepository feedRepository;
	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final FeedUserRepository feedUserRepository;
	@Autowired
	private final CommentRepository commentRepository;
	@Autowired
	private final CommitUserRepository commitUserRepository;
	@Autowired
	private final CommitGroupRepository commitGroupRepository;
	@Autowired
	private MileageRepository mileageRepository;

	// 오늘 하루 인증 피드 등록한 그룹원 리스트
	@Transactional
	public List<ResponseUser> todayAuthUser(Long groupPk) throws Exception {
		List<ResponseUser> resList = new ArrayList<>();
		Group group = groupRepository.findById(groupPk)
									 .get();

		List<GroupUser> userList = group.userList;
		for (GroupUser gu : userList) {
			Long userPk = gu.getUser()
							.getId();
			Optional<Feed> optFeed = feedRepository.findByGroupPkAndWriterAndCreateDate
													   (groupPk,
														userPk,
														"true",
														LocalDate.now()
																 .toString());
			if (optFeed.isPresent()) {
				resList.add(new ResponseUser(gu.getUser()));
			}
		}
		return resList;
	}

	// 그룹 내 피드 생성
	@Transactional
	public Long createFeed(Long userPk, Feed feedReq) throws Exception {
		Group group = groupRepository.findById(feedReq.getGroupPk())
									 .get();
		User user = userRepository.findById(userPk)
								  .get();

		Optional<GroupUser> optGU = groupUserRepository.findByGroupAndUser(group, user);
		if (!optGU.isPresent()) {
			throw new Exception("해당 그룹에 가입되어 있지 않아 접근 불가합니다.");
		}

		Feed feed = feedRepository.save(Feed.builder()
											.content(feedReq.getContent())
											.feedType(feedReq.getFeedType())
											.createDate(LocalDateTime.now())
											.groupPk(feedReq.getGroupPk())
											.writer(userPk)
											.build());

		if (feedReq.getFeedType()
				   .equals("true")) {
			user.setMileage(user.getMileage() + 100);
			userRepository.save(user);
			mileageRepository.save(Mileage.builder()
										  .content("인증피드 등록 마일리지 지급")
										  .date(LocalDateTime.now())
										  .mileage("+100")
										  .user(user)
										  .build());
		} else if (feedReq.getFeedType()
						  .equals("false")) {
			user.setMileage(user.getMileage() + 100);
			userRepository.save(user);
			mileageRepository.save(Mileage.builder()
										  .content("공유피드 등록 마일리지 지급")
										  .date(LocalDateTime.now())
										  .mileage("+100")
										  .user(user)
										  .build());
		}
		return feed.getFeedPk();
	}

	// 피드 파일 등록/수정
	@Transactional
	public void updateImg(Long feedPk, MultipartFile file) throws Exception {
		Feed feed = feedRepository.findById(feedPk)
								  .get();
		String mediaPath = s3Service.upload(feed.getMedia(), file);
		feed.setMedia(mediaPath);
		feedRepository.save(feed);
	}

	// 그룹 내 피드 리스트
	@Transactional
	public List<ResponseFeed> groupFeedList(Long userPk, Long groupPk, String start, String end)
		throws Exception {
		Group group = groupRepository.findById(groupPk)
									 .get();
		User user = userRepository.findById(userPk)
								  .get();

		Optional<GroupUser> optGU = groupUserRepository.findByGroupAndUser(group, user);
		if (!optGU.isPresent()) {
			throw new Exception("해당 그룹에 가입되어 있지 않아 접근 불가합니다.");
		}

		List<Feed> feedList = feedRepository.findAllByGroupPkAndStatusAndCreateDateBetween(
			groupPk, "true", start, end);
		List<ResponseFeed> resList = new ArrayList<>();
		for (Feed feed : feedList) {
			String nickname = userRepository.findById(feed.getWriter())
											.get()
											.getNickname();
			resList.add(new ResponseFeed(feed, nickname));
		}
		return resList;
	}

	// 개인 피드 리스트
	@Transactional
	public List<ResMyFeed> userFeedList(Long userPk) {
		List<Feed> list = feedRepository.findAllByWriterAndStatusOrderByCreateDateDesc(
			userPk, "true");
		List<ResMyFeed> resList = new ArrayList<>();
		for (Feed feed : list) {
			String nickname = userRepository.findById(feed.getWriter())
											.get()
											.getNickname();
			String groupName = groupRepository.findById(feed.getGroupPk())
											  .get()
											  .getName();
			resList.add(new ResMyFeed(feed, nickname, groupName));
		}
		return resList;
	}

	// 개인 피드 수정
	@Transactional
	public void updateFeed(Long userPk, Feed feedReq) throws Exception {
		Optional<Feed> feed = feedRepository.findById(feedReq.getFeedPk());
		if (userPk == feed.get()
						  .getWriter()
						  .longValue()) {
			feed.ifPresent(selectFeed -> {
				selectFeed.setContent(feedReq.getContent());
				selectFeed.setFeedType(feedReq.getFeedType());
				selectFeed.setUpdateDate(LocalDateTime.now()
													  .toString());
				feedRepository.save(selectFeed);
			});
		} else {
			throw new Exception("피드 작성자가 아닙니다.");
		}
	}

	// 개인 피드 삭제
	@Transactional
	public void deleteFeed(Long userPk, Long feedPk) throws Exception {
		Optional<Feed> feed = feedRepository.findById(feedPk);
		User user = userRepository.findById(userPk)
								  .get();

		if (userPk.equals(feed.get()
							  .getWriter())) {
			feedUserRepository.deleteByFeedPk(feedPk);
			commentRepository.deleteByFeedPk(feedPk);
			feed.ifPresent(selectFeed -> {
				try {
					feedRepository.delete(selectFeed);
					s3Service.deleteFile(selectFeed.getMedia());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			user.setMileage(user.getMileage() - 50);
			userRepository.save(user);
			mileageRepository.save(Mileage.builder()
										  .content("피드 삭제 마일리지 차감")
										  .date(LocalDateTime.now())
										  .mileage("-50")
										  .user(user)
										  .build());
		} else {
			throw new Exception("피드 작성자가 아닙니다.");
		}
	}

	// 그룹을 탈퇴한 경우 그룹&회원의 피드 삭제
	@Transactional
	public void deleteFeedByGroupUser(Long userPk, Long groupPk) throws Exception {
		List<Feed> feedList = feedRepository.findByGroupPkAndWriter(groupPk, userPk);
		getObject(feedList);
	}

	// 회원이 탈퇴했거나 강퇴된 경우 그 회원의 모든 피드 삭제
	@Transactional
	public void deleteFeedByUser(Long userPk) throws Exception {
		List<Feed> feedList = feedRepository.findByWriter(userPk);
		getObject(feedList);
	}

	// 관리자가 그룹을 삭제했을 경우 그 그룹과 관련된 모든 피드 삭제
	@Transactional
	public void deleteFeedByGroup(Long groupPk) throws Exception {
		List<Feed> feedList = feedRepository.findByGroupPk(groupPk);
		getObject(feedList);
	}

	public void getObject(List<Feed> feedList) throws Exception {
		for (Feed feed : feedList) {
			s3Service.deleteFile(feed.getMedia());
			feedUserRepository.deleteByFeedPk(feed.getFeedPk());
			commentRepository.deleteByFeedPk(feed.getFeedPk());
			feedRepository.deleteById(feed.getFeedPk());
		}
	}

	// 인증피드 인증확인
	@Transactional
	public Feed authCheckFeed(Long userPk, Long feedPk) throws Exception {
		Feed feed = feedRepository.findById(feedPk)
								  .get();
		User user = userRepository.findById(userPk)
								  .get();
		Group group = groupRepository.findById(feed.getGroupPk())
									 .get();

		Optional<GroupUser> optGU = groupUserRepository.findByGroupAndUser(group, user);
		if (!optGU.isPresent()) {
			throw new Exception("해당 그룹에 가입되어 있지 않아 접근 불가합니다.");
		}

		if (feed.getAuthCheck()
				.equals("true")) {
			throw new Exception("인증완료된 피드입니다.");
		}

		if (!feed.getFeedType()
				 .equals("true")) {
			throw new Exception("인증피드가 아닙니다.");
		}

		if (userPk == feed.getWriter()
						  .longValue()) {
			throw new Exception("자신이 올린 피드에는 인증할 수 없습니다.");
		}

		Optional<FeedUser> optFU = feedUserRepository.findByFeedAndUser(feed, user);
		if (optFU.isPresent()) {
			throw new Exception("이미 해당 피드에 인증을 하였습니다.");
		}

		feed.setAuthCnt(feed.getAuthCnt() + 1);     // 인증피드 확인한 그룹원 수 +1
		feedUserRepository.save(FeedUser.builder()  // FeedUser 테이블에도
										.feed(feed)
										.user(user)
										.build());    // 그 피드에 인증 확인한 그룹원 추가

		user.setMileage(user.getMileage() + 50);    // 인증피드 확인 마일리지 지금 + 50
		userRepository.save(user);
		mileageRepository.save(Mileage.builder()
									  .content("인증피드 확인 마일리지 지급")
									  .date(LocalDateTime.now())
									  .mileage("+50")
									  .user(user)
									  .build());


		Long groupPk = feed.getGroupPk();
		Long writerPk = feed.getWriter();
		User writer = userRepository.findById(writerPk)
									.get();
		LocalDate date = feed.getCreateDate()
							 .toLocalDate();
		int cnt = feed.getAuthCnt();
		int total = group.getTotalNum() - 1;
		if (cnt >= Math.round(total * 0.7)) {       // 그룹의 현재 총 인원수의 70%(반올림) 이상이 인증확인하면
			feed.setAuthCheck("true");              // 그 인증피드는 인증완료
			feedRepository.save(feed);

			writer.setMileage(writer.getMileage() + 100); // 인증피드 인증 완료 마일리지 지금 + 100
			userRepository.save(writer);
			mileageRepository.save(Mileage.builder()
										  .content("인증피드 인증 완료 마일리지 지급")
										  .date(LocalDateTime.now())
										  .mileage("+100")
										  .user(writer)
										  .build());

			Optional<CommitUser> optCU = commitUserRepository.findByUserPkAndDate(writerPk, date);
			if (optCU.isPresent()) {
				CommitUser cu = optCU.get();
				cu.setCnt(cu.getCnt() + 1);
				commitUserRepository.save(cu);
			}

			int groupTotal = group.getTotalNum();
			Optional<CommitGroup> optCG = commitGroupRepository.findByGroupPkAndDate(groupPk, date);
			if (optCG.isPresent()) {
				CommitGroup cg = optCG.get();
				if (cg.getTotal() != groupTotal) {
					cg.setTotal(groupTotal);
				}
				int authCnt = cg.getCnt() + 1;

				double calc = 0;
				if (groupTotal > 9) {
					calc = authCnt / (double) groupTotal * 100.0;
					calc += calc * 1.05 * 2;
				} else if (groupTotal > 4) {
					calc = authCnt / (double) groupTotal * 100.0;
					calc += calc * 1.05;
				} else {
					calc = authCnt / (double) groupTotal * 100.0;
				}

				cg.setCnt(authCnt);
				cg.setCalc(Math.round(calc * 10) / 10.0);
				commitGroupRepository.save(cg);
			} else {
				double calc = 1.0 / (double) groupTotal * 100.0;
				commitGroupRepository.save(CommitGroup.builder()
													  .date(date)
													  .groupPk(groupPk)
													  .total(groupTotal)
													  .calc(Math.round(calc * 10) / 10.0)
													  .cnt(1)
													  .build());
			}
		}

		return feed;
	}
}