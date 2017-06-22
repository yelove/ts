<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- js 引用  --%>
<div id="footer-sec">Copyright &copy; 2017.台卡网络科技有限公司 All rights
	reserved.</div>
<div id="dm-notif"></div>
<script src="/ts/assets/js/jquery-2.1.4.min.js"></script>
<script src="/assets/js/bootstrap.js"></script>
<script
	src="/assets/js/jquery.metisMenu.js"></script>
<script src="/assets/js/custom.js"></script>
<script type="text/javascript">
function dm_notification(text, color, time, icon) {
    var icon_span = "",
        html_element,
        time = time,
        $cont = $('#dm-notif');
	if(time){
		time = time;	
	}else{
		time = 5000;	
	}
    if (icon) {
        icon_span = "<span class='" + icon + "'></span> ";
    }
    // Create the HTML element
    html_element = $('<div class="dm-notification dm-notify-' + color + '">' + icon_span + text + '</div>').fadeIn('fast');
    // Append the label to the cont
    $cont.append(html_element);
    // Remove the notification on click
    html_element.on('click', function() {
        dm_notification_close($(this));
    });
    // After time seconds, the notification fades out
    setTimeout(function() {
        dm_notification_close($cont.children('.dm-notification').first());
    }, time);
}

function dm_notification_close(elem) {
    // if you don't pass an argument the function removes all alerts
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
function longPolling() {
	$.ajax({
		url : "<%=request.getContextPath()%>/ajax",
			data : {
				"timed" : new Date().getTime()
			},
			dataType : "text",
			type : "GET",
			timeout : 15000,
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log(XMLHttpRequest.status, textStatus);
				if(textStatus == 'timeout'){
					longPolling();
				}else if(XMLHttpRequest.status==0){
					setTimeout(function() {
						longPolling();
					}, 10000);
				}else{
					setTimeout(function() {
						longPolling();
					}, 12000);
				}
			},
			success : function(data, textStatus) {
				if (textStatus == "success") { // 请求成功
					var rs = $.parseJSON(data);
					if(rs.status==1000){
						dm_notification('<a href="javascript:grabeOrder(0)">有新的订单哦.</a>', 'green', 2000)
						if($('#bigt')){
							$('#bigt').html(rs.big);
							$('#litt').html(rs.lit);
						}
					}
				}
				setTimeout(function() {
					longPolling();
				}, 1000);
			}
		});
	}
	function grabeOrder(odid){
		if(!odid){
			odid = 0;
		}
		$.ajax({
			url : "graborder/"+odid,
			type : "GET",
			success : function(rs) {
				var data = $.parseJSON(rs);
				if (data.status == 1000) {
					alert("抢单成功！");
				} else {
					alert("手慢啦!");
				}
			},
			error : function(error) {
				alert("服务异常!");
			}
		});
	}
	function getLcTime(ns){
		return new Date(parseInt(ns)).toLocaleString().substr(0,17);
	}
</script>
