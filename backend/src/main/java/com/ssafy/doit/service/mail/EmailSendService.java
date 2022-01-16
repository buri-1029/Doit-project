package com.ssafy.doit.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class EmailSendService {
    @Autowired
    private JavaMailSenderImpl mailSender;
    private int size;

    //인증키 생성
    private String getKey(int size) {
        this.size = size;
        return getAuthCode();
    }

    //인증코드 난수 발생
    private String getAuthCode() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while(buffer.length() < size) {
            num = random.nextInt(10);
            buffer.append(num);
        }
        return buffer.toString();
    }

    //회원가입 인증메일 보내기
    public String sendSignupMail(String email) {
        //6자리 난수 인증번호 생성
        String authKey = getKey(6);
        //인증메일 보내기
        try {
            MailUtil sendMail = new MailUtil(mailSender);
            sendMail.setSubject("Doit 회원가입 이메일 인증");
            sendMail.setText(new StringBuffer()
                    .append("<h1> Doit 회원가입 이메일 인증</h1>")
                    .append("<h3> 함께 만들어가는 목표 관리 커뮤니티 <strong>Doit</strong> 입니다. </h3>")
                    .append("<h4> 아래 링크를 클릭하시면 가입완료 페이지로 이동됩니다. </h4>")
                    .append("<a href='http://i4c108.p.ssafy.io:80/user/complete?email=")
                    .append(email)
                    .append("&authKey=")
                    .append(authKey)
                    .append("' target='_blenk'>회원가입 이메일 인증 확인</a>")
                    .toString());
            sendMail.setFrom("buhee1029@gmail.com", "Doit 관리자");
            sendMail.setTo(email);
            sendMail.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authKey;
    }

    // 비번변경 인증메일 보내기
    public String sendChangePwMail(String email) {
        //6자리 난수 인증번호 생성
        String authKey = getKey(6);
        //인증메일 보내기
        try {
            MailUtil sendMail = new MailUtil(mailSender);
            sendMail.setSubject("Doit 비밀번호 변경 이메일 인증");
            sendMail.setText(new StringBuffer()
                    .append("<h1> Doit 비밀번호 변경 이메일 인증 </h1>")
                    .append("<h3> 함께 만들어가는 목표 관리 커뮤니티 <strong>Doit</strong> 입니다. </h3>")
                    .append("<h4> 아래 링크를 클릭하시면 비밀번호 변경페이지로 이동됩니다. </h4>")
                    .append("<a href='http://i4c108.p.ssafy.io:80/user/pwchange?email=")
                    .append(email)
                    .append("&authKey=")
                    .append(authKey)
                    .append("' target='_blenk'> 비밀번호 변경 이메일 인증 확인</a>")
                    .toString());
            sendMail.setFrom("buhee1029@gmail.com", "Doit 관리자");
            sendMail.setTo(email);
            sendMail.send();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return authKey;
    }
}

