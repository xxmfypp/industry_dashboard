<%--
  Created by IntelliJ IDEA.
  User: liwir_000
  Date: 11/20/2016
  Time: 9:47 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/x-underscore-template" id="common-dialog-tpl">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header" style="padding: 10px 15px;">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel"><@=title@></h4>
            </div>
            <div class="modal-body">
                <@=content@>
            </div>
            <@if(obj.buttons){@>
            <div class="modal-footer">
                <@_.each(buttons,function(btn,key){@>
                <button type="button" class="btn <@print(btn.className||'')@>" key="<@-key@>"><@-key@></button>
                <@})@>
            </div>
            <@}@>
        </div>
    </div>
</script>
<c:choose>
    <c:when test="${not empty param.dev}">
        <%--开发环境--%>
        <%@include file="core/js-dev.jsp"%>
    </c:when>
    <c:otherwise>
        <%--生产环境--%>
        <script> window.versionObj = <%@include file="/rev/js/rev-manifest.json"%></script>
        <%@include file="core/js-dev.jsp"%>
    </c:otherwise>
</c:choose>