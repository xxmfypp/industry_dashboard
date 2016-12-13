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
    <style>
        .login-header{
            margin: 0;
            margin-bottom: 1rem;
            font-size: 1.85rem;
        }
        .login-wrap{
            background:#03a9f4;
            line-height: 100vh;
            background-position: center center;
            background-size: cover;
            text-align: center;
        }
        .login-container-wrap {
            width: 28.25rem;
            padding: 2rem 1rem;
            text-align: center;
            vertical-align: middle;
            line-height: 1.5;
            display: inline-block;
            color: #fff;
            background-color: rgba(192, 236, 255, 0.2);
            border: 1px solid transparent;
            border-radius: 5px;
            -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
            box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
        }
        .login-header{
            font-weight: normal;
            text-shadow: 1px 2px 3px rgba(0,0,0,.3);
            letter-spacing: 2px;
        }
        .opts-wrap{
            display: inline-block;
            width: 80%;
        }
        .opts-item{
            margin-top: 1.5rem;
            background: rgba(255,255,255,.15);
            /* font-size: 1.5rem; */
            text-align: left;
            padding-left:1rem;
            white-space: nowrap;
        }
        .opts-item .fa{
            vertical-align: middle;
            font-size: 1.5rem;
            width: 1.2rem;
        }
        .opts-item .ipt{
            width: calc(100% - 1.5rem);
            height: 50px;
            font-size: 1.2rem;
            background: transparent;
            color: #fff;
            border: none;
            outline: none;
            -webkit-box-shadow: none;
            -moz-box-shadow: none;
            box-shadow: none;
        }
        .opts-item .ipt::-webkit-input-placeholder { /* Chrome/Opera/Safari */
            color: rgba(255,255,255,0.56);
        }
        .opts-item .ipt::-moz-placeholder { /* Firefox 19+ */
            color: rgba(255,255,255,0.56);
        }
        .opts-item .ipt:-ms-input-placeholder { /* IE 10+ */
            color: rgba(255,255,255,0.56);
        }
        .opts-item .ipt:-moz-placeholder { /* Firefox 18- */
            color: rgba(255,255,255,0.56);
        }
        .login-btn{
            display: block;
            width: 100%;
            line-height: 2.5;
            font-size: 1.2rem;
            border: none;
            background-color: #0089c7;
            -webkit-transition: all .3s;
            -moz-transition: all .3s;
            -ms-transition: all .3s;
            -o-transition: all .3s;
            transition: all .3s;
        }
        .login-btn:hover{
            background-color: #006d9e;
        }
    </style>
</head>
<body>

<div class="login-wrap">
    <div class="login-container-wrap">
        <h1 class="login-header">泽它科技数据分析系统</h1>
        <form class="form" method="post" action="login">
            <div class="opts-wrap">
                <div class="opts-item">
                    <span class="fa fa-user"></span> <input class="form-control ipt" name="login_name" placeholder="请输入用户名" value="test_admin">
                </div>
                <div class="opts-item">
                    <span class="fa fa-lock"></span> <input class="form-control ipt" name="pwss" type="password" placeholder="请输入密码" value="123456">
                </div>
                <div class="opts-item" style="padding: 0">
                    <button type="submit" class="login-btn">登 录</button>
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>