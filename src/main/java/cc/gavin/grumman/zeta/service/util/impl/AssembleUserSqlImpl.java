package cc.gavin.grumman.zeta.service.util.impl;

import cc.gavin.grumman.zeta.service.util.AssembleSql;
import cc.gavin.grumman.zeta.util.ExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 12/29/16.
 */
public class AssembleUserSqlImpl implements AssembleSql{

    private XSSFSheet xssfSheet;

    public AssembleUserSqlImpl(XSSFSheet xssfSheet){
        this.xssfSheet = xssfSheet;
    }


    @Override
    public List<String> getSqlList() {
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  ExcelUtil.getIndexMap(new String[]{"用户名","性别","出生日期"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `user_info`(`birthday`, `username`, `gender`) values('"+ExcelUtil.getValue(xssfRow.getCell(map.get("出生日期")))+"', '"+ExcelUtil.getValue(xssfRow.getCell(map.get("用户名")))+"', '"+ExcelUtil.getValue(xssfRow.getCell(map.get("性别")))+"')";
                list_Sql.add(sql);
            }

        }
        return list_Sql;
    }
}
