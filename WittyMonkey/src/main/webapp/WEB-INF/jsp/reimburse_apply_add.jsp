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
<%@include file="common/iconfont.jsp" %>
<html>
<head>
</head>
<link rel="stylesheet" type="text/css" href="style/common.css"/>
<link rel="stylesheet" type="text/css" href="style/reimburse.css"/>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript" src="js/reimburse_apply_add&update.js"></script>
<!-- 根据设置动态加载js语言 -->
<script type="text/javascript" src="i18n/messages_${loginUser.setting.lang }.js"></script>
<fmt:setBundle basename="i18n/messages_${loginUser.setting.lang }"/>
<body>
<form id="add-form" class="layui-form">
    <input type="hidden" id="method" name="method" value="add"/>
    <table class="form_table" style="width: 350px">
        <tr>
            <td><label class="layui-form-label" style="margin-left: 10px"><fmt:message key="reimburse.money"/></label></td>
            <td><input type="number" name="money" id="money" class="layui-input" onblur="validateMoney(this)"/></td>
        </tr>
        <tr>
            <td><label class="layui-form-label"><fmt:message key="reimburse.apply_note"/></label></td>
            <td><textarea id="applyNote" name="applyNote" class="layui-textarea" onblur="validateNote(this)"></textarea></td>
        </tr>
    </table>
</form>
<div id="btnGroup">
    <input type="button" class="layui-btn layui-btn-radius layui-btn-danger" value="<fmt:message key="btn.close"/>"
           onclick="closeMe()"/>
    <input type="button" class="layui-btn layui-btn-radius" value="<fmt:message key="btn.save"/>"
           onclick="save()"/>
</div>
</body>
</html>
