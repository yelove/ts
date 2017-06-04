package com.ts.main.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.ts.main.bean.model.User;
import com.ts.main.common.CommonStr;
import com.ts.main.service.UserService;
import com.ts.main.utils.MD5Tools;

@RequestMapping(value = "user/")
@Controller
public class LogInAction {

	@Autowired
	private UserService uService;

	@RequestMapping(value = "login", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody Map<String, Object> login(User user, ModelMap mdmap, HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		rm.put(CommonStr.STATUS, 1002);
		if (StringUtils.isEmpty(user.getName())) {
			rm.put(CommonStr.STATUS, 1009);
			return rm;
		}
		User aluser = uService.getUserByName(user.getName());
		if (null == aluser) {
			rm.put(CommonStr.STATUS, 1009);
			return rm;
		}
		if (!aluser.getPassword().equals(MD5Tools.MD5(user.getPassword()))) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		} else {
			request.getSession(true).setAttribute(CommonStr.USERNAME, aluser.getEmail());
			request.getSession(true).setAttribute(CommonStr.TKUSER, aluser);
			rm.put(CommonStr.STATUS, 1000);
		}
		return rm;
	}

	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> register(User user, ModelMap mdmap, HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		boolean suc = uService.saveUser(user);
		if (suc) {
			request.getSession(true).setAttribute(CommonStr.USERNAME, user.getEmail());
			request.getSession(true).setAttribute(CommonStr.TKUSER, user);
			rm.put(CommonStr.STATUS, 1000);
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	@RequestMapping(value = "checklogin", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> isLogin(ModelMap mdmap, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.USERNAME);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null != obj) {
			rm.put(CommonStr.STATUS, 1000);
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logOut(ModelMap mdmap, HttpServletRequest request) {
		request.getSession(true).removeAttribute(CommonStr.USERNAME);
		request.getSession(true).removeAttribute(CommonStr.TKUSER);
		return "redirect:/";
	}

	private static final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
	private static final String accessKeyId = "LTAI3KYpEO3kEPYC";
	private static final String accessKeySecret = "5OC8joOodACF9cLc9XAmPxORB9t5I3";

	@RequestMapping(value = "uploadavatar", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> uploadavatar(
			@RequestParam(value = "croppedImage", required = false) MultipartFile file, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		User user = (User)obj;
		String finename = user.getId()+"_"+System.currentTimeMillis()+".jpg";
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		InputStream inputStream;
		try {
			inputStream = file.getInputStream();
			ossClient.putObject("zsytp", finename, inputStream);
			
		} catch (IOException e) {
			e.printStackTrace();
			rm.put(CommonStr.STATUS, 1009);
			return rm;
		}
		user = uService.getUserBiIdWithCache(user.getId());
		String oldimg = user.getImgurl();
		user.setImgurl(finename);
		user.setUpdatetime(System.currentTimeMillis());
		uService.updateUser(user);
		
		if(!StringUtils.isEmpty(oldimg)){
			ossClient.deleteObject("zsytp", oldimg);
		}
		ossClient.shutdown();
		rm.put(CommonStr.STATUS, 1000);
		rm.put(CommonStr.DESC, finename);
		return rm;
	}
}
