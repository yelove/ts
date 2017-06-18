<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>密码找回-bz521.com</title>
<link rel="stylesheet" href="/ts/assets/libs/uikit/css/uikit.min.css">
<link media="all" type="text/css" rel="stylesheet"
	href="/ts/assets/libs/uikit/css/components/notify.min.css">
<link rel="stylesheet" href="/ts/assets/css/style.css">
</head>
<body>
<div id="scroll"></div>
	<div class="background-cover-img img-login"></div>
	<div class="background-cover"></div>
	<div class="login-wrapper">
		<a href="/ts" class="login-logo"><i class="iconfont icon-minilogo"></i>
			密码重置</a>
		<form method="POST" class="uk-form uc-form"
			id="register-form">
			<c:if test="${code ==1000}">
			<div class="uk-form-row">
				<div class="uk-form-controls">
					<input type="password" placeholder="密码" name="passwd"
						id="password" required="" data-parsley-required-message="密码不能为空"
						minlength="6" data-parsley-minlength-message="密码不能小于6位"
						autocomplete="off">
				</div>
			</div>
			<div class="uk-form-row">
				<div class="uk-form-controls">
					<input type="password" placeholder="重复密码"
						name="password_confirmation" required=""
						data-parsley-required-message="确认密码不能为空"
						data-parsley-equalto="#password"
						data-parsley-equalto-message="两次密码不一致" autocomplete="off">
				</div>
			</div>
			<div class="uk-form-row">
				<input type="hidden" id="_token"
					value="${desc}">
				<button type="submit" class="uk-button button-signup">重置密码</button>
			</div>
			</c:if>
			<c:if test="${code ==1002}">
				<p><h3>参数不正确</h3></p>
			</c:if>
			<c:if test="${code ==1004}">
				<p><h3>参数无效</h3></p>
			</c:if>
			<c:if test="${code ==1006}">
				<p><h3>该链接已失效</h3></p>
			</c:if>
		</form>
	</div>
	<script src="/ts/assets/js/jquery-2.1.4.min.js"></script>
	<script src="/ts/all/all.js"></script>
	<script
		src="/ts/vendor/Parsley/dist/parsley.min.js"></script>
<script type="text/javascript">
	$(function() {
		$("#register-form").parsley({
			successClass : "",
			errorClass : "",
			errorsWrapper : "<p class='uk-form-help-block'></p>",
			errorTemplate : "<span></span>"
		});
		$("#register-form").on(
				'submit',
				function() {
					$.ajax('/ts/user/changepw/'+$("#_token").val(), {
						type : "POST",
						dataType : "JSON",
						data : $('#register-form').serialize(),
						success : function(res) {
							if (res.status == 1000) {
								UIkit.notify('密码重置成功,即将跳转首页',{status : "success"});
								setTimeout(function(){
									location.href = '/';
				                 },2500);
							}else if(res.status == 1009){
								UIkit.notify('用户不存在',{status : "danger"});
							}else if(res.status == 1006){
								UIkit.notify('该链接已过期',{status : "danger"});
							}else if(res.status == 1004){
								UIkit.notify('参数无效',{status : "warning"});
							}else if(res.status == 1002){
								UIkit.notify('参数错误哟',{status : "danger"});
							}else {
								UIkit.notify('未知错误,可能是服务器异常哟',{status : "danger"});
							}
						}
					});
					return false;
				});
	});
</script>
</body>
</html>
