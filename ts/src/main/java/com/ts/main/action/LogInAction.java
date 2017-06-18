package com.ts.main.action;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ts.main.bean.model.Findpw;
import com.ts.main.bean.model.User;
import com.ts.main.bean.vo.UserVo;
import com.ts.main.common.CommonStr;
import com.ts.main.service.MissionService;
import com.ts.main.service.UserService;
import com.ts.main.utils.AliMailUtil;
import com.ts.main.utils.MD5Tools;

@RequestMapping(value = "user/")
@Controller
public class LogInAction {
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private UserService uService;
	
	@Autowired
	private MissionService misService;

	@RequestMapping(value = "login", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody Map<String, Object> login(User user, HttpServletRequest request) {
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
	
	@RequestMapping(value = "adminlogin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody Map<String, Object> adminlogin(User user, HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		rm.put(CommonStr.STATUS, 1002);
		if (StringUtils.isEmpty(user.getName())) {
			rm.put(CommonStr.STATUS, 1009);
			return rm;
		}
		User aluser = uService.getUserByName(user.getName());
		if (null == aluser||aluser.getDepartmentid()>=0) {
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
	
	@RequestMapping(value = "adminmain", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String adminmain(ModelMap mdmap, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		if(null==obj){
			return "redirect:/signin2adm.html";
		}
		User user = (User)obj;
		mdmap.put("username", user.getEmail());
		mdmap.put("lastlogintime", user.getUpdatetime());
		request.setAttribute("mislist", misService.getAllMission());
		return "main";
	}
	
	@RequestMapping(value = "pwfind", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody Map<String, Object> passwordFind(final User user, HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		rm.put(CommonStr.STATUS, 1002);
		if (StringUtils.isEmpty(user.getName())) {
			return rm;
		}
		User aluser = uService.getUserByName(user.getName());
		if (null == aluser) {
			rm.put(CommonStr.STATUS, 1009);
			return rm;
		}
		final String id = uService.insertFindpw(user.getName(), aluser.getId());
		if(null==id){
			rm.put(CommonStr.STATUS, 1007);
			return rm;
		}
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				AliMailUtil.sample(user.getName(), "密码找回邮件", "点击URL重置您的密码</br><a href='http://www.ba521.com/user/findpw/"+id+"'>http://www.ba521.com/user/findpw/"+id+"</a><br/>");
			}
		});
		rm.put(CommonStr.STATUS, 1000);
		return rm;
	}
	
	@RequestMapping(value = "findpw/{id}", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String resetPw(@PathVariable(value = "id", required = true) String id,ModelMap mdmap) {
		if(StringUtils.isEmpty(id)){
			mdmap.put(CommonStr.CODE, 1002);
		}else{
			Findpw fp = uService.getFpById(id);
			if(null!=fp){
				if(fp.getFtype().intValue()==0){
					mdmap.put(CommonStr.CODE, 1000);
					mdmap.put(CommonStr.DESC, id);
				}else{
					mdmap.put(CommonStr.CODE, 1006);
				}
			}else{
				mdmap.put(CommonStr.CODE, 1004);
			}
		}
		return "findpw";
	}
	
	@RequestMapping(value = "changepw/{id}", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody Map<String, Object>  resetPw(@PathVariable(value = "id", required = true) String id,@RequestParam(value = "passwd", required = true, defaultValue = "") String passwd) {
		Map<String, Object> rm = new HashMap<String, Object>();
		if(StringUtils.isEmpty(id)||StringUtils.isEmpty(passwd)){
			rm.put(CommonStr.STATUS, 1002);
		}else{
			Findpw fp = uService.getFpById(id);
			if(null!=fp){
				if(fp.getFtype().intValue()==0){
					User user = uService.getUserBiIdWithCache(fp.getUid());
					if(null!=user){
						user.setPassword(MD5Tools.MD5(passwd));
						uService.updateUser(user);
						rm.put(CommonStr.STATUS, 1000);
					}else{
						rm.put(CommonStr.STATUS, 1009);
					}
				}else{
					rm.put(CommonStr.STATUS, 1006);
				}
			}else{
				rm.put(CommonStr.STATUS, 1004);
			}
		}
		return rm;
	}

	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> register(final User user, HttpServletRequest request) {
		Map<String, Object> rm = new HashMap<String, Object>();
		boolean suc = uService.saveUser(user);
		if (suc) {
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					AliMailUtil.sample(user.getEmail(), "注册成功!", "<a href='http://www.bz521.com'>欢迎来到辈子网!</a><br/>我们是一家以分享个人经历作为交流方式的私密社交网站.在这里可以看到形形色色不同的人生经历,也可以记录自己的点点滴滴.<br/>");
				}
			});
			request.getSession(true).setAttribute(CommonStr.USERNAME, user.getEmail());
			request.getSession(true).setAttribute(CommonStr.TKUSER, user);
			rm.put(CommonStr.STATUS, 1000);
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	@RequestMapping(value = "checklogin", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> isLogin( HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null != obj) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put(CommonStr.USER, convertUser(uService.getUserBiIdWithCache(((User) obj).getId())));
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	private static Cache<String, Object> userupadtelock = CacheBuilder.newBuilder().softValues()
			.expireAfterWrite(2, TimeUnit.SECONDS).initialCapacity(16).build();

	@RequestMapping(value = "saveuserifo", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveUserInfo(ModelMap mdmap, UserVo user, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null != obj) {
			User ouser = (User) obj;
			if (null == userupadtelock.getIfPresent(String.valueOf(ouser.getId()))) {

				User bser = uService.getUserBiIdWithCache(ouser.getId());
				if (!StringUtils.isEmpty(user.getByear())) {
					String birthday = user.getByear() + "-"
							+ (StringUtils.isEmpty(user.getBmonth()) ? "1" : user.getBmonth()) + "-"
							+ (StringUtils.isEmpty(user.getBday()) ? "1" : user.getBday());
					bser.setBirthday(birthday);
				}
				if (!StringUtils.isEmpty(user.getName())) {
					bser.setName(user.getName());
				}
				if (!StringUtils.isEmpty(user.getSignature())) {
					bser.setSignature(user.getSignature());
				}
				if (null != user.getSex()) {
					bser.setSex(user.getSex());
				}
				if (!StringUtils.isEmpty(user.getProvince())) {
					bser.setProvince(user.getProvince());
				}
				if (!StringUtils.isEmpty(user.getCity())) {
					bser.setCity(user.getCity());
				}
				if (!StringUtils.isEmpty(user.getCountry())) {
					bser.setCountry(user.getCountry());
				}
				uService.updateUser(bser);
				request.getSession(true).setAttribute(CommonStr.TKUSER, bser);
			}
			rm.put(CommonStr.STATUS, 1000);
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	// public static void main(String args[]) {
	// User u = new User();
	// u.setId(21l);
	// u.setName("xxx");
	// UserVo uv = (UserVo) u;
	// System.out.println(uv.getName());
	// uv.setBday("123");
	// System.out.println(uv.getBday());
	// }

	@RequestMapping(value = "getuser", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getUser(ModelMap mdmap, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null != obj) {
			rm.put(CommonStr.STATUS, 1000);
			rm.put(CommonStr.USER, convertUser(uService.getUserBiIdWithCache(((User) obj).getId())));
		} else {
			rm.put(CommonStr.STATUS, 1009);
		}
		return rm;
	}

	private UserVo convertUser(User user) {
		UserVo uv = new UserVo();
		try {
			BeanUtils.copyProperties(uv, user);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if (!StringUtils.isEmpty(user.getBirthday())) {
			String[] bd = user.getBirthday().split("-");
			uv.setBday(bd[2]);
			uv.setBmonth(bd[1]);
			uv.setByear(bd[0]);
		}
		return uv;
	}

	@RequestMapping(value = "checkUserEmail", method = RequestMethod.GET)
	public void checkUserEmail(@RequestParam(value = "email", required = true) String email, HttpServletResponse resp) {
		User user = uService.getUserByName(email);
		if (null != user) {
			resp.setStatus(200);
		} else {
			resp.setStatus(404);
		}
	}

	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logOut(ModelMap mdmap, HttpServletRequest request) {
		request.getSession(true).removeAttribute(CommonStr.USERNAME);
		request.getSession(true).removeAttribute(CommonStr.TKUSER);
		return "redirect:/";
	}

	private static final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";

	@RequestMapping(value = "uploadavatar", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> uploadavatar(
			@RequestParam(value = "croppedImage", required = false) MultipartFile file, HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(CommonStr.TKUSER);
		Map<String, Object> rm = new HashMap<String, Object>();
		if (null == obj) {
			rm.put(CommonStr.STATUS, 1004);
			return rm;
		}
		User user = (User) obj;
		String finename = user.getId() + "_" + System.currentTimeMillis() + ".jpg";
		OSSClient ossClient = new OSSClient(endpoint, AliMailUtil.accessKeyId, AliMailUtil.accessKeySecret);
		InputStream inputStream;
		try {
			inputStream = file.getInputStream();
			ossClient.putObject("zsypt", finename, inputStream);
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
		if (!StringUtils.isEmpty(oldimg)) {
			ossClient.deleteObject("zsypt", oldimg);
		}
		ossClient.shutdown();
		rm.put(CommonStr.STATUS, 1000);
		rm.put(CommonStr.DESC, finename);
		return rm;
	}
}
