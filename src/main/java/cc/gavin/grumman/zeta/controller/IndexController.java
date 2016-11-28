package cc.gavin.grumman.zeta.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import java.io.*;

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

    public void test() throws Exception {
        UploadFile uploadFile = null;

        FileInputStream in = new FileInputStream(uploadFile.getFile());

        String fileName = "/WEB-INF/resource/lib/jquery/dist/jquery.js";
        String fileDownloadPath="";
        String  webRootPath = PathKit.getWebRootPath();
        fileName = fileName.startsWith("/")?webRootPath + fileName:fileDownloadPath + fileName;
        System.out.print(fileName);
        renderFile(fileName);
    }
}
