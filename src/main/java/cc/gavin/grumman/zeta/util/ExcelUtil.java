package cc.gavin.grumman.zeta.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Created by xxmfypp on 11/29/16.
 */
public class ExcelUtil {

    public static void readXls(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    HSSFCell name = hssfRow.getCell(0);
                    HSSFCell passwd = hssfRow.getCell(1);
                    HSSFCell birthday = hssfRow.getCell(2);
                    HSSFCell sex = hssfRow.getCell(3);
                    System.out.println(getValue(name) + "_" + getValue(passwd) + "_" + getValue(birthday) + "_" + getValue(sex));
                }
            }
        }
    }


    public static Map<String,List<String>> readXlsx(File file) throws IOException {
        Map<String,List<String>> map = new HashMap<String,List<String>>();
        InputStream is = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);


        // Read the Sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            if(xssfSheet.getSheetName().equals("用户信息")){
                map.put("用户信息",getUserInfoSql(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("商品信息")){
                map.put("商品信息",getCommodityInfoSql(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("订单信息")){
                map.put("订单信息",getOrderInfoSql(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("收藏信息")){
                map.put("收藏信息",getCollectionInfoSql(xssfSheet));
            }
        }
        return map;
    }


    private  static List<String> getUserInfoSql(XSSFSheet xssfSheet){
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  getIndexMap(new String[]{"用户名","性别","出生日期"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `user_info`(`birthday`, `username`, `gender`) values('"+getValue(xssfRow.getCell(map.get("出生日期")))+"', '"+getValue(xssfRow.getCell(map.get("用户名")))+"', '"+getValue(xssfRow.getCell(map.get("性别")))+"')";
                list_Sql.add(sql);
            }

        }
        return list_Sql;
    }

    private  static List<String> getCommodityInfoSql(XSSFSheet xssfSheet){
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  getIndexMap(new String[]{"商品编号","商品名称","类别","商品价格"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `commodity_info`(`category`, `price`, `name`, `commodity_id`) values('"+getValue(xssfRow.getCell(map.get("类别")))+"', '"+getValue(xssfRow.getCell(map.get("商品价格")))+"', '"+getValue(xssfRow.getCell(map.get("商品名称")))+"', '"+getValue(xssfRow.getCell(map.get("商品编号")))+"')";
                list_Sql.add(sql);
            }
        }
        return list_Sql;
    }

    private  static List<String> getOrderInfoSql(XSSFSheet xssfSheet){
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  getIndexMap(new String[]{"订单编号","用户名","所属地区","订单金额","订单日期"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `order_info`(`amount`, `username`, `area`, `create_time`, `order_id`) values('"+getValue(xssfRow.getCell(map.get("订单金额")))+"', '"+getValue(xssfRow.getCell(map.get("用户名")))+"', '"+getValue(xssfRow.getCell(map.get("所属地区")))+"', '"+getValue(xssfRow.getCell(map.get("订单日期")))+"', '"+getValue(xssfRow.getCell(map.get("订单编号")))+"')";
                list_Sql.add(sql);
            }
        }
        return list_Sql;
    }

    private  static List<String> getCollectionInfoSql(XSSFSheet xssfSheet){
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  getIndexMap(new String[]{"用户名","商品编号","收藏日期"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `collection_info`(`username`, `create_time`, `commodity_id`) values('"+getValue(xssfRow.getCell(map.get("用户名")))+"', '"+getValue(xssfRow.getCell(map.get("收藏日期")))+"', '"+getValue(xssfRow.getCell(map.get("商品编号")))+"')";
                list_Sql.add(sql);
            }
        }
        return list_Sql;
    }


    private static String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getStringCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }


    private static String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            return String.valueOf(xssfRow.getRawValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    private static Map<String,Integer> getIndexMap(String[] param,XSSFRow xssfRow){
        Map<String,Integer> map  =  new HashMap<String,Integer>();
        for(int i=0;i<param.length;i++){
            for(int j=0;j<100;j++){
                XSSFCell cell =  xssfRow.getCell(j);
                if(cell==null){
                    break;
                }

                String value = getValue(cell);
                if(!StringUtils.isEmpty(value)){
                    if(value.equals(param[i])){
                        map.put(param[i],j);
                    }
                }else{
                    break;
                }
            }
        }
        return map;
    }

}


