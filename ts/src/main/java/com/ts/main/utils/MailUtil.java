/**
 * 
 */
package com.ts.main.utils;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;

/**
 * @author 张绍云
 *
 */
public class MailUtil {

	public static void main(String[] args) throws MessagingException, GeneralSecurityException {
		Properties props = new Properties();

		// 开启debug调试
		props.setProperty("mail.debug", "true");
		// 发送服务器需要身份验证
		props.setProperty("mail.smtp.auth", "true");
		// 设置邮件服务器主机名
		props.setProperty("mail.host", "smtp.qq.com");
		// 发送邮件协议名称
		props.setProperty("mail.transport.protocol", "smtp");

		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.socketFactory", sf);

		Session session = Session.getInstance(props);

		Message msg = new MimeMessage(session);
		msg.setSubject("seenews 错误");
		StringBuilder builder = new StringBuilder();
		builder.append("url = " + "http://blog.csdn.net/never_cxb/article/details/50524571");
		builder.append("\n页面爬虫错误");
		builder.append("\n时间 " + System.currentTimeMillis());
		msg.setText(builder.toString());
		msg.setFrom(new InternetAddress("357307667@qq.com"));

		Transport transport = session.getTransport();
		// qq邮箱授权码
		transport.connect("smtp.qq.com", "357307667@qq.com", "fzrradjnjehibgbe");

		transport.sendMessage(msg, new Address[] { new InternetAddress("357307667@qq.com") });
		transport.close();
	}

	private static Session qqmailsession = null;

	static {
		Properties props = new Properties();
		props.setProperty("mail.debug", "false");
		// 发送服务器需要身份验证
		props.setProperty("mail.smtp.auth", "true");
		// 设置邮件服务器主机名
		props.setProperty("mail.host", "smtp.qq.com");
		// 发送邮件协议名称
		props.setProperty("mail.transport.protocol", "smtp");
		MailSSLSocketFactory sf = null;
		try {
			sf = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		sf.setTrustAllHosts(true);
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.socketFactory", sf);
		qqmailsession = Session.getInstance(props);
	}

	public void sendQQmail(String title, String msgstr, String fromMailAccount, String[] toMailAccount) {
		try {
			Transport transport = qqmailsession.getTransport();
			Message msg = new MimeMessage(qqmailsession);
			msg.setSubject(title);
			msg.setText(msgstr);
			msg.setFrom(new InternetAddress(fromMailAccount));
			transport.connect("smtp.qq.com", "357307667@qq.com", "fzrradjnjehibgbe");
			Address[] adsarr = new Address[toMailAccount.length];
			int i = 0;
			for (String address : toMailAccount) {
				adsarr[i] = new InternetAddress(address);
				i++;
			}
			transport.sendMessage(msg, adsarr);
			transport.close();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
