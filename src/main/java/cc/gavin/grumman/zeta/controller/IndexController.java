package cc.gavin.grumman.zeta.controller;

import cc.gavin.grumman.zeta.service.QueryService;
import cc.gavin.grumman.zeta.util.ExcelUtil;
import cc.gavin.grumman.zeta.util.JFinalConfig;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log4jLogger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by user on 11/4/16.
 */
public class IndexController extends Controller {

    private Logger logger = Logger.getLogger(IndexController.class);

    /**
     * 主页统计查询
     */
    public void index() throws ExecutionException, InterruptedException {

        long start_time = System.currentTimeMillis();


        //用户性别 - 饼状图
        QueryService genderCount = new QueryService("select gender as AA ,count(1) as BB from user_info GROUP BY gender ",0);

        //用户出生日期 - 柱状图
        QueryService birthdayCount = new QueryService("select DATE_FORMAT(birthday,'%Y') as AA,count(1) as BB from user_info GROUP BY  DATE_FORMAT(birthday,'%Y')",1);

        //商品类别 - 环状图
        QueryService categoryCount = new QueryService("select category as AA,COUNT(1) as BB from commodity_info GROUP BY category ",0);

        //价格分布图 - 饼状图
        QueryService priceCount = new QueryService("select '价格小于200'as AA,COUNT(1) as BB from commodity_info where price < 200" +
                " UNION " +
                "select '价格200-700',COUNT(1) from commodity_info where price BETWEEN 200 and 400" +
                " UNION " +
                "select '价格400-600',COUNT(1) from commodity_info where price BETWEEN 400 and 600" +
                " UNION " +
                "select '价格600-800',COUNT(1) from commodity_info where price BETWEEN 600 and 800" +
                " UNION " +
                "select '价格800-1000',COUNT(1) from commodity_info where price BETWEEN 800 and 1000" +
                " UNION " +
                "select '价格1000-1200',COUNT(1) from commodity_info where price BETWEEN 1000 and 1200" +
                " UNION " +
                "select '价格1200-1400',COUNT(1) from commodity_info where price BETWEEN 1200 and 1400" +
                " UNION " +
                "select '价格1400-1600',COUNT(1) from commodity_info where price BETWEEN 1400 and 1600" +
                " UNION " +
                "select '价格1600-1800',COUNT(1) from commodity_info where price BETWEEN 1600 and 1800" +
                " UNION " +
                "select '价格1800-2000',COUNT(1) from commodity_info where price BETWEEN 1800 and 2000" +
                " UNION " +
                "select '价格大于2000',COUNT(1) from commodity_info where price >2000",0);

        //收藏记录 - 柱状图
        QueryService collectionCount = new QueryService("select b.`name` as AA,count(1) as BB from collection_info a INNER JOIN commodity_info b on a.commodity_id=b.commodity_id GROUP BY a.commodity_id ORDER BY COUNT(1) DESC LIMIT 0,10",0);

        //订单金额走势 - 折线图
        QueryService orderAmountCount =  new QueryService("select DATE_FORMAT(create_time,'%Y-%m') as AA ,SUM(amount) as BB  from order_info GROUP BY DATE_FORMAT(create_time,'%Y-%m')",1);

        //订单区域数量 - 地图
        QueryService orderAreaCount = new QueryService("select area as AA,count(1) as BB  from order_info GROUP BY area",0);


        Future<JSON> genderJson = JFinalConfig.fjp.submit(genderCount);
        Future<JSON> birthdayFuture = JFinalConfig.fjp.submit(birthdayCount);
        Future<JSON> categoryFuture = JFinalConfig.fjp.submit(categoryCount);
        Future<JSON> priceFuture = JFinalConfig.fjp.submit(priceCount);
        Future<JSON> collectionFuture = JFinalConfig.fjp.submit(collectionCount);
        Future<JSON> orderAmountFuture = JFinalConfig.fjp.submit(orderAmountCount);
        Future<JSON> orderAreaFuture = JFinalConfig.fjp.submit(orderAreaCount);

        JSONObject jsonResult = new JSONObject();
        jsonResult.put("genderData",genderJson.get());
        jsonResult.put("birthdayData",birthdayFuture.get());
        jsonResult.put("categoryData",categoryFuture.get());
        jsonResult.put("priceData",priceFuture.get());
        jsonResult.put("collectionData",collectionFuture.get());
        jsonResult.put("orderAmountData",orderAmountFuture.get());
        jsonResult.put("orderAreaData",orderAreaFuture.get());

        setAttr("data",jsonResult);

        logger.info("统计耗时:"+String.valueOf(System.currentTimeMillis()-start_time));

        renderJsp("index.jsp");

    }

    /**
     * 上传Excel
     */
    public void uploadEcl(){
        List<String> errMessage = new ArrayList<String>();
        try {
            UploadFile uploadFile = getFile("uploadFile");
            File excelFile = uploadFile.getFile();
            String fileName = uploadFile.getFileName();
            Map<String,List<Record>> excelMap =  new HashMap<String, List<Record>>();
            if (fileName.endsWith("xls")) {
                ExcelUtil.readXls(excelFile);
            } else if (fileName.endsWith("xlsx")) {
                excelMap = ExcelUtil.readXlsx(excelFile);
            }

            List<Record> userInfos = excelMap.get("用户信息");
            List<Record> commodityInfos = excelMap.get("商品信息");
            List<Record> orderInfos = excelMap.get("订单信息");
            List<Record> collectionInfos = excelMap.get("收藏信息");

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
        renderJson(resultMsg);

    }
}
