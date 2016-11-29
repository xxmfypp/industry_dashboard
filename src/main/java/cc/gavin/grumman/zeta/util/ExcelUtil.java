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


    public static Map<String,List<Record>> readXlsx(File file) throws IOException {
        Map<String,List<Record>> map = new HashMap<String,List<Record>>();
        InputStream is = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);


        // Read the Sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            if(xssfSheet.getSheetName().equals("用户信息")){
                map.put("用户信息",getUserInfoRecord(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("商品信息")){
                map.put("商品信息",getCommodityInfoRecord(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("订单信息")){
                map.put("订单信息",getOrderInfoRecord(xssfSheet));
            }else if(xssfSheet.getSheetName().equals("收藏信息")){
                map.put("收藏信息",getCollectionInfoRecord(xssfSheet));
            }
        }
        return map;
    }


    private  static List<Record> getUserInfoRecord(XSSFSheet xssfSheet){
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  getIndexMap(new String[]{"用户名","性别","出生日期"},title_xssfRow);

        List<Record> list_Record = new ArrayList<Record>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                Record record = new Record();
                record.set("username",getValue(xssfRow.getCell(map.get("用户名"))));
                record.set("gender",getValue(xssfRow.getCell(map.get("性别"))));
                record.set("birthday",getValue(xssfRow.getCell(map.get("出生日期"))));
                list_Record.add(record);
            }
        }
        return list_Record;
    }

    private  static List<Record> getCommodityInfoRecord(XSSFSheet xssfSheet){
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  getIndexMap(new String[]{"商品编号","商品名称","类别","商品价格"},title_xssfRow);

        List<Record> list_Record = new ArrayList<Record>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                Record record = new Record();
                record.set("commodity_id",getValue(xssfRow.getCell(map.get("商品编号"))));
                record.set("name",getValue(xssfRow.getCell(map.get("商品名称"))));
                record.set("category",getValue(xssfRow.getCell(map.get("类别"))));
                record.set("price",getValue(xssfRow.getCell(map.get("商品价格"))));
                list_Record.add(record);
            }
        }
        return list_Record;
    }

    private  static List<Record> getOrderInfoRecord(XSSFSheet xssfSheet){
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  getIndexMap(new String[]{"订单编号","用户名","所属地区","订单金额","订单日期"},title_xssfRow);

        List<Record> list_Record = new ArrayList<Record>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                Record record = new Record();
                record.set("order_id",getValue(xssfRow.getCell(map.get("订单编号"))));
                record.set("username",getValue(xssfRow.getCell(map.get("用户名"))));
                record.set("area",getValue(xssfRow.getCell(map.get("所属地区"))));
                record.set("amount",getValue(xssfRow.getCell(map.get("订单金额"))));
                record.set("create_time",getValue(xssfRow.getCell(map.get("订单日期"))));
                list_Record.add(record);
            }
        }
        return list_Record;
    }

    private  static List<Record> getCollectionInfoRecord(XSSFSheet xssfSheet){
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  getIndexMap(new String[]{"用户名","商品编号","收藏日期"},title_xssfRow);

        List<Record> list_Record = new ArrayList<Record>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                Record record = new Record();
                record.set("username",getValue(xssfRow.getCell(map.get("用户名"))));
                record.set("commodity_id",getValue(xssfRow.getCell(map.get("商品编号"))));
                record.set("create_time",getValue(xssfRow.getCell(map.get("收藏日期"))));
                list_Record.add(record);
            }
        }
        return list_Record;
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


