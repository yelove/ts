<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- 左侧导航栏部分 -->
<nav class="navbar-default navbar-side" role="navigation">
	<div class="sidebar-collapse">
		<ul class="nav" id="main-menu">
			<li>
				<div class="user-img-div">
					<!-- <img src="assets/img/user.png" class="img-thumbnail" /> -->
					<div class="inner-text">
						${username} <br /> <small> 最后一次登录 : ${lastlogintime}</small>
					</div>
				</div>
			</li>
			<li><a id="mainmenu" href="main"> <i
					class="fa fa-dashboard "></i>任务维护
			</a></li>
			<li id="jdkfli"><a href="#"><i class="fa fa-desktop "></i>业务管理
					<span class="fa arrow"></span></a>
				<ul class="nav nav-second-level">
					<li><a id="suborderli" href="suborder"><i
							class="fa fa-coffee"></i>敏感词审核</a></li>
				</ul></li>

		</ul>
	</div>
</nav>