package cc.gavin.grumman.zeta.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;

import java.io.File;

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

    public void test(){
        String fileName = "/WEB-INF/resource/lib/jquery/dist/jquery.js";
        String fileDownloadPath="";
        String  webRootPath = PathKit.getWebRootPath();
        fileName = fileName.startsWith("/")?webRootPath + fileName:fileDownloadPath + fileName;
        System.out.print(fileName);
        renderFile(fileName);
    }
}
