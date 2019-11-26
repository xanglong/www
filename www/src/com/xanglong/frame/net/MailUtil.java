package com.xanglong.frame.net;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Const;
import com.xanglong.frame.config.Mail;
import com.xanglong.frame.log.Logger;
import com.xanglong.frame.util.DateUtil;
import com.xanglong.frame.util.StringUtil;

/**邮件工具类*/
public class MailUtil {
	
	private static Queue<Email> mailQueue = new LinkedList<Email>();

	private static Mail mail = null;

    /**
     * 获取邮件Session对象
     * @return session 邮件会话对象
     * */
    private static Session getSession() {
    	 Properties props = new Properties();
    	 if ("465".equals(mail.getPort())) {
    		 Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
             props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
             props.setProperty("mail.smtp.socketFactory.fallback", "false");
             props.setProperty("mail.smtp.socketFactory.port", mail.getPort());
    	 }
         props.setProperty("mail.transport.protocol", "smtp");
         props.setProperty("mail.smtp.host", mail.getHost());
         props.setProperty("mail.smtp.auth", "true");
         props.setProperty("mail.smtp.port", mail.getPort());
         return Session.getInstance(props);
    }
    
    /**
     * 获取消息对象
     * @param session 邮件会话对象
     * @param email 邮件对象
     * @return 邮件消息对象
     * */
    private static MimeMessage getMimeMessage(Session session, Email email) {
    	MimeMessage message = new MimeMessage(session);
    	try {
			message.setFrom(new InternetAddress(mail.getAccount(), email.getPersonal(), Const.CHARSET_STR));
			message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email.getReceive(), Const.CHARSET_STR));
	        message.setSubject(email.getSubject(), Const.CHARSET_STR);
	        message.setContent(email.getContent(), ContentType.HTML.getCode() + ";charset=" + Const.CHARSET_STR);
	        message.setSentDate(new Date());
	        message.saveChanges();
		} catch (UnsupportedEncodingException | MessagingException e) {
			Logger.error(e);
		}
    	return message;
    }
    
    /**
     * 异步发送邮件
     * @param 邮件对象
     * */
    private static void asyncSend(Email email) {
    	Session session = getSession();
        session.setDebug(Sys.getConfig().getIsDebug());
        MimeMessage message = getMimeMessage(session, email);
		try {
			Transport transport = session.getTransport();
			transport.connect(mail.getAccount(), mail.getPassword());
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
		} catch (MessagingException e) {
			//你是不是账号密码没配置？
			Logger.error(e);
		}
    }
    
    /**
     * 发送邮件
     * @param 邮件对象
     * */
    public static void send(Email email) {
    	if (email == null || StringUtil.isBlank(email.getReceive())) {
    		return;
    	}
    	if (mail == null) {
    		mail = Sys.getConfig().getMail();
    	}
    	if (mailQueue.size() == 0) {
    		//如果队列为空,则先添加到队列
    		mailQueue.offer(email);
    		new Thread() {
                public void run() {
                	//循环发送邮件队列，发送时取出对象
            		while (mailQueue.size() > 0) {
            			//获取一个对象，但不从队列删除
            			Email email = mailQueue.peek();
            			//发送邮件，这个需要几秒时间
            			asyncSend(email);
            			//发送完移除对象，防止开启多个任务
            			mailQueue.poll();
            		}
                }
            }.start();
    	} else {
    		//如果队列不为空，则只需要添加到队列即可
    		mailQueue.add(email);
    	}
    }
    
    /**根据异常原因发送异常邮件*/
    public static void send(Throwable throwable) {
    	Mail mail = Sys.getConfig().getMail();
		String dateTime = DateUtil.getDateTime();
		try (StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
		) {
			throwable.printStackTrace(printWriter);
			String errorText = dateTime + "\t" + stringWriter.toString();
			send(new Email(mail.getPersonal(), mail.getErrorMailReceive(), mail.getErrorMailSubject(), errorText));
		} catch(Exception e) {
			Logger.error(e);
		}
    }

}