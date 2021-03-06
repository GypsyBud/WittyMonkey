/**
 * Created by neilw on 2017/4/23.
 */
var layer;
var form;
layui.use(['layer', 'form'], function () {
    layer = layui.layer;
    form = layui.form();
    form.on('select(type)', function (data) {
        search();
    });
    form.on('select(searchType)', function (data) {
        changeType(data.value);
    });
    changeType(1);
});
function changeType(obj) {
    var type = obj | $("#type").val();
    var html = "";
    if (type == 1 || type == 3) {
        html = "";
        var condition = {"searchType": type};
        page("getMaterielByPage.do", 1, condition);
    } else if (type == 2) {
        $.ajax({
            url: "getAllMaterielTypeByHotel.do",
            dataType: "json",
            type: "GET",
            async: false,
            success: function (data) {
                var res = eval("(" + data + ")");
                html = '<select name="type" id="type" lay-filter="type">';
                for (var i in res) {
                    html += '<option value="' + res[i]["id"] + '">' + res[i]["name"] + '</option>';
                }
                html += '</select>';
            }
        });
    } else if (type == 4 || type == 5){
        html = "";
        if (type == 4){
            html = '<div style="display: inline-block;"><input type="text" name="barcode" id="barcode" class="layui-input layui-inline"/></div>';
        } else if (type == 5){
            html = '<div style="display: inline-block;"><input type="text" name="name" id="name" class="layui-input layui-inline"/></div>';
        }
        html += '<div class="searchBtn" onclick="search()"><i class="layui-btn layui-icon">&#xe615;</i>';
    }
    $(".searchContent").html(html);
    form.render("select");
}
function refreshTable(obj) {
    var html = "";
    if (obj.length == 0) {
        html = '<tr class="text-c">' +
            '<td colspan="8">' + no_data + '</td>' +
            '</tr>"';
    } else {
        for (var i in obj) {
            html += "<tr class='text-c";
            if (obj[i]["stock"] < obj[i]["warningStock"]) {
                html += ' text-highlight-red'
            }
            html += "'>" +
                "<td>" + obj[i].barcode + "</td>" +
                "<td>" + obj[i].name + "</td>" +
                "<td>" + obj[i].materielType + "</td>" +
                "<td>" + obj[i].stock + "</td>" +
                "<td>" + obj[i].unit + "</td>" +
                "<td>" + obj[i].entryUser + "</td>" +
                "<td>" + formatDate(obj[i].entryDatetime) + "</td>" +
                "<td>" +
                "<i class='editBtn layui-icon layui-btn layui-btn-primary layui-btn-small' onclick='editMateriel(" + obj[i].id + ")'>&#xe642; " + btn_edit + "</i>" +
                "<i class='deleteBtn layui-icon layui-btn layui-btn-primary layui-btn-small' onclick='deleteMateriel(" + obj[i].id + "," + obj[i].stock + ")'>&#xe640; " + btn_delete + "</i>" +
                "</td>" +
                "</tr>";
        }
    }
    $("#dataTabel").html(html);
}

function search() {
    var type = $("#searchType").val();
    var condition;
    if (type == 4) {
        condition = {"searchType": type, "barcode": $("#barcode").val()};
    } else if (type == 5){
        condition = {"searchType": type, "name": $("#name").val()};
    } else if (type == 2){
        condition = {"searchType": type, "typeId": $("#type").val()};
    }
    page("getMaterielByPage.do", 1, condition);
}

function editMateriel(id) {
    layer.open({
        type: 2,
        area: ['800px', '350px'],
        maxmin: false,
        shade: 0.4,
        title: materiel_detail_title,
        content: "toMaterielDetail.do?id=" + id
    });
}

function deleteMateriel(id, stock) {
    var delete_hint;
    if (stock != 0){
        delete_hint = materiel_detele_not_null_hint;
    } else {
        delete_hint = materiel_delete_hint;
    }
    layer.confirm(delete_hint, {icon: 7, title: materiel_delete_title},
        function (index) {
            var load = layer.load();
            $.ajax({
                url: "deleteMateriel.do",
                data: {"id": id},
                dataType: "json",
                type: "POST",
                success: function (data) {
                    layer.close(load);
                    var result = eval("(" + data + ")");
                    switch (result.status) {
                        case 400:
                            layer.msg(materiel_delete_not_exist, {
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
                            layer.msg(materiel_delete_success, {
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

function showAddMateriel(){
    layer.open({
        type: 2,
        area: ['800px', '350px'],
        maxmin: false,
        shade: 0.4,
        title: materiel_add_title,
        content: "toAddMateriel.do"
    });
}