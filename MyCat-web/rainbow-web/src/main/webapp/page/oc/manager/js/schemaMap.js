//列操作
var oc_schemaMap_caozuo = function(value, row, index) {
	return  formatString('<img title="编辑" onclick="oc_schemaMap_editNode(\'{0}\');" src="{1}"/>&nbsp;<img title="删除" onclick="oc_schemaMap_delete(\'{2}\');" src="{3}"/>', row.guid, './ui/style/images/extjs_icons/pencil.png', row.guid, './ui/style/images/extjs_icons/delete.png');
};

//刷新
var oc_schemaMap_reload = function(){
	$('#oc_schemaMap_datagrid').datagrid('clearSelections');
	$('#oc_schemaMap_datagrid').datagrid('reload',{});
};

//快速查找
var oc_schemaMap_search = function(value,name){	
	if(value != null && value != ''){
		var o = new Object();
		o[name] = value;
		$('#oc_schemaMap_datagrid').datagrid('load',$.parseJSON(JSON.stringify(o)));
		o = null;
	}else{
		oc_schemaMap_reload();
	}
};



//修改操作
function oc_schemaMap_editNode(schemaMap){
	$('#oc_schemaMap_datagrid').datagrid('clearSelections');
	$('#oc_schemaMap_datagrid').datagrid('selectRecord', schemaMap);
	var node = $('#oc_schemaMap_datagrid').datagrid('getSelected');
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			$.messager.progress();
			var isValid = $('#oc_schemaMap_addForm').form('validate');
			if (!isValid){
				$.messager.progress('close');	
				return;
			}
			var d = $(this).closest('.window-body');
			var data =serializeObject($('#oc_schemaMap_addForm'),true);
			var rainbow = new Rainbow();
			rainbow.setAttr(data);
			rainbow.setService("schemaMapService");
			rainbow.setMethod("update");
			rainbowAjax.excute(rainbow,new oc_schemaMap_callback(d,oc_schemaMap_reload));
			}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.editDialog('./page/oc/manager/jsp/schemaMapForm.jsp',buttons,600,350,true,'编辑信息','oc_schemaMap_addForm',node);
}

//新增操作
function oc_schemaMap_add(){
	var buttons = [ {
		text : '保存',
		iconCls : 'icon-ok',
		handler : function() {
			var d = $(this).closest('.window-body');
			var schemaName = $('#oc_schemamap_schema_name').val();
			var nodes = $('#oc_schemamap_table_datagrid').datagrid('getChecked');
			var rainbow = new Rainbow();
			rainbow.set("schemaName", schemaName);
			rainbow.setRows(nodes);
			rainbow.setService("schemaMapService");
			rainbow.setMethod("insert");
			rainbowAjax.excute(rainbow,new oc_schemaMap_callback(d,oc_schemaMap_reload));
		}
	},{
		text : '取消',
		iconCls : 'icon-cancel',
		handler : function() {
			$(this).closest('.window-body').dialog('destroy');
		}
	}];
	rainbowDialog.addDialog('./page/oc/manager/jsp/schemaMapForm.jsp',buttons,800,450,true,'新增信息','oc_schemaMap_addForm');
}

//删除操作
var oc_schemaMap_delete = function(guid){
	$('#oc_schemaMap_datagrid').datagrid('clearSelections');
	$('#oc_schemaMap_datagrid').datagrid('selectRecord',guid);
	var node = $('#oc_schemaMap_datagrid').datagrid('getSelected');
	$.messager.confirm('询问', '您确定要删除当前【'+node.schemaName+'】记录？', function(b) {
		if(b){
			$.messager.progress();
			var d = $(this).closest('.window-body');
			var rainbow = new Rainbow();
			rainbow.addRows({"guid":guid});
			rainbow.setService("schemaMapService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new oc_schemaMap_callback(d,oc_schemaMap_reload));
		}
	});
};

//批量删除操作
var oc_schemaMap_batchDelete = function(){
	var nodes = $('#oc_schemaMap_datagrid').datagrid('getSelections');
	if(nodes.length > 0){
		$.messager.confirm('询问', '您确定要删除所有选择的记录吗？', function(b) {
			if(b){
			$.messager.progress();
			var rainbow = new Rainbow();
			for(var i = 0 ; i < nodes.length ; i++){
				rainbow.addRows({"guid":nodes[i].guid});
			}
			rainbow.setService("schemaMapService");
			rainbow.setMethod("delete");
			rainbowAjax.excute(rainbow,new oc_schemaMap_callback(null,oc_schemaMap_reload));
			}
		});
	}else{
		$.messager.show({title:'提示',msg:'请选择一条记录!'});
	}
};


//ajax回调处理
var oc_schemaMap_callback = function(dialog,relod){
		this.onSuccess=function(data){
			try {
				if (data.success) {
					relod();
					if(dialog){
						dialog.dialog('destroy');
					}
				}
				$.messager.progress('close');
				$.messager.show({
					title : '提示',
					msg : data.msg
				});
			} catch (e) {
				$.messager.progress('close');
				$.messager.alert('提示', "系统异常!");
			}
		};
		this.onFail = function(jqXHR, textStatus, errorThrown){
			$.messager.progress('close');
			$.messager.alert('提示', "系统异常!");
		};
	};

//查询过滤
var oc_schemaMap_query = function(){
	var datas =serializeObject($('#oc_schemaMap_queryForm'));
	$('#oc_schemaMap_datagrid').datagrid('load',datas);
};

//清空查询条件
var oc_schemaMap_query_clear = function(){
	$('#oc_schemaMap_queryForm input').val('');
	oc_schemaMap_reload();
};