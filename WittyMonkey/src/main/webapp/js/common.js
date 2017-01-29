/**
 * 国际化
 */
$(document).ready(function(){
	loadProperties("messages", "i18n/");
});
function loadProperties(name, path, lang){
	var lang = lang || navigator.language;
	jQuery.i18n.properties({
		name: name,
		path: path,
		mode: 'map',
		language: lang
	})
}
/**
 * 验证用户名
 * @param nameInp
 * @returns
 */
function validateLoginName(nameInp){
	var name = $(nameInp).val();
	if (name == null || name == ""){
		layer.tips($.i18n.prop("regist.input_name_first"),nameInp,{tips:4});
		return false;
	}
	$.ajax({
		type: "GET",
		url:"validateLoginName.do",
		data: {"loginName": name},
		dataType: "json",
		success: function(data) {
			var result = eval("(" + data + ")");
			if (result.status == 200){
				layer.tips($.i18n.prop("regist.user_is_exist"),nameInp,{tips:4});
				return false;
			} else{
				return true;
			}
		},
		error: function(data) {
			layer.msg($.i18n.prop("error_500"),{time:3000, icon: 5});
		}
	});
	return true;
}
/**
 * 验证密码
 * @param pwd
 * @returns
 */
function validatePassword(pwdInp){
	var password = $(pwdInp).val();
	if ( password.length == 0) {
		layer.tips($.i18n.prop("regist.input_password_first"),pwdInp,{tips:4});
		return false;
	} else if ( password.length < 6) {
		layer.tips($.i18n.prop("regist.password_less_six"),pwdInp,{tips:4});
		return false;
	} else {
		return true;
	}
}
/**
 * 验证重复密码
 * @param repwd
 * @returns
 */
function validateRepassword(repwdInp){
	var password = $("#password").val();
	var repassword = $(repwdInp).val();
	if (password != repassword){
		layer.tips($.i18n.prop("regist.password_not_same"), repwdInp,{tips:4});
		return false;
	} else {
		return true;
	}
}
/**
 * 验证邮箱
 * @param email
 * @returns
 */
function validateEmail(email){
	var emailStr = $(email).val();
	var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
	if (!reg.test(emailStr)){
		layer.tips($.i18n.prop("regist.email_is_wrong"), email,{tips:4});
		return false;
	} else {
		return true;
	}
}
/**
 * Ajax方式验证发至邮箱的验证码是否正确
 * <b>status:400:没有获取验证码；200：验证通过；401：验证码错误</b>
 * @param codeInp
 * @returns
 */
function validateCode(codeInp) {
	var code = $(codeInp).val();
	var codeBtn = $("get_code");
	$.ajax({
		type : "GET",
		url : "validateCode.do",
		data: {
			"code" : code 
		},
		dataType:"text",
		success: function(data){
			var result = eval("(" + data + ")");
			if (result.status == 400){
				layer.tips($.i18n.prop("regist.get_code_first"), codeBtn,{tips:1});
				return false;
			} else if (result.status == 401){
				layer.tips($.i18n.prop("regist.code_is_wrong"), codeInp,{tips:4});
				return false;
			}else if (result.status == 201){
				return true;
			}
		}
	});
}
/**
 * 验证整个注册表单
 * @param form
 * @returns
 */
function validateRegistForm(form){
	var loginName = $(form).find("#loginName");
	var pwd = $(form).find("#password");
	var repwd = $(form).find("#repassword");
	var email = $(form).find("#email");
	if (!validateLoginName(loginName)){
		return false;
	}
	if ($(pwd).val() == "" && !validatePassword(pwd)){
		return false;
	}
	if ($(repwd).val() == "" && !validateRepassword(repwd)){
		return false;
	}
	if ($(email).val() == "" && !validateEmail(email)){
		return false;
	}
	return true;
}