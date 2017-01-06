package cc.gavin.grumman.zeta.controller;

import cc.gavin.grumman.zeta.bean.QueryBean;
import cc.gavin.grumman.zeta.service.InsertService;
import cc.gavin.grumman.zeta.service.QueryService;
import cc.gavin.grumman.zeta.util.Constants;
import cc.gavin.grumman.zeta.util.ExcelUtil;
import cc.gavin.grumman.zeta.util.JFinalConfig;
import cc.gavin.grumman.zeta.util.JsonDateValueProcessor;
import cc.gavin.grumman.zeta.validate.LoginValidator;
import cc.gavin.grumman.zeta.validate.UploadValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
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
        renderJson(query_report2());
    }//select materiel_type,sum(income_no) from storage_info group by materiel_type;

    private JSONObject query_report1() throws ExecutionException, InterruptedException {
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
        return msg;
    }


    private JSONObject query_report2(){

        long start_time = System.currentTimeMillis();






        JSONObject dataJson =  new JSONObject();

        dataJson.put("materielAmountByType",queryStorgeInfo());
        dataJson.put("totalPriceByStore",queryStoreStorgeInfo());

        dataJson.put("goodsStockOutPriceTrend",queryPPPOutGoingInfo());

        dataJson.put("allotMaterialPriceByType",queryDepartmentAllocationInfo());

        dataJson.put("shipStockOut",queryDistributionAllocationInfo());

        dataJson.put("stockInTotalPriceTrend",queryStorageInfo());



        JSONObject resultJson =  new JSONObject();

        resultJson.put("status","0");
        resultJson.put("data",dataJson);


        return resultJson;
    }

    private JSONObject queryStorgeInfo(){
        String sql = "select materiel_type,sum(income_no)  as income_no from storage_info group by materiel_type";
        List<Record> recordList =  Db.find(sql);
        List<String> x = new ArrayList<>();
        List<Long> y = new ArrayList<>();
        for(Record record:recordList){
            x.add(record.getStr("materiel_type"));
            y.add(record.getBigDecimal("income_no").longValue());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("xAxisData",x.toArray(new String[x.size()]));
        jsonObject.put("seriesData",y.toArray(new Long[y.size()]));
        return jsonObject;
    }

    private JSONObject queryStoreStorgeInfo(){
        String sql = "select store_name,sum(income_amount) as income_amount from store_storage_info group by store_name";
        List<Record> recordList =  Db.find(sql);
        JSONArray jsonArray = new JSONArray();
        for(Record record:recordList){
            JSONObject json = new JSONObject();
            json.put(record.getStr("store_name"),record.getBigDecimal("income_amount"));
            jsonArray.add(json);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("seriesData",jsonArray);
        return jsonObject;
    }

    private JSONObject queryPPPOutGoingInfo(){
        String sql = "select docket_time,sum(expenditure_amount) as expenditure_amount from ppp_outgoing_info group by docket_time";
        List<Record> recordList =  Db.find(sql);
        List<String> x = new ArrayList<>();
        List<Long> y = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
        for(Record record:recordList){
            x.add(dateFormat.format(record.getDate("docket_time")));
            y.add(record.getBigDecimal("expenditure_amount").longValue());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("xAxisData",x.toArray(new String[x.size()]));
        jsonObject.put("seriesData",y.toArray(new Long[y.size()]));
        return jsonObject;
    }


    private JSONObject queryDepartmentAllocationInfo(){
        String sql = "select docket_time,materiel_type,sum(expenditure_amount) as expenditure_amount from department_allocation_info " +
                "where length(expenditure_department)>0 " +
                "group by docket_time,materiel_type";
        List<Record> recordList =  Db.find(sql);
        Set<String> materielTypes = new HashSet<>();
        Set<String> docketTimes = new HashSet<>();

        Map<String,BigDecimal> ddAmountMap =  new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
        for(Record record:recordList){
            String docketTime = dateFormat.format(record.getDate("docket_time"));
            String materielType = record.getStr("materiel_type");
            docketTimes.add(docketTime);
            materielTypes.add(materielType);
            ddAmountMap.put(docketTime+"|"+materielType,record.getBigDecimal("expenditure_amount"));
        }
        List<String> setList= new ArrayList<String>(docketTimes);
        Collections.sort(setList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // TODO Auto-generated method stub

                return o1.toString().compareTo(o2.toString());
            }

        });
        docketTimes = new LinkedHashSet<String>(setList);//这里注意使用LinkedHashSet
        JSONArray jsonArray = new JSONArray();
        for(String materielType:materielTypes){
            BigDecimal[] args = new BigDecimal[docketTimes.size()];
            int i=0;
            for(String time:docketTimes){
                BigDecimal b = ddAmountMap.get(time+"|"+materielType);
                if(b==null){
                    args[i] = new BigDecimal(0);
                }else{
                    args[i] = b;
                }
                i++;
            }
            jsonArray.add(args);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("xAxisData",docketTimes.toArray(new String[docketTimes.size()]));
        jsonObject.put("typeName",materielTypes.toArray(new String[materielTypes.size()]));
        jsonObject.put("seriesData",jsonArray);
        return jsonObject;
    }

    private JSONObject queryDistributionAllocationInfo(){
        String sql = "select docket_time,distribution_department,sum(cost_amount) as cost_amount from distribution_allocation_info group by docket_time,distribution_department order by docket_time";
        List<Record> recordList =  Db.find(sql);
        Set<String> distributionDepartments = new HashSet<>();
        Set<String> docketTimes = new HashSet<>();

        Map<String,BigDecimal> ddAmountMap =  new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
        for(Record record:recordList){
            String docketTime = dateFormat.format(record.getDate("docket_time"));
            String distributionDepartment = record.getStr("distribution_department");
            docketTimes.add(docketTime);
            distributionDepartments.add(distributionDepartment);
            ddAmountMap.put(docketTime+"|"+distributionDepartment,record.getBigDecimal("cost_amount"));
        }
        List<String> setList= new ArrayList<String>(docketTimes);
        Collections.sort(setList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // TODO Auto-generated method stub

                return o1.toString().compareTo(o2.toString());
            }

        });
        docketTimes = new LinkedHashSet<String>(setList);//这里注意使用LinkedHashSet
        JSONArray jsonArray = new JSONArray();
        for(String department:distributionDepartments){
            BigDecimal[] args = new BigDecimal[docketTimes.size()];
            int i=0;
            for(String time:docketTimes){
                BigDecimal b = ddAmountMap.get(time+"|"+department);
                if(b==null){
                    args[i] = new BigDecimal(0);
                }else{
                    args[i] = b;
                }
                i++;
            }
            jsonArray.add(args);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("xAxisData",docketTimes.toArray(new String[docketTimes.size()]));
        jsonObject.put("storeName",distributionDepartments.toArray(new String[distributionDepartments.size()]));
        jsonObject.put("seriesData",jsonArray);
        return jsonObject;
    }


    private JSONObject queryStorageInfo(){
        String sql = "select docket_time,materiel_type,sum(income_amount) as income_amount from storage_info group by docket_time,materiel_type order by docket_time";
        List<Record> recordList =  Db.find(sql);
        Set<String> materielTypes = new HashSet<>();
        Set<String> docketTimes = new HashSet<>();

        Map<String,BigDecimal> ddAmountMap =  new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
        for(Record record:recordList){
            String docketTime = dateFormat.format(record.getDate("docket_time"));
            String materielType = record.getStr("materiel_type");
            docketTimes.add(docketTime);
            materielTypes.add(materielType);
            ddAmountMap.put(docketTime+"|"+materielType,record.getBigDecimal("income_amount"));
        }
        List<String> setList= new ArrayList<String>(docketTimes);
        Collections.sort(setList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // TODO Auto-generated method stub

                return o1.toString().compareTo(o2.toString());
            }

        });
        docketTimes = new LinkedHashSet<String>(setList);//这里注意使用LinkedHashSet
        JSONArray jsonArray = new JSONArray();
        for(String materielT:materielTypes){
            BigDecimal[] args = new BigDecimal[docketTimes.size()];
            int i=0;
            for(String time:docketTimes){
                BigDecimal b = ddAmountMap.get(time+"|"+materielT);
                if(b==null){
                    args[i] = new BigDecimal(0);
                }else{
                    args[i] = b;
                }
                i++;
            }
            jsonArray.add(args);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("xAxisData",docketTimes.toArray(new String[docketTimes.size()]));
        jsonObject.put("yAxisData",materielTypes.toArray(new String[materielTypes.size()]));
        jsonObject.put("seriesData",jsonArray);
        return jsonObject;
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


    /**
     * 分页查询门店入库信息
     */
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

        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        paramJson.put("materiel_types",Constants.storestorage.materiel_type);
        paramJson.put("suppliers",Constants.storestorage.supplier);
        paramJson.put("income_departments",Constants.storestorage.income_department);
        paramJson.put("expenditure_departments",Constants.storestorage.expenditure_department);
        resultJson.put("params",paramJson);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor());
        JSONObject pages =  JSONObject.fromObject(page,jsonConfig);
        resultJson.put("pages", pages);
        renderJson(resultJson);
    }

    /**
     * 分页查询入库信息
     */
    public void query_storage(){
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

        StringBuilder sb = new StringBuilder(" from storage_info where 1=1 ");
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

        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        paramJson.put("materiel_types",Constants.storage.materiel_type);
        paramJson.put("suppliers",Constants.storage.supplier);
        paramJson.put("income_departments",Constants.storage.income_department);
        paramJson.put("expenditure_departments",Constants.storage.expenditure_department);
        resultJson.put("params",paramJson);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor());
        JSONObject pages =  JSONObject.fromObject(page,jsonConfig);
        resultJson.put("pages", pages);
        renderJson(resultJson);
    }


    /**
     * 分页查询半成品入库信息
     */
    public void query_ppp_storage(){
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

        StringBuilder sb = new StringBuilder(" from ppp_storage_info where 1=1 ");
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

        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        paramJson.put("materiel_types",Constants.pppstorage.materiel_type);
        paramJson.put("suppliers",Constants.pppstorage.supplier);
        paramJson.put("income_departments",Constants.pppstorage.income_department);
        paramJson.put("expenditure_departments",Constants.pppstorage.expenditure_department);
        resultJson.put("params",paramJson);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor());
        JSONObject pages =  JSONObject.fromObject(page,jsonConfig);
        resultJson.put("pages", pages);
        renderJson(resultJson);
    }

    /**
     * 分页查询半成品出库信息
     */
    public void query_ppp_outgoing(){
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

        StringBuilder sb = new StringBuilder(" from ppp_outgoing_info where 1=1 ");
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

        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        paramJson.put("materiel_types",Constants.pppoutgoing.materiel_type);
        paramJson.put("suppliers",Constants.pppoutgoing.supplier);
        paramJson.put("income_departments",Constants.pppoutgoing.income_department);
        paramJson.put("expenditure_departments",Constants.pppoutgoing.expenditure_department);
        resultJson.put("params",paramJson);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor());
        JSONObject pages =  JSONObject.fromObject(page,jsonConfig);
        resultJson.put("pages", pages);
        renderJson(resultJson);
    }


    /**
     * 分页查询部门调拨信息
     */
    public void query_department_allocation(){
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

        StringBuilder sb = new StringBuilder(" from department_allocation_info where 1=1 ");
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

        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        paramJson.put("materiel_types",Constants.departmentallocation.materiel_type);
        paramJson.put("suppliers",Constants.departmentallocation.supplier);
        paramJson.put("income_departments",Constants.departmentallocation.income_department);
        paramJson.put("expenditure_departments",Constants.departmentallocation.expenditure_department);
        resultJson.put("params",paramJson);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor());
        JSONObject pages =  JSONObject.fromObject(page,jsonConfig);
        resultJson.put("pages", pages);
        renderJson(resultJson);
    }

    /**
     * 分页查询配送出库信息
     */
    public void query_distribution_allocation(){
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
        String distribution_department = getPara("distribution_department");
        String distribution_allocation = getPara("distribution_allocation");

        StringBuilder sb = new StringBuilder(" from distribution_allocation_info where 1=1 ");
        if(StringUtils.isNotBlank(materiel_type)){
            sb.append(" and materiel_type = '"+materiel_type+"' ");
        }
        if(StringUtils.isNotBlank(distribution_department)){
            sb.append(" and distribution_department = '"+distribution_department+"' ");
        }
        if(StringUtils.isNotBlank(distribution_allocation)){
            sb.append(" and distribution_allocation = '"+distribution_allocation+"' ");
        }

        Page<Record> page = Db.paginate(pageNumber,pageSize," select * ",sb.toString());

        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        paramJson.put("materiel_types",Constants.distributionallocation.materiel_type);
        paramJson.put("distribution_department",Constants.distributionallocation.distribution_department);
        paramJson.put("outgoing_department",Constants.distributionallocation.outgoing_department);
        resultJson.put("params",paramJson);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor());
        JSONObject pages =  JSONObject.fromObject(page,jsonConfig);
        resultJson.put("pages", pages);
        renderJson(resultJson);
    }


}
