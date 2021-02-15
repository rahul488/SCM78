package com.finalProject.com.SmartContactManger.Service;


import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	public boolean sendEmail(String subject, String message, String to) {
		boolean f=false;
		try {
				MimeMessagePreparator mimeMessages=new MimeMessagePreparator() {
					
					@Override
					public void prepare(MimeMessage mimeMessage) throws Exception {
						mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
						mimeMessage.setFrom(new InternetAddress("myself.rahul78@gmail.com"));
						mimeMessage.setSubject(subject);
						mimeMessage.setContent(message, "text/html");
					}
				};
		
		mailSender.send(mimeMessages);
		System.out.println("SUCCESS...............");
		f=true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	
		return f;
	}

	
}

