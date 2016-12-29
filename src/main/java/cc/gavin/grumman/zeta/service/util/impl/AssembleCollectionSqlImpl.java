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
public class AssembleCollectionSqlImpl implements AssembleSql{

    private XSSFSheet xssfSheet;

    public AssembleCollectionSqlImpl(XSSFSheet xssfSheet){
        this.xssfSheet = xssfSheet;
    }


    @Override
    public List<String> getSqlList() {
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  ExcelUtil.getIndexMap(new String[]{"用户名","商品编号","收藏日期"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `collection_info`(`username`, `create_time`, `commodity_id`) values('"+ExcelUtil.getValue(xssfRow.getCell(map.get("用户名")))+"', '"+ExcelUtil.getValue(xssfRow.getCell(map.get("收藏日期")))+"', '"+ExcelUtil.getValue(xssfRow.getCell(map.get("商品编号")))+"')";
                list_Sql.add(sql);
            }
        }
        return list_Sql;
    }
}
