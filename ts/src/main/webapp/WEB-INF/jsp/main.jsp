<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<jsp:include page="sidebar.jsp" />

<div id="page-wrapper">
		<div id="addorder" style="display: none;" class="row">
			<div style="width: 100%;" class="col-md-6 col-sm-6 col-xs-12">
				<div class="panel panel-info">
					<div class="panel-heading">填写话题信息</div>
					<div class="panel-body">
						<form id="orderform" role="form" action="/bidding/addmsg"
							method="post">
							<div class="form-group col-md-10">
								<label>内容:</label> <input id="mgs" name="msg"
									class="form-control" type="text">
							</div>
							<div class="form-group col-md-2">
								<label>类型:</label> <select id="mtype" name="mtype"
									class="form-control" >
									<option value ="daily">日常话题</option>
									<option value ="week">周常话题</option>
									<option value ="holiday">节日话题</option>
									</select>
							</div>
							<div class="form-group col-md-6">
								<label>开始时间:</label> <input id="sdate1" name="sdate1"
									placeholder="当天零点" class="form-control datapcc" type="text">
							</div>
							<div class="form-group col-md-6">
								<label>结束时间:</label> <input id="edate1" name="edate1"
									placeholder="当天24点" class="form-control datapcc" type="text">
							</div>
							<a href="javascript:subOrder();" style="float: right;background-color: #A600FF;"
								class="btn btn-info">保存话题</a>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div style="width: 100%;" class="col-md-6 col-sm-6 col-xs-12">
				<div class="panel panel-info">
					<a id="addorderbtn" href="javascript:showHideAddform();"
						class="btn btn-info btn-block">创建话题</a>
					<div class="panel-footer" style="height:120px;">
						<div class="input-group">
							<input id="queryparam" type="text" class="form-control"
								placeholder="请输入话题内容"> <span
								class="input-group-btn">
								<button onclick="queryOrder()" class="btn btn-success"
									type="button">查询话题</button>
							</span>

						</div>
						<div class="form-group col-md-6">
								<label>开始时间:</label> <input id="sdate2" name="sdate2"
									placeholder="当天零点" class="form-control datapcc" type="text">
							</div>
							<div class="form-group col-md-6">
								<label>结束时间:</label> <input id="edate2" name="edate2"
									placeholder="当天24点" class="form-control datapcc" type="text">
							</div>
					</div>
					<div class="panel-body">
						<div class="table-responsive">
							<table class="table table-striped table-bordered table-hover">
								<thead>
									<tr>
										<th>编号</th>
										<th>话题内容</th>
										<th>类型</th>
										<th>话题时间</th>
										<th>话题结束时间</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody id="orderrs">
								</tbody>
							</table>
							<p id="noval" style="display:none">没有数据！</p>
						</div>
						<ul class="pagination"></ul>
					</div>
				</div>
			</div>
		</div>
</div>
<jsp:include page="foot.jsp" />
<script src="/ts/assets/js/bootstrap-datetimepicker.min.js"></script>
<script src="/ts/assets/js/jBootstrapPage.js"></script>
<script type="text/javascript">
	$('#mainmenu').addClass("active-menu");
	var showflag = false;
	function showHideAddform() {
		if (showflag) {
			showflag = false;
			$("#addorder").hide();
			$("#addorderbtn").html('创建话题');
		} else {
			showflag = true;
			$("#addorder").show();
			$("#addorderbtn").html('隐藏');
		}
	}
	$('.datapcc').datetimepicker({
        language:  'fr',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 2,
		format: 'yyyy-mm-dd',
		forceParse: 0
    });
	function createRsOd(rsod){
		if(rsod&&rsod.length>0){
			$("#noval").hide();
			var html = "";
			var xid = 1;
			for(var i=0;i<rsod.length;i++ ){
				html +="<tr><td>"+xid+"</td><td>"+rsod[i].mission+
				"</td><td>"+coventType(rsod[i].mtype)+"</td><td>"+getLcTime(rsod[i].startdate)+"</td><td>"+getLcTime(rsod[i].enddate)+
				"</td><td><button class='btn btn-inverse' onclick='javascript:;'>"+
				"<i class='glyphicon glyphicon-plus'></i>下架</button></td><tr>";
				xid++;
			}
			$("#orderrs").html(html);
		}else{
			$("#orderrs").html('');
			$("#noval").show();
			return;
		}
	}
	var ctpg = 1;
	function queryOrder() {
			$.ajax({
				url : "/ts/mission/query",
				type : "POST",
				data : {
					'qstr' : $("#queryparam").val(),
					'stime' : $("#sdate2").val(),
					'etime' : $("#edate2").val(),
					'ctpg':ctpg
				},
				success : function(rs) {
					var data = $.parseJSON(rs);
					if (data.status == 1000) {
						createRsOd(data.orderlist);
						if(ctpg==1){
							$(".pagination").jBootstrapPage({
					            pageSize : 20,
					            total : data.maxrow,
					            selectedIndex : data.ctpg,
					            maxPageButton:6,
					            onPageClicked: function(obj, pageIndex) {
					            	queryOrder(pageIndex+1);
					            }
					        });
						}
					} else {
						alert("查询异常!");
					}
				},
				error : function(error) {
					alert("服务异常!");
				}
			});
	}
	
	function coventType(mtype){
		if(mtype=='daily'){
			return '日常话题';
		}else if(mtype=='week'){
			return '周常话题';
		}else if(mtype=='holiday'){
			return '节日话题';
		}else{
			return '特殊话题';
		}
	}
	function coventdate(date){
		return new Date(date).toLocaleString().replace(/:\d{1,2}$/,' ');  
	}

	function subOrder() {
		var mgs = $("#mgs").val();
		var sdate1 = $("#sdate1").val();
		if (mgs == '' || sdate1 == '') {
			alert("请输入完整的话题信息");
		} else {
			$.ajax({
				url : "/ts/mission/add",
				type : "POST",
				data :{'mgs':mgs,'stime':sdate1,'etime':$("#edate1").val(),'mtype':$("#mtype").val()},
				success : function(rs) {
					var data = $.parseJSON(rs);
					if (data.status == 1000) {
						showflag = false;
						document.getElementById("orderform").reset();
						$("#addorderbtn").html('创建话题');
						$("#addorder").hide();
						queryOrder();
						alert("添加话题成功");
					} else if (data.status == 1009) {
						alert("登录超时");
					} else if (data.status == 1010) {
						alert("数据库异常");
					} else {
						alert("未知异常!");
					}
				},
				error : function(error) {
					alert("服务异常!");
				}
			});
		}
	}
</script>
</body>
</html>