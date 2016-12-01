<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 11/2/16
  Time: 2:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/common/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/view/common/seo.jsp"%>
    <title>泽它科技有限公司</title>
    <%@ include file="/WEB-INF/view/common/style.jsp"%>
</head>
<body >
<%@ include file="/WEB-INF/view/common/header.jsp"%>
<div class="layout">
<%--    <div class="layout-left" id="main-menu-cot">

    </div>--%>
    <div class="layout-main">
        <div class="loading-status"></div>
        <div id="module-page-container" class="page-container">

        </div>
    </div>
</div>

<%@ include file="/WEB-INF/view/common/script.jsp"%>
<script>
    (function(){
        seajs.use("main",function(app){
            app.init({
                el:document.body
            });

        });
    }());
</script>

</body>
</html>
