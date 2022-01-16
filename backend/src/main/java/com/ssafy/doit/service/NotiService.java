package com.ssafy.doit.service;

import com.ssafy.doit.model.feed.Feed;
import com.ssafy.doit.model.notification.NotiType;
import com.ssafy.doit.model.notification.Notification;
import com.ssafy.doit.model.response.ResMyFeed;
import com.ssafy.doit.model.response.ResponseChatRoom;
import com.ssafy.doit.model.store.ChatRoom;
import com.ssafy.doit.repository.UserRepository;
import com.ssafy.doit.repository.feed.FeedRepository;
import com.ssafy.doit.repository.group.GroupRepository;
import com.ssafy.doit.repository.NotiRepository;
import com.ssafy.doit.repository.store.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotiService {
    @Autowired
    private final NotiRepository notiRepository;
    @Autowired
    private final ChatRoomRepository chatRoomRepository;
    @Autowired
    private final GroupRepository groupRepository;
    @Autowired
    private final FeedRepository feedRepository;
    @Autowired
    private final UserRepository userRepository;

    public List<Notification> getList(Long uid) throws Exception{
        List<Notification> notifications = notiRepository.findAllByUserPkOrderByNotiDateDesc(uid);
        List<Notification> res  = new ArrayList<>();

        for(Notification noti : notifications){
            setTarget(noti);
            if(noti.getTarget() == null)
                notiRepository.delete(noti);
            else res.add(noti);
        }

        return res;
    }

    public Notification setTarget(Notification n) {
        Long id = n.getTargetId();

        if(n.getNotiType() == NotiType.NEWCHAT && chatRoomRepository.findById(id).isPresent()){
            ChatRoom chatRoom = chatRoomRepository.findById(id).get();
            n.setTarget(new ResponseChatRoom(chatRoom, n.getUserPk()));
        }
        else if((n.getNotiType() == NotiType.KICKOUT || n.getNotiType() == NotiType.AUTHORIZE ) && groupRepository.findById(id).isPresent())
            n.setTarget(groupRepository.findById(id).get());
        else if(feedRepository.findById(id).isPresent()) {
            Feed feed = feedRepository.findById(id).get();
            String writer = userRepository.findById(feed.getWriter()).get().getNickname();
            String group = groupRepository.findById(feed.getGroupPk()).get().getName();
            n.setTarget(new ResMyFeed(feed, writer, group));
        }

        return n;
    }

    public void refreshChat(Notification noti){
        Optional<Notification> notification = notiRepository.findByUserPkAndNotiTypeAndTargetId(noti.getUserPk(), NotiType.NEWCHAT, noti.getTargetId());
        if(notification.isPresent()) notiRepository.delete(notification.get());
    }

    public void save(Notification notification){
        notiRepository.save(notification);
    }
    public void delete(Long id) {notiRepository.delete(notiRepository.findById(id).get());}
}
