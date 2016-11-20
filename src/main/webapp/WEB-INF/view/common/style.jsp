<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:choose>
    <c:when test="${not empty param.dev}">
        <%--开发环境--%>
        <%@include file="core/css-dev.jsp"%>
    </c:when>
    <c:otherwise>
        <%--生产环境--%>
        <%@include file="core/css-dev.jsp"%>
    </c:otherwise>
</c:choose>