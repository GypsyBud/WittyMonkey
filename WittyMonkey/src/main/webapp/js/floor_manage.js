/**
 * Created by Neil on 2017/2/20.
 */
var layer;
var laypage;
layui.use(['layer', 'laypage'],function () {
    layer = layui.layer;
    laypage = layui.laypage;
    page("getFloorByPage.do");
});

function refreshTable(obj) {
    var html = "";
    if (obj.length == 0) {
        html = '<tr class="text-c">' +
            '<td colspan="6">' + no_data + '</td>' +
            '</tr>"';
    } else {
        for (var i in obj) {
            html += "<tr class='text-c'>" +
                "<td>" + obj[i].floorNo + "</td>" +
                "<td>" + obj[i].roomNum + "</td>" +
                "<td>" + obj[i].note + "</td>" +
                "<td>" + obj[i].entryUser + "</td>" +
                "<td>" + formatDate(obj[i].entryDatetime) + "</td>" +
                "<td>" +
                "<i class='editBtn layui-icon layui-btn layui-btn-primary layui-btn-small' onclick='editFloor(" + obj[i].floorNo + ")'>&#xe642; " + btn_edit + "</i>" +
                "<i class='deleteBtn layui-icon layui-btn layui-btn-primary layui-btn-small' onclick='deleteFloor(" + obj[i].floorNo + ")'>&#xe640; " + btn_delete + "</i>" +
                "</td>" +
                "</tr>";
        }
    }
    $("#dataTabel").html(html);
}
function deleteFloor(floorNo) {
    layer.confirm(floor_manage_delete_hint, {icon: 7, title: floor_manage_delete_title},
        function (index) {
            var load = layer.load();
            $.ajax({
                url: "deleteFloor.do",
                data: {"floorNo": floorNo},
                dataType: "json",
                type: "GET",
                success: function (data) {
                    layer.close(load);
                    var result = eval("(" + data + ")");
                    switch (result.status) {
                        case 400:
                            layer.msg(floor_manage_delete_not_exist, {
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
                            layer.msg(floor_manage_delete_success, {
                                icon: 6,
                                time: 2000
                            }, function () {
                                window.location.reload();
                                closeMe();
                            });
                            break;
                    }
                }
            });
            layer.close(index);
        });
}
function editFloor(obj) {
    layer.open({
        type: 2,
        area: ['400px', '280px'],
        maxmin: false,
        shade: 0.4,
        title: floor_manage_edit_title,
        content: "toEditFloor.do?floorNo=" + obj
    });
}
function showAddFloor() {
    layer.open({
        type: 2,
        area: ['400px', '280px'],
        maxmin: false,
        shade: 0.4,
        title: floor_manage_add_title,
        content: "toAddFloor.do"
    });
}