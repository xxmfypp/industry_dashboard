package cc.gavin.grumman.zeta.controller;

import cc.gavin.grumman.zeta.util.ExcelUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void upload_tmp(){
        List<String> errMessage = new ArrayList<String>();
        try {

            String filePath = "/home/user/Desktop/template.xlsx";
            Map<String,List<Record>> map =  new HashMap<String, List<Record>>();
            if (filePath.endsWith("xls")) {
                ExcelUtil.readXls(filePath);
            } else if (filePath.endsWith("xlsx")) {
                map = ExcelUtil.readXlsx(filePath);
            }

            List<Record> userInfos = map.get("用户信息");
            List<Record> commodityInfos = map.get("商品信息");
            List<Record> orderInfos = map.get("订单信息");
            List<Record> collectionInfos = map.get("收藏信息");

            Db.update("delete from user_info");
            Db.update("delete from commodity_info");
            Db.update("delete from order_info");
            Db.update("delete from collection_info");


            for(int i = 0;i<userInfos.size();i++){

                try{
                    Db.save("user_info","username",userInfos.get(i));
                }catch (Exception e){
                    StringBuilder sb =  new StringBuilder("[用户信息]");
                    sb.append("  用户名:").append(collectionInfos.get(i).getColumns().get("username"));
                    sb.append("  性别:").append(collectionInfos.get(i).getColumns().get("gender"));
                    sb.append("  出生日期:").append(collectionInfos.get(i).getColumns().get("birthday"));
                    sb.append("  发生异常,异常原因：").append(e.getMessage());
                    errMessage.add(sb.toString());
                }
            }

            for(int i = 0;i<commodityInfos.size();i++){

                try{
                    Db.save("commodity_info","commodity_id",commodityInfos.get(i));
                }catch (Exception e){
                    StringBuilder sb =  new StringBuilder("[商品信息]");
                    sb.append("  商品编号:").append(collectionInfos.get(i).getColumns().get("commodity_id"));
                    sb.append("  商品名称:").append(collectionInfos.get(i).getColumns().get("name"));
                    sb.append("  类别:").append(collectionInfos.get(i).getColumns().get("category"));
                    sb.append("  商品价格:").append(collectionInfos.get(i).getColumns().get("price"));
                    sb.append("  发生异常,异常原因：").append(e.getMessage());
                    errMessage.add(sb.toString());
                }
            }

            for(int i = 0;i<orderInfos.size();i++){

                try{
                    Db.save("order_info","order_id",orderInfos.get(i));
                }catch (Exception e){
                    StringBuilder sb =  new StringBuilder("[订单信息]");
                    sb.append("  订单编号:").append(collectionInfos.get(i).getColumns().get("order_id"));
                    sb.append("  用户名:").append(collectionInfos.get(i).getColumns().get("username"));
                    sb.append("  所属地区:").append(collectionInfos.get(i).getColumns().get("area"));
                    sb.append("  订单金额:").append(collectionInfos.get(i).getColumns().get("amount"));
                    sb.append("  订单日期:").append(collectionInfos.get(i).getColumns().get("create_time"));
                    sb.append("  发生异常,异常原因：").append(e.getMessage());
                    errMessage.add(sb.toString());
                }
            }

            for(int i = 0;i<collectionInfos.size();i++){
                try{
                    Db.save("collection_info","username",collectionInfos.get(i));
                }catch (Exception e){
                    StringBuilder sb =  new StringBuilder("[收藏信息]");
                    sb.append("  用户名:").append(collectionInfos.get(i).getColumns().get("username"));
                    sb.append("  商品编号:").append(collectionInfos.get(i).getColumns().get("commodity_id"));
                    sb.append("  收藏日期:").append(collectionInfos.get(i).getColumns().get("create_time"));
                    sb.append("  发生异常,异常原因：").append(e.getMessage());
                    errMessage.add(sb.toString());
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            errMessage.add(e.getMessage());
        }

        JSONObject resultMsg = new JSONObject();

        if(errMessage.isEmpty()){
            resultMsg.put("status",0);
            resultMsg.put("msg","上传成功");
        }else{
            resultMsg.put("status",1);
            resultMsg.put("msg",errMessage);
        }
        renderJson(errMessage);

    }
}
