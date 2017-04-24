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
<html>
<head>
    <title><fmt:message key="staff.manage.title"/></title>
</head>
<script type="text/javascript" src="js/common.js"></script>
<!-- 根据设置动态加载js语言 -->
<script type="text/javascript" src="i18n/messages_${loginUser.setting.lang }.js"></script>
<link href="style/common.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="js/staff_manage.js"></script>
<fmt:setBundle basename="i18n/messages_${loginUser.setting.lang }"/>
<body>
<div>
    <nav class="breadcrumb">
        <i class="refreshBtn layui-icon layui-btn layui-btn-small" onclick="reload();">&#x1002;</i>
    </nav>
    <div id="main">
        <i class="newBtn layui-icon layui-btn layui-btn-radius layui-btn-normal" onclick="showAddStaff()">&#xe61f;
            <fmt:message key="staff.add"/></i>
        <form class="layui-form">
            <div style="width: 200px; margin: 0 auto">
                <select name="type" id="type" lay-filter="type" lay-verify="required" onchange="changeType()">
                    <option value="0" selected><fmt:message key="staff.incumbent"/></option>
                    <option value="1"><fmt:message key="staff.dimissory"/></option>
                </select>
            </div>
        </form>
        <table class="layui-table" lay-skin="line" style="min-width: 920px">
            <thead id="tableHead">
            </thead>
            <tbody id="dataTabel">
            </tbody>
        </table>
        <div id="page"></div>
    </div>
</div>
</body>
</html>
