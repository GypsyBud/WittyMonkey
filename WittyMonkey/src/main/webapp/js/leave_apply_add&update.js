/**
 * Created by neilw on 2017/4/27.
 */
var layer;
var form;
layui.use(['layer', 'form'], function () {
    layer = layui.layer;
    form = layui.form();
    form.on('select(type)', function (data) {
        calcDeduct();
    });
});

$(document).ready(function () {
    $("#leaveDate").dateRangePicker({
        language: $("#lang").val(),
        startDate: new Date(),
        selectForward: true,
        autoClose: true,
        showShortcuts: false,
        singleMonth: true,
        setValue: function (s, s1, s2) {
            $("#leaveDate").val(s1 + " - " + s2);
            $("#from").val(s1);
            $("#to").val(s2);
            $("#days").val(dateDiff(s1, s2) + 1);
            calcDeduct();
        }
    });
});

// 获取扣薪
function calcDeduct() {
    var from = $("#from").val();
    var to = $("#to").val();
    if (from == undefined || from.length <= 0 || to == undefined || to.length <= 0) {
        return false;
    }
    $.ajax({
        url: "calcDeduct.do",
        data: {"from": from, "to": to, 'type': $("#type").val()},
        dataType: "JSON",
        type: "GET",
        success: function (data) {
            var res = eval("(" + data + ")");
            switch (res["status"]) {
                case 401:
                    layer.tips(date_wrong, $("#leaveDate"), {tips: 2});
                    break;
                case 200:
                    $("#deduct").val(res["deduct"]);
                    break;
            }
        }
    });
    return true;
}
/**
 *计算天数差的函数，通用
 */
function leaveDays(from, to) {    //sDate1和sDate2是2006-12-18格式
    if (from == undefined || to == undefined) {
        return 0;
    }
    if (from instanceof Date) {
        from = from.format("yyyy-MM-dd");
    }
    var fromDate = new Date(from.replace(/-/g, "/"));
    var toDate = new Date(to.replace(/-/g, "/"));
    return parseInt(Math.abs(toDate - fromDate) / 1000 / 60 / 60 / 24) + 1    //把相差的毫秒数转换为天数;
}

function save() {
    var from = $("#from").val();
    var to = $("#to").val();
    if (from == undefined || to == undefined) {
        layer.tips(leave_date_choose, $("leaveDate"), {tips: 2});
        return false;
    }
    if (!validateNote($("#applyNote"))) {
        return false;
    }
    var load = layer.load();
    var success_hint;
    var method = $("#method").val();
    if (method == "add"){
        success_hint = leave_apply_success;
    } else if (method = "upadte"){
        success_hint = apply_edit_success;
    }
    $.ajax({
        url: "saveLeaveApply.do",
        data: $("#leave_form").serialize(),
        dataType: "JSON",
        type: "POST",
        success: function (data) {
            layer.close(load);
            var res = eval("(" + data + ")");
            switch (res["status"]) {
                case 400:
                    layer.tips(leave_date_choose, $("#leaveDate"), {tips: 2});
                    break;
                case 401:
                    layer.tips(time_error, $("#leaveDate"), {tips: 2});
                    break;
                case 410:
                    layer.tips(messageOfValidateLength(apply_note), $("#applyNote"), {tips: 2});
                    break;
                case 420:
                    layer.msg(apply_approved, {
                        icon: 2, time: 2000
                    }, function () {
                        parent.location.reload();
                        closeMe();
                    });
                    break;
                case 421:
                    layer.msg(apply_rejected, {
                        icon: 2, time: 2000
                    }, function () {
                        parent.location.reload();
                        closeMe();
                    });
                    break;
                case 500:
                    layer.msg(error_500, {
                        icon: 2, time: 2000
                    }, function () {
                        parent.location.reload();
                        closeMe();
                    });
                    break;
                case 200:
                    layer.msg(success_hint, {
                        icon: 1,
                        time: 2000
                    }, function () {
                        parent.location.reload();
                        closeMe();
                    });
                    break;
            }
        }
    });
}