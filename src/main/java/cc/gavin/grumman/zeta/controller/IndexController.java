package cc.gavin.grumman.zeta.controller;

import com.jfinal.core.Controller;

/**
 * Created by user on 11/4/16.
 */
public class IndexController extends Controller {

    /**
     * 查询
     */
    public void index(){

        renderJsp("index.jsp");
    }


}
