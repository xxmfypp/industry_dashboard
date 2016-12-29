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
public class AssembleCommoditySqlImpl implements AssembleSql{

    private XSSFSheet xssfSheet;

    public AssembleCommoditySqlImpl(XSSFSheet xssfSheet){
        this.xssfSheet = xssfSheet;
    }


    @Override
    public List<String> getSqlList() {
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  ExcelUtil.getIndexMap(new String[]{"商品编号","商品名称","类别","商品价格"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `commodity_info`(`category`, `price`, `name`, `commodity_id`) values('"+ExcelUtil.getValue(xssfRow.getCell(map.get("类别")))+"', '"+ExcelUtil.getValue(xssfRow.getCell(map.get("商品价格")))+"', '"+ExcelUtil.getValue(xssfRow.getCell(map.get("商品名称")))+"', '"+ExcelUtil.getValue(xssfRow.getCell(map.get("商品编号")))+"')";
                list_Sql.add(sql);
            }
        }
        return list_Sql;
    }
}
