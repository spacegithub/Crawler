<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page isELIgnored="false"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="/jfinal/js/bootstrap-3.3.7/css/bootstrap.min.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=2.0">
	<title>用户页</title>
	
</head>
<body>
<script type="text/javascript" src="/jfinal/js/jquery/jquery-3.2.1.js"></script>

<script type="text/javascript" language="javascript" src="/jfinal/js/bootstrapPager.js"></script>
	<style type="text/css">
table.gridtable {
	width:2300px; 
	overflow:scroll;
    font-family: verdana,arial,sans-serif;
    font-size:11px;
    color:#333333;
    border-width: 1px;
    border-color: #666666;
    border-collapse: collapse;
    width="1280"
}
table.gridtable th {
    border-width: 1px;
    padding: 8px;
    border-style: solid;
    border-color: #666666;
    background-color: #dedede;
}
table.gridtable td {
    border-width: 1px;
    padding: 8px;
    border-style: solid;
    border-color: #666666;
    background-color: #ffffff;
}
</style>
	商品页 
   	<!-- 	<button name="export" onclick="sub()">导出</button> 
	<script type="text/javascript">  
     function  sub(){  
       $.ajax({  
       type:"post", 
       dataType: 'json',
       
       url:"product/getAllMsg",  
       success:function(data){  
    	   alert(data)
       }
       });  
     }  
    </script>   -->
    <p><a href="http://catch.dai-kuan.cn/catchMsg/product/getAllMsg">导出数据</a>
	<table class="gridtable" >
		<th>区域</th>
		<th>标题</th>
		<th>地址</th>
		<th>楼盘名称</th>
		<th>开始时间</th>
		<th>结束时间</th>
		<th>起拍价</th>
		<th>评估价</th>
		<th>法院</th>
		<th>拍卖次数</th>
		<th>拍卖机构</th>
		<th>房屋类型</th>
		<th>建筑面积</th>
		<th>保证金</th>
		<th>采集日</th>
		<th>房屋结构</th>
		<th>咨询电话</th>
		<th>出价次数</th>
		<th>税费分担</th>
		<th>楼层</th>
		<th>竣工日期</th>
		<th>租赁情况</th>
	<c:forEach items="${list}" var="item">
		<tbody>
			<tr>
				<td>${item.get('city')}</td>
				<td>${item.get('title')}</td>
				<td>${item.get('addr')}</td>
				<td>${item.get('community_name')}</td>
				<td>${item.get('startDate')}</td>
				<td>${item.get('endDate')}</td>
				<td>${item.get('currentPriceCN')}</td>
				<td>${item.get('assessmentPriceCN')}</td>
				<td>${item.get('shopName')}</td>
				<td>${item.get('countNum')}</td>
				<td>${item.get('intermediary')}</td>
				<td>${item.get('type')}</td>
				<td>${item.get('build_area')}</td>
				<td>${item.get('bond')}</td>
				<td>${item.get('collection_date')}</td>
				<td>${item.get('structure')}</td>
				<td>${item.get('tel')}</td>
				<td>${item.get('bidCount')}</td>
				<td>${item.get('taxation')}</td>
				<td>${item.get('floor')}</td>
				<td>${item.get('completionDate')}</td>
				<td>${item.get('lease')}</td>
			</tr>
		</tbody>
	</c:forEach>
	</table>
	
	<div class="container">	
	<div>
			<script>
			var str = '${total}' ;
			document.write(Pager({
			    totalCount:str, 		//总条数为150
			    pageSize:20,    		//每页显示6条内容，默认10
			    buttonSize:5,   		//显示6个按钮，默认10
			    pageParam:'p',   		//页码的参数名为'p'，默认为'page'
			    className:'pagination', //分页的样式
			    prevButton:'上一页',     //上一页按钮
			    nextButton:'下一页',     //下一页按钮
			    firstButton:'首页',      //第一页按钮
			    lastButton:'末页',       //最后一页按钮
			}));
			</script>
		</div>
		</div>
</body>
</html>
