/**
 * 
 */
package com.ts.main.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * @author zsy
 *
 */
public class AliMailUtil {
	
	private static Logger logger = LoggerFactory.getLogger(AliMailUtil.class);
	
	public static final String accessKeyId = "LTAIwv5zQ7FpNT6g";
	public static final String accessKeySecret = "2ba355WUsEK9v1Dy6THOOBRMUUWZpi";
	private static IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
			accessKeySecret);
	
	/**
	 * 阿里云邮件发送接口 
	 * @param emailAddress 收件人地址 多个则用逗号分隔 最多传100个
	 * @param subject 邮件主题
	 * @param mailText 邮件内容
	 */
	public static void sample(String emailAddress,String subject,String mailText) {
		IAcsClient client = new DefaultAcsClient(profile);
		SingleSendMailRequest request = new SingleSendMailRequest();
		try {
			request.setAccountName("master@sumail.bz521.com");
			request.setFromAlias("辈子网");
			request.setAddressType(1);
			request.setTagName("active");
			request.setReplyToAddress(false);
			request.setToAddress(emailAddress);
			request.setSubject(subject);
			request.setHtmlBody(mailText);
			SingleSendMailResponse httpResponse = client.getAcsResponse(request);
			logger.info("send email to {} get response EnvId {}",emailAddress,httpResponse.getEnvId());
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (com.aliyuncs.exceptions.ClientException e) {
			e.printStackTrace();
		}
	}
}
