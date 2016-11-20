<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="res" value="${ctx}/resource" scope="request"/>
<c:choose>
    <%--开发环境--%> <c:when test="${not empty param.dev}"><c:set var="root" value="${res}/src" scope="request"/></c:when>
    <%--生产环境--%><c:otherwise><c:set var="root" value="${res}/src" scope="request"/></c:otherwise>
</c:choose>
