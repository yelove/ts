<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div id="page-wrapper">
		<div id="addorder" style="display: none;" class="row">
			<div style="width: 100%;" class="col-md-6 col-sm-6 col-xs-12">
				<div class="panel panel-info">
					<div class="panel-heading">填写任务信息</div>
					<div class="panel-body">
						<form id="orderform" role="form" action="/bidding/addmsg"
							method="post">
							<div class="form-group col-md-6">
								<label>内容:</label> <input id="mgs" name="msg"
									class="form-control" type="text">
							</div>
							<div class="form-group col-md-6">
								<label>备 注:</label> <input id="msgdesc" name="msgdesc"
									class="form-control" type="text">
							</div>
							<input type="submit"
								style="float: right; background-color: #A600FF;"
								class="btn btn-info">提交</input>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div style="width: 100%;" class="col-md-6 col-sm-6 col-xs-12">
				<div class="panel panel-info">
					<a id="addorderbtn" href="javascript:showHideAddform();"
						class="btn btn-info btn-block">创建任务</a>
					<div class="panel-body">
						<div class="table-responsive">
							<table class="table table-striped table-bordered table-hover">
								<thead>
									<tr>
										<th>编号</th>
										<th>内容</th>
										<th>类型</th>
										<th>创建时间</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody id="orderrs">
									<c:if test="${mislist !=null}">
										<c:forEach items="${mislist}" var="mglist">
											<tr>
												<td>${mglist.id}</td>
												<td>${mglist.mission}</td>
												<td>${mglist.mtype}</td>
												<td>${mglist.createtime}</td>
												<td>
													<button class="btn btn-inverse"
														onclick="location='/bidding/update/${mglist.id}'">
														<i class="glyphicon glyphicon-plus"></i>下架
													</button>
												</td>
											</tr>
										</c:forEach>
									</c:if>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
</div>
<jsp:include page="foot.jsp" />
<script type="text/javascript">
	$('#mainmenu').addClass("active-menu");
	var showflag = false;
	function showHideAddform() {
		if (showflag) {
			showflag = false;
			$("#addorder").hide();
			$("#addorderbtn").html('创建任务');
		} else {
			showflag = true;
			$("#addorder").show();
			$("#addorderbtn").html('隐藏');
		}
	}
	function dm_notification(text, color, time, icon) {
		var icon_span = "", html_element, time = time, $cont = $('#dm-notif');
		if (time) {
			time = time;
		} else {
			time = 2000;
		}
		if (icon) {
			icon_span = "<span class='" + icon + "'></span> ";
		}
		html_element = $(
				'<div class="dm-notification dm-notify-' + color + '">'
						+ icon_span + text + '</div>').fadeIn('fast');
		$cont.append(html_element);
		html_element.on('click', function() {
			dm_notification_close($(this));
		});
		setTimeout(function() {
			dm_notification_close($cont.children('.dm-notification').first());
		}, time);
	}
	function dm_notification_close(elem) {
		if (typeof elem !== "undefined") {
			elem.fadeOut('fast', function() {
				$(this).remove();
			});
		} else {
			$('.alert').fadeOut('fast', function() {
				$(this).remove();
			});
		}
	}
</script>
</body>
</html>