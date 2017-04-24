<%--
  Created by IntelliJ IDEA.
  User: neilw
  Date: 2017/2/15
  Time: 9:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="common/taglib.jsp" %>
<%@ include file="common/js&css.jsp" %>
<%@ include file="common/iconfont.jsp" %>
<%@ include file="common/laypage.jsp" %>
<html>
<head>
    <title><fmt:message key="floor.manage.title"/></title>
</head>
<script type="text/javascript" src="js/common.js"></script>
<!-- 根据设置动态加载js语言 -->
<script type="text/javascript" src="i18n/messages_${loginUser.setting.lang }.js"></script>
<link href="style/common.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="js/floor_manage.js"></script>
<link type="text/css" rel="stylesheet" href="style/floor_manage.css"/>
<fmt:setBundle basename="i18n/messages_${loginUser.setting.lang }"/>
<body>
<div>
    <nav class="breadcrumb">
            <i class="refreshBtn layui-icon layui-btn layui-btn-small" onclick="reload();">&#x1002;</i>
    </nav>
    <div id="main">
            <i class="newBtn layui-icon layui-btn layui-btn-radius layui-btn-normal" onclick="showAddFloor()">&#xe61f; <fmt:message key="floor.btn.add"/></i>
            <table class="layui-table" lay-skin="line" style="min-width: 850px">
            <thead>
            <tr>
                <th width="50px"><fmt:message key="floor.manage.floor_no"/></th>
                <th width="50px"><fmt:message key="floor.manage.room.num"/></th>
                <th><fmt:message key="note"/></th>
                <th width="150px"><fmt:message key="floor.manage.entry_user"/></th>
                <th width="200px"><fmt:message key="floor.manage.entry_date"/></th>
                <th width="150px"><fmt:message key="operation"/></th>
            </tr>
            </thead>
            <tbody id="dataTabel">
            </tbody>
        </table>
        <div id="page"></div>
    </div>
</div>
</body>
</html>
