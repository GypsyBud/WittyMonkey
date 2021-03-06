<%--
  Created by IntelliJ IDEA.
  User: neilw
  Date: 2017/4/7
  Time: 14:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="common/taglib.jsp" %>
<%@ include file="common/js&css.jsp" %>
<%@ include file="common/iconfont.jsp" %>
<html>
<head>
    <link href="style/common.css" rel="stylesheet"
          type="text/css"/>
</head>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript" src="js/staff_add.js"></script>
<link type="text/css" href="style/staff_manage.css" rel="stylesheet"/>
<!-- 根据设置动态加载js语言 -->
<script type="text/javascript" src="i18n/messages_${loginUser.setting.lang }.js"></script>
<fmt:setBundle basename="i18n/messages_${loginUser.setting.lang }"/>
<body>
<div id="border">
    <form id="add_form" class="layui-form">
        <table class="form_table">
            <tr>
                <td><label class="layui-form-label"><fmt:message key="staff.real_name"/></label></td>
                <td><input type="text" class="layui-input" name="realName"
                           id="realName" onblur="validateRealName(this)"></td>
                <td><label class="layui-form-label"><fmt:message key="staff.idcard"/></label></td>
                <td><input type="text" class="layui-input" name="idcard"
                                         id="idcard" onblur="validateIdCard(this)"></td>
            </tr>
            <tr><td><label class="layui-form-label"><fmt:message key="staff.tel"/></label></td>
                <td><input type="text" class="layui-input" name="tel"
                           id="tel" onblur="validateTel(this)"></td>
                <td><label class="layui-form-label"><fmt:message key="staff.email"/></label></td>
                <td><input type="text" class="layui-input" name="email"
                                         id="email" onblur="validateEmailFormat(this)"></td>
            </tr>
            <tr>
               <td><label class="layui-form-label"><fmt:message key="staff.workdays"/></label></td>
                <td><input class="layui-input" type="number" id="workDays" name="workDays" value="30"/></td>
            </tr>
            <tr>
                <td><label class="layui-form-label"><fmt:message key="staff.roles"/></label></td>
                <td colspan="3">
                    <div class="role_choose">
                        <c:forEach items="${roles}" var="role">
                            <input type="checkbox" name="roles" value="${role.id}" title="${role.name}"/>
                        </c:forEach>
                    </div>
                </td>
            </tr>
      </table>
    </form>
    <div id="btnGroup">
        <input type="button" class="layui-btn layui-btn-radius layui-btn-danger" value="<fmt:message key="btn.close"/>"
               onclick="closeMe()"/>
        <input type="button" class="layui-btn layui-btn-radius" value="<fmt:message key="btn.save"/>"
               onclick="save('save')"/>
    </div>
</div>
</body>
</html>
