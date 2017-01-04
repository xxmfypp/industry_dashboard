<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/common/taglib.jsp"%>
<c:set var="pageNumber" value="${empty param.pageNumber? 1 : param.pageNumber}"/>
<c:set var="pageSize" value="${empty param.pageSize? 10 : param.pageSize}"/>
<c:set var="listDataHtml">
    <c:forEach items="${dataList }" var="objection" varStatus="status">
        <tr>

            <td class="text-center">

            </td>
            <td class="text-center">

            </td>
        </tr>
    </c:forEach>
    <c:if test="${fn:length(dataList) < 1}">
        <tr>
            <td colspan="7">
                <%@ include file="/WEB-INF/view/common/emptyListTip.jsp" %>
            </td>
        </tr>
    </c:if>
</c:set>

<c:choose>
    <c:when test="${param.fragment}">
        ${listDataHtml}
    </c:when>
    <c:otherwise>

        <div class="main-wrap">
            <div class="blank-panel" >

                <form class="js-query-form">
                    <div class="query-opts ">
                        <div class="query-group">
                            <div class="query-item">

                                <div class="query-title">客户名称：</div>
                                <div class="query-input">
                                    <input name="customerName" id="customerName" class="form-control w-7" value="">
                                </div>

                                <div class="query-title">日期：</div>
                                <div class="query-input">
                                    <input type="text" class="form-control w-7" id="applyTime" name="applyTime" value=""/>
                                </div>
                            </div>

                        </div>

                        <div class="btns-group">
                            <button type="button" class="btn btn-primary btn-md js-query-btn">查询</button>

                        </div>

                    </div>
                </form>

                <div class="table-wrap auto">
                    <table class="main-table">
                        <thead>
                        <tr>
                            <th style="min-width: 8rem">时间</th>
                            <th style="width: 10rem;">操作</th>
                        </tr>
                        </thead>
                        <tbody class="js-data-list-cot">
                            ${listDataHtml}
                        </tbody>
                    </table>
                    <div class="pagination-cot-wrap">

                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<script>
    rs.data.page.totalRow = "${total}";
    rs.data.page.pageNumber = "${param.pageNumber}";
    rs.data.page.pageSize = "${param.pageSize}";
    rs.data.page.sort = "${param.sort}";
    rs.data.page.order = "${param.order}";
</script>