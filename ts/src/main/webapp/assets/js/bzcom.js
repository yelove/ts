function userLoginTip(){
	$("#userlogin").html(" <a href='/user.html'> <i class='iconfont icon-user'></i><span class='i uk-icon-angle-down'></span></a>"+
			"<div class='lomenu uk-dropdown uk-dropdown-flip dropdown-arrow-up uk-dropdown-bottom' style='top: 48px; left: 113px;background: #273035;'><ul class='uk-nav uk-nav-dropdown'>"+
			"<li><a href='/ts/user.html'><i class='iconfont icon-user'></i> 我的主页</a></li><li><a href='#'><i class='iconfont icon-mail'></i> 我的消息</a></li><li class='uk-nav-divider'></li>"+
			"<li><a href='/ts/user/logout'><i class='iconfont icon-iconexit'></i> 退出</a></li></ul></div>");
};
function ajaxRead(url, id) {
	$.ajax({
				dataType : 'json',
				url : url,
				data : {
					'start' : startnum
				},
				beforeSend : function() {
					loading = layer.load(2, {
						shade : 0
					});
				},
				success : function(res) {
					layer.close(loading);
					if (res.status == 1000) {
						if (res.bookhot != null
								&& res.bookhot.length > 0) {
							var bookhot = eval(res.bookhot);
							var booklis = "";
							$.each(
									bookhot,
									function(n, value) {
										booklis += "<li><article class='uk-comment uk-comment-primary'><header class='uk-comment-header'>"
												+ "<img class='uk-comment-avatar' style='border-radius:0%;' src='http://zsytp.oss-cn-shanghai.aliyuncs.com/"
												+ value.userImgUrl
												+ "' width='50' height='50'>"
												+ "<a href='/view.html?uid="
												+ value.userid
												+ "'><h4 class='uk-comment-title'>"
												+ value.tsno
												+ "</h4></a><div class='uk-comment-meta'>"
												+ value.creatdate
												+ " | "
												+ value.interval
												+ " | #</div></header><div class='uk-comment-body'><p>"
												+ value.text
												+ "</p><div><div class='uk-grid'><div class='uk-width-8-10'>&nbsp;</div><a href='javascript:zanbook("
												+ value.id
												+ ");' val='90' class='like uk-width-1-10'><i class='uk-icon-thumbs-o-up' style='margin-top: 8px;'></i> (<span id='bkzan"+value.id+"' class='zan'>"
												+ value.praisenum
												+ "</span>)</a> <button onclick='showComment("
												+ value.id
												+ ")' class='uk-button uk-button-link uk-width-1-10' style='float:right;position:relative;color:#d8a868;'>"
												if(value.commentnum>0){
													booklis +=value.commentnum+"条";
												}
												booklis += "评论</button></div><div id='showcmt"+value.id+"' class='plk_div div_d1'></div></div>"
												var nearlistsizt = value.nearlist.length;
												if(value.nearlist&&null!=value.nearlist[0]){
													booklis += "<hr class='hr_1'/></div>";
												}else{
													booklis += "</div>";
												}
													$.each(value.nearlist,
																	function(n,
																			value) {
																		if (null != value) {
																			booklis += "<div class='uk-comment-body fbt_d1'> "
																					+ value.creatdate
																					+ "| "
																					+ value.interval
																					+ " |  #</div><div class='uk-comment-body'><p>"
																					+ value.text
																					+ "</p><div><div class='uk-grid'><div class='uk-width-8-10'>&nbsp;</div><a href='javascript:zanbook("
																					+ value.id
																					+ ");' val='90' class='like uk-width-1-10'><i class='uk-icon-thumbs-o-up' style='margin-top: 8px;'></i> (<span id='bkzan"+value.id+"' class='zan'>"
																					+ value.praisenum
																					+ "</span>)</a> <button onclick='showComment("
																					+ value.id
																					+ ")' class='uk-button uk-button-link uk-width-1-10' style='float:right;position:relative;color:#d8a868;'>"
																					if(value.commentnum>0){
																						booklis +=value.commentnum+"条";
																					}
																					booklis += "评论</button></div><div id='showcmt"+value.id+"' class='plk_div div_d1'></div>";
																			if (n<nearlistsizt-1) {
																				booklis += "<hr class='hr_1'/></div></div>";
																			} else {
																				booklis += "</div></div>";
																			}
																		}
																	});
													booklis += "</article></li><hr class='hr_2'/>";
												});
							$('#' + id).append(booklis);
							startnum = startnum + res.bookhot.length;
						} else {
							end = 1;
							$('#' + id)
									.append(
											'<p class="uk-text-center" style="color:white;">没有更多了<p>');
						}
					}
				}
			});
};
function getHot(url) {
	$.ajax({dataType : 'json',
				url : url,
				data : {
					'start' : startnum
				},
				success : function(res) {
					if (res.status == 1000) {
						var bookhot = eval(res.bookhot);
						var booklis = "";
						$.each(
							bookhot,
							function(n, value) {
								booklis += "<li><article class='uk-comment uk-comment-primary'><header class='uk-comment-header'>"
										+ "<img class='uk-comment-avatar' style='border-radius:0%;' src='http://zsypt.oss-cn-shanghai.aliyuncs.com/"
										+ value.userImgUrl
										+ "' width='50' height='50'>"
										+ "<a href='/view.html?uid="
										+ value.userid
										+ "'><h4 class='uk-comment-title'>"
										+ value.tsno
										+ "</h4></a><div class='uk-comment-meta'>"
										+ value.creatdate
										+ " | "
										+ value.interval
										+ " | #</div></header><div class='uk-comment-body'><p>"
										+ value.text
										+ "</p><div><div class='uk-grid'><div class='uk-width-8-10'>&nbsp;</div><a href='javascript:zanbook("
										+ value.id
										+ ");' val='90' class='like uk-width-1-10'><i class='uk-icon-thumbs-o-up' style='margin-top: 8px;'></i> (<span id='bkzan"+value.id+"' class='zan'>"
										+ value.praisenum
										+ "</span>)</a> <button onclick='showComment("
										+ value.id
										+ ")' class='uk-button uk-button-link uk-width-1-10' style='float:right;position:relative;color:#d8a868;'>"
										if(value.commentnum>0){
											booklis +=value.commentnum+"条";
										}
										booklis += "评论</button></div><div id='showcmt"+value.id+"' class='plk_div div_d1'></div></div>"
										var nearlistsizt = value.nearlist.length;
										if(value.nearlist&&null!=value.nearlist[0]){
											booklis += "<hr class='hr_1'/></div>";
										}else{
											booklis += "</div>";
										}
											$.each(value.nearlist,
															function(n,
																	value) {
																if (null != value) {
																	booklis += "<div class='uk-comment-body fbt_d1'> "
																			+ value.creatdate
																			+ "| "
																			+ value.interval
																			+ " |  #</div><div class='uk-comment-body'><p>"
																			+ value.text
																			+ "</p><div><div class='uk-grid'><div class='uk-width-8-10'>&nbsp;</div><a href='javascript:zanbook("
																			+ value.id
																			+ ");' val='90' class='like uk-width-1-10'><i class='uk-icon-thumbs-o-up' style='margin-top: 8px;'></i> (<span id='bkzan"+value.id+"' class='zan'>"
																			+ value.praisenum
																			+ "</span>)</a> <button onclick='showComment("
																			+ value.id
																			+ ")' class='uk-button uk-button-link uk-width-1-10' style='float:right;position:relative;color:#d8a868;'>"
																			if(value.commentnum>0){
																				booklis +=value.commentnum+"条";
																			}
																			booklis += "评论</button></div><div id='showcmt"+value.id+"' class='plk_div div_d1'></div>";
																	if (n<nearlistsizt-1) {
																		booklis += "<hr class='hr_1'/></div></div>";
																	} else {
																		booklis += "</div></div>";
																	}
																}
															});
											booklis += "</article></li><hr class='hr_2'/>";
										});
						startnum = startnum + res.bookhot.length;
						$('#mine').html(booklis);
						if (res.bookhot.length < 10) {
							$('#mine')
									.append(
											'<p class="uk-text-center" style="color:white;">没有更多了<p>');
						}
					}
				}
			});
};
function submitComment(boid) {
	$.ajax({
		dataType : 'json',
		type : 'POST',
		url : '/ts/book/subcomment',
		data : {
			'comment' : $('#bkcmt' + boid).val(),
			'bookid' : boid
		},
		success : function(res) {
			if (res.status == 1000) {
				delete showarry[boid.toString()];
				showComment(boid);
			} else if (res.status == 1004) {
				UIkit.notify('登录超时', {
					status : "danger"
				});
			} else {
				UIkit.notify('评论失败', {
					status : "danger"
				});
			}
		}
	})
};
function zanbook(boid) {
	$.ajax({
		dataType : 'json',
		type : 'POST',
		url : '/ts/book/zanbook',
		data : {
			'bookid' : boid
		},
		success : function(res) {
			if (res.status == 1000) {
				var xz = Number($('#bkzan' + boid).html());
				if (res.code == 1) {
					xz++;
				} else {
					xz--;
				}
				$('#bkzan' + boid).html(xz);
			} else if (res.status == 1004) {
				UIkit.notify('登录超时', {
					status : "danger"
				});
			} else {
				UIkit.notify('服务器异常', {
					status : "danger"
				});
			}
		}
	})
};
function zancomment(cmtid) {
	$.ajax({
		dataType : 'json',
		type : 'POST',
		url : '/ts/book/zancomment',
		data : {
			'cmtid' : cmtid
		},
		success : function(res) {
			if (res.status == 1000) {
				var xz = Number($('#cmtzan' + cmtid).html());
				if (res.code == 1) {
					xz++;
				} else {
					xz--;
				}
				$('#cmtzan' + cmtid).html(xz);
			} else if (res.status == 1004) {
				UIkit.notify('登录超时', {
					status : "danger"
				});
			} else {
				UIkit.notify('服务器异常', {
					status : "danger"
				});
			}
		}
	})
};
var showarry = {};
function showComment(boid) {
	if (showarry[boid.toString()]) {
		$('#showcmt' + boid).slideUp();
		delete showarry[boid.toString()];
	} else {
		$.ajax({
					dataType : 'json',
					type : 'POST',
					url : '/ts/book/getcomment',
					data : {
						'bookid' : boid
					},
					success : function(res) {
						if (res.status == 1000) {
							var commentHtml = '<div><textarea id="bkcmt'+boid+'" class="uk-button uk-width-4-5" style="text-align: left;" placeholder="吐个槽吧..."></textarea><button class="uk-button uk-button-primary uk-width-1-5" style="height:60px" onclick="submitComment('
									+ boid
									+ ')" type="button">发表</button></div>';
							if (null != res.bkv) {
								commentHtml += '<div class="mt_10">';
								if (res.bkv.hotList) {
									commentHtml += '<div><ul>';
									$.each(res.bkv.hotList, function(n,
											value) {
										commentHtml += '<p>'
												+ value.comment
												+ '</p>';
									});
									commentHtml += '</ul></div>';
								}
								if (res.bkv.nomalList) {
									commentHtml += '<ul>';
									$.each(res.bkv.nomalList, function(
											n, value) {
										commentHtml += '<li style="display: flex;"><a href="/view.html?uid='+value.userid+'"><img class="avatar_1" src="http://zsypt.oss-cn-shanghai.aliyuncs.com/'+value.userimg+'"/></a><div style="padding-left:12px;"><div><a style="font-size:18px" href="/view.html?uid='+value.userid+'">'+value.username+'</a></div><div><p class="p_dd">'
												+ value.comment
												+ '</p></div><div><h4 style="float:left;color:white;">'+value.createTimeStr+'</h4><div style="padding-left:20px;float:left;margin-top:-4px"><a onclick="zancomment('+value.id+')"><i class="uk-icon-thumbs-o-up" ></i> (<span id="cmtzan'+value.id+'" class="zan">'+value.zan+'</span>)</a></div></div></div></li>';
									});
									commentHtml += '</ul>';
								}
								commentHtml += '</div>';
							} else {
								commentHtml += '<p class="uk-text-center" style="color:white;"> 暂时还没有评论<p>';
							}
							$('#showcmt' + boid).html(commentHtml);
							$('#showcmt' + boid).slideDown();
							$('#bkcmt' + boid).focus();
							showarry[boid.toString()] = 1;
						} else {
							UIkit.notify('服务器异常', {
								status : "danger"
							});
						}
					}
				})
	}
};