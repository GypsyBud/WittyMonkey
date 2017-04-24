<%--
  Created by IntelliJ IDEA.
  User: neilw
  Date: 2017/2/20
  Time: 11:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="common/taglib.jsp" %>
<%@ include file="common/js&css.jsp" %>
<%@include file="common/iconfont.jsp" %>
<html>
<head>
    <title><fmt:message key="room.add.title"/></title>
    <link rel="stylesheet" type="text/css" href="style/common.css"/>
    <script type="text/javascript" src="js/common.js"></script>
    <script type="text/javascript" src="js/materiel_add&update.js"></script>
    <!-- 根据设置动态加载js语言 -->
    <script type="text/javascript" src="i18n/messages_${loginUser.setting.lang }.js"></script>
    <fmt:setBundle basename="i18n/messages_${loginUser.setting.lang }"/>
</head>
<body>
<i class="layui-icon layui-btn editBtn layui-btn-small" onclick="edit()"
   style="position: absolute;top: 5px;right: 5px;">&#xe642;</i>
<form id="materiel_form" class="layui-form">
    <!-- java端处理方式 -->
    <input type="hidden" id="method" name="method" value="update"/>
    <table class="form_table">
        <tr>
            <td><label class="layui-form-label"><fmt:message key="materiel.barcode"/></label></td>
            <td style="width: 200px">
                <div class="input"><input type="text" class="layui-input" id="barcode" name="barcode" disabled
                                          onblur="validateBarcode('update',this)" value="${editMateriel.barcode}"/></div>
            </td>
            <td><label class="layui-form-label"><fmt:message key="materiel.name"/></label></td>
            <td>
                <div class="input"><input type="text" class="layui-input" id="name" name="name"
                                          value="${editMateriel.name}"
                                          disabled
                                          onblur="validateLength(this, '<fmt:message key="materiel.name"/>', 50)"/>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <label class="layui-form-label"><fmt:message key="materiel.unit"/></label></td>
            <td>
                <div class="input inner"><input type="text" class="layui-input" id="unit" name="unit"
                                                value="${editMateriel.unit}"
                                                disabled onblur="validateLength(this, '<fmt:message
                        key="materiel.unit"/>', 10)"/></div>
            </td>
            <td><label class="layui-form-label"><fmt:message key="materiel.type"/></label></td>
            <td>
                <div class="input inner">
                    <select name="typeId" id="typeId" disabled>
                        <c:forEach items="${types}" var="type">
                            <option value="${type.id}"
                                    <c:if test="${type.id eq editMateriel.materielType.id}">
                                        selected
                                    </c:if>
                            >${type.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </td>
        </tr>
        <tr>
            <td><label class="layui-form-label"><fmt:message key="materiel.warn_stock"/></label></td>
            <td>
                <div class="input inner"><input type="number" class="layui-input" disabled
                                                value="${editMateriel.warningStock}" id="warnStock" name="warnStock"/>
                </div>
            </td>
            <td><label class="layui-form-label"><fmt:message key="materiel.sell_price"/></label></td>
            <td>
                <div class="input inner"><input type="number" class="layui-input" id="sellPrice" name="sellPrice"
                                                disabled
                                                placeholder="<fmt:message key="materiel.sell_price.hint"/>"
                                                value="${editMateriel.sellPrice}"/></div>
            </td>
        </tr>
        <tr>
            <td style="margin-top: 10px;"><label class="layui-form-label"><fmt:message key="note"/></label></td>
            <td colspan="3"><textarea class="layui-textarea" id="note" name="note" onblur="validateNote(this)"
                                      disabled>${editMateriel.note}</textarea></td>
        </tr>
    </table>
</form>
<div id="btnGroup">
    <input type="button" class="layui-btn layui-btn-danger layui-btn-radius" value="<fmt:message key="btn.close"/>"
           onclick="closeMe()"/>
    <input type="hidden" class="layui-btn layui-btn-radius" id="updateBtn" value="<fmt:message key="btn.save"/>"  onclick="save()"/>
</div>
</body>
</html>
