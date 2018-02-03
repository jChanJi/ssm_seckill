<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="comment/tag.jsp"%>
<!doctype html>
<html lang="en">
<head>
    <title>秒杀详情页面</title>
    <%@include file="comment/head.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/bootstrap-3.3.7-dist/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <div class="panel panel-default text-center">
        <div class="pannel-heading">
            <H1>${seckill.name}</H1>
        </div>
        <div class="panel-body">
            <h2 class="text-danger">
                <!--显示时间-->
                <span class="glyphicon glyphicon-time"></span>
                <!--显示倒计时-->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
</div>
<!--登陆弹出层输入电话-->
<div id="killPhoneModal" class="modal fade" >
    <div class="modal-dialog" style="background-color: #ffffff">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>秒杀电话:
                </h3>
            </div>
        </div>

        <div class="modal-body">
            <div class="row">
                <div class="col-xs-8 col-xs-offset-2">
                    <input type="text" name="killPhone" id="killPhoneKey" placeholder="填写手机号码" class="form-control">
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <span id="killPhoneMessage" class="glyphicon"></span>
            <button type="button" id="killPhoneBtn" class="btn btn-success">
                <span class="glyphicon glyphicon-phone"></span>
                提交
            </button>
        </div>
    </div>
</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="${pageContext.request.contextPath}/resources/plugins/jquery.js" type="text/javascript"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="${pageContext.request.contextPath}/resources/plugins/bootstrap-3.3.7-dist/js/bootstrap.js" type="text/javascript"></script>
<!--countdown cdn插件-->
<script src="${pageContext.request.contextPath}/resources/plugins/jquery.countdown.min.js" type="text/javascript"></script>
<!--cookie  插件-->、
<script src="${pageContext.request.contextPath}/resources/plugins/jquery.cookie.min.js" type="text/javascript"></script>
<!--$/{pageContext.request.contextPath}-->
<script src="${pageContext.request.contextPath}/resources/script/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
        //使用EL表达式传入参数
        Seckill.detail.init({
            seckillId:${seckill.seckillId},
            //.time 拿到毫秒时间
            startTime:${seckill.startTime.time},
            endTime:${seckill.endTime.time}
        })
    })
</script>
</body>
</html>