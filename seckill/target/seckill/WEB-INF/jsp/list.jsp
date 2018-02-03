
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--引入jstl-->
<%@include file="comment/tag.jsp"%>
<!doctype html>
<html lang="en">
<head>
    <title>秒杀列表页面</title>
    <%@include file="comment/head.jsp"%>
</head>
<body>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading text-center">
            <h2>秒杀列表</h2>
        </div>

    </div>
    <div class="panel-body">
        <table class="table table-hover">
            <thead>
            <tr>
                <th>名称</th>
                <th>库存</th>
                <th>开始时间</th>
                <th>结束时间</th>
                <th> 创建时间</th>
                <th>详情页</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="sk" items="${list}">
                <tr></tr>
                <td>${sk.name}</td>
                <td>${sk.number}</td>
                <td>
                    <fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH-mm-ss"/>
                </td>
                <td>
                    <fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH-mm-ss"/>
                </td>
                <td>
                    <fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH-mm-ss"/>
                </td>
                <td>
                    <a class=" btn btn-info" href="/seckill/${sk.seckillId}/detail" target="_blank">
                        link
                    </a>

                </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="${pageContext.request.contextPath}/resources/plugins/jquery.js" type="text/javascript"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="${pageContext.request.contextPath}/resources/plugins/bootstrap-3.3.7-dist/js/bootstrap.js" type="text/javascript"></script>
</body>
</html>