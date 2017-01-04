package cc.gavin.grumman.zeta.controller;

import cc.gavin.grumman.zeta.bean.QueryBean;
import cc.gavin.grumman.zeta.service.InsertService;
import cc.gavin.grumman.zeta.service.QueryService;
import cc.gavin.grumman.zeta.util.Constants;
import cc.gavin.grumman.zeta.util.ExcelUtil;
import cc.gavin.grumman.zeta.util.JFinalConfig;
import cc.gavin.grumman.zeta.validate.LoginValidator;
import cc.gavin.grumman.zeta.validate.UploadValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by user on 11/4/16.
 */
public class IndexController extends Controller {

    private Logger logger = Logger.getLogger(IndexController.class);

    /**
     * 主页
     */
    public void index() throws ExecutionException, InterruptedException {

        renderJsp("login.jsp");

    }

    @Before({Tx.class,LoginValidator.class})
    public void login(){
        String login_name = getPara("login_name");
        String pwss = getPara("pwss");
        Record customerInfo =  Db.findFirst("select * from customer_info where login_name=? and pwss = password(?) ",login_name,pwss);
        if(customerInfo!=null){
            setSessionAttr("customerInfo",customerInfo);
            renderJsp("index.jsp");
        }else{
            renderJsp("login.jsp");
        }
    }



    /**
     * 统计查询
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void query() throws ExecutionException, InterruptedException {
        long start_time = System.currentTimeMillis();

        List<QueryBean> list = new ArrayList<QueryBean>();

        list.add(new QueryBean("genderData","select gender as AA ,count(1) as BB from user_info GROUP BY gender ",0));

        list.add(new QueryBean("birthdayData","select DATE_FORMAT(birthday,'%Y') as AA,count(1) as BB from user_info GROUP BY  DATE_FORMAT(birthday,'%Y') ",1));

        list.add(new QueryBean("categoryData","select category as AA,COUNT(1) as BB from commodity_info GROUP BY category ",0));

        list.add(new QueryBean("priceData","select '价格小于200'as AA,COUNT(1) as BB from commodity_info where price < 200" +
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
                "select '价格大于2000',COUNT(1) from commodity_info where price >2000",0));

        list.add(new QueryBean("collectionData","select b.`name` as AA,count(1) as BB from collection_info a INNER JOIN commodity_info b on a.commodity_id=b.commodity_id GROUP BY a.commodity_id ORDER BY COUNT(1) DESC LIMIT 0,10",0));

        list.add(new QueryBean("orderAmountData","select DATE_FORMAT(create_time,'%Y-%m') as AA ,SUM(amount) as BB  from order_info GROUP BY DATE_FORMAT(create_time,'%Y-%m')",1));

        list.add(new QueryBean("orderAreaData","select area as AA,count(1) as BB  from order_info GROUP BY area ",0));

        Future<JSONObject> dataJson = JFinalConfig.fjp.submit(new QueryService(list));

        JSONObject msg = new JSONObject();
        msg.put("status","0");
        msg.put("msg","");
        msg.put("data",dataJson.get());

        logger.info("统计耗时:"+String.valueOf(System.currentTimeMillis()-start_time));
        renderJson(msg);
    }




    @Before({Tx.class,UploadValidator.class})
    public void uploadEcl() {
        upload2();
    }

    private void upload1(){
        long start_time = System.currentTimeMillis();

        List<String> errMessage = new ArrayList<String>();
        File excelFile = null;
        try {
            UploadFile uploadFile = getFile("uploadFile");
            excelFile = uploadFile.getFile();
            Db.batch(Arrays.asList(new String[]{"truncate table user_info", "truncate table commodity_info", "truncate table order_info", "truncate table collection_info"}), 4);
            List<List<String>> sqlList = ExcelUtil.readXlsx1(excelFile);
            JFinalConfig.fjp.execute(new InsertService(sqlList));
        } catch (Exception e) {
            e.printStackTrace();
            errMessage.add(e.getMessage());
        }finally {
            if(excelFile!=null){
                excelFile.delete();
            }
        }
        JSONObject resultMsg = new JSONObject();

        if (errMessage.isEmpty()) {
            resultMsg.put("status", 0);
            resultMsg.put("msg", "上传成功");
        } else {
            resultMsg.put("status", 1);
            resultMsg.put("msg", errMessage);
        }

        logger.info("插入耗时:"+String.valueOf(System.currentTimeMillis()-start_time));
        renderJson(resultMsg);
    }

    private void upload2(){
        long start_time = System.currentTimeMillis();

        List<String> errMessage = new ArrayList<String>();
        File excelFile = null;
        try {
            UploadFile uploadFile = getFile("uploadFile");
            excelFile = uploadFile.getFile();
            Db.batch(Arrays.asList(new String[]{"truncate table department_allocation_info",
                    "truncate table distribution_allocation_info",
                    "truncate table ppp_outgoing_info",
                    "truncate table ppp_storage_info",
                    "truncate table storage_info",
                    "truncate table store_storage_info"}), 6);
            List<List<String>> sqlList = ExcelUtil.readXlsx2(excelFile);
            JFinalConfig.fjp.submit(new InsertService(sqlList));
        } catch (Exception e) {
            e.printStackTrace();
            errMessage.add(e.getMessage());
        }finally {
            if(excelFile!=null){
                excelFile.delete();
            }
        }
        JSONObject resultMsg = new JSONObject();

        if (errMessage.isEmpty()) {
            resultMsg.put("status", 0);
            resultMsg.put("msg", "上传成功");
        } else {
            resultMsg.put("status", 1);
            resultMsg.put("msg", errMessage);
        }

        logger.info("插入耗时:"+String.valueOf(System.currentTimeMillis()-start_time));
        renderJson(resultMsg);
    }

    public void to_query_store_storage(){
        setAttr("materiel_types",Constants.storestorage.materiel_type);
        setAttr("suppliers",Constants.storestorage.supplier);
        setAttr("income_departments",Constants.storestorage.income_department);
        setAttr("expenditure_departments",Constants.storestorage.expenditure_department);
        renderJson();

    }


    public void query_store_storage(){
        int pageNumber;
        if(getParaToInt("pn")==null){
            pageNumber=1;
        }else{
            pageNumber=getParaToInt("pn");
        } //前端通过pn传参
        int pageSize;           //指定每一页的显示数量
        if(getParaToInt("ps")==null){
            pageSize=10;
        }else{
            pageSize=getParaToInt("ps");
        }  //对一

        String materiel_type = getPara("materiel_type");
        String supplier = getPara("supplier");
        String income_department = getPara("income_department");
        String expenditure_department = getPara("expenditure_department");

        StringBuilder sb = new StringBuilder(" from store_storage_info where 1=1 ");
        if(StringUtils.isNotBlank(materiel_type)){
            sb.append(" and materiel_type = '"+materiel_type+"' ");
        }
        if(StringUtils.isNotBlank(supplier)){
            sb.append(" and supplier = '"+supplier+"' ");
        }
        if(StringUtils.isNotBlank(income_department)){
            sb.append(" and income_department = '"+income_department+"' ");
        }
        if(StringUtils.isNotBlank(expenditure_department)){
            sb.append(" and expenditure_department = '"+expenditure_department+"' ");
        }

        Page<Record> page = Db.paginate(pageNumber,pageSize," select * ",sb.toString());

        renderJson(page);

    }
}
