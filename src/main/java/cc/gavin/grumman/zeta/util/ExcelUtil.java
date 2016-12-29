package cc.gavin.grumman.zeta.util;


import cc.gavin.grumman.zeta.service.AssembleSqlService;
import cc.gavin.grumman.zeta.service.util.AssembleSql;
import cc.gavin.grumman.zeta.service.util.impl.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;


/**
 * Created by user on 11/29/16.
 */
public class ExcelUtil {

    private static DecimalFormat    df   = new DecimalFormat("######0.00");
    private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");


    public static List<List<String>> readXlsx1(File file) throws IOException, ExecutionException, InterruptedException {
        List<AssembleSql> lists = new ArrayList<>();
        InputStream is = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);


        // Read the Sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            if(xssfSheet.getSheetName().equals("用户信息")){
                lists.add(new AssembleUserSqlImpl(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("商品信息")){
                lists.add(new AssembleCommoditySqlImpl(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("订单信息")){
                lists.add(new AssembleOrderSqlImpl(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("收藏信息")){
                lists.add(new AssembleCollectionSqlImpl(xssfSheet));
            }
        }
        Future<List<List<String>>> future = JFinalConfig.fjp.submit(new AssembleSqlService(lists));
        return future.get();
    }

    public  static List<List<String>> readXlsx2(File file) throws IOException, ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(6);
        List<AssembleSql> lists = new ArrayList<>();

        InputStream is = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        // Read the Sheet
        long start = System.currentTimeMillis();
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            //门店入库单 入库单 半成品入库单 半成品出库单 部门调拨单 配送出库单
            if(xssfSheet.getSheetName().equals("门店入库单")){
                lists.add(new AssembleStoreStorageSqlImpl(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("入库单")){
                lists.add(new AssembleStorageSqlImpl(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("半成品入库单")){
                lists.add(new AssemblePPPStorageSqlImpl(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("半成品出库单")){
                lists.add(new AssemblePPPOutGoingSqlImpl(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("部门调拨单")){
                lists.add(new AssembleDepartmentAllocationSqlImpl(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("配送出库单")){
                lists.add(new AssembleDistributionAllocationSqlImpl(xssfSheet));
            }
        }
        Future<List<List<String>>> future = forkJoinPool.submit(new AssembleSqlService(lists));
        return future.get();
    }



    public static String getValue(XSSFCell xssfRow) {
        if(xssfRow==null){
            return "";
        }
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            //判断是日期类型
            if (HSSFDateUtil.isCellDateFormatted(xssfRow))
                return dateformat.format(DateUtil.getJavaDate(xssfRow.getNumericCellValue()));
            else{
                return df.format(xssfRow.getNumericCellValue());
            }
        }else{
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    public static Map<String,Integer> getIndexMap(String[] param,XSSFRow xssfRow){
        Map<String,Integer> map  =  new HashMap<String,Integer>();
        for(int i=0;i<param.length;i++){
            for(int j=0;j<100;j++){
                XSSFCell cell =  xssfRow.getCell(j);
                if(cell==null){
                    break;
                }

                String value = getValue(cell);
                if(!StringUtils.isEmpty(value)){
                    value = value.replaceAll("\\s+","").replaceAll(" ","");
                    if(value.equals(param[i])){
                        map.put(param[i],j);
                    }
                }else{
                    break;
                }
            }
            if(!map.containsKey(param[i])){
                System.out.println(param[i]);
            }
        }

        return map;
    }

    public static Object getInt(String param){
        if(StringUtils.isBlank(param)){
            return 0;
        }else{
            return param;
        }
    }










    public static void main(String[] args){
        try{
            readXlsx2(new File("/home/user/Desktop/info.xlsx"));
        }catch (Exception e){
           e.printStackTrace();
        }


    }

}


