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
public class AssembleStoreStorageSqlImpl  implements AssembleSql{

    private XSSFSheet xssfSheet;

    public AssembleStoreStorageSqlImpl(XSSFSheet xssfSheet){
        this.xssfSheet = xssfSheet;
    }


    @Override
    public List<String> getSqlList() {
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  ExcelUtil.getIndexMap(new String[]{"门店名称","单据日期","单据类型","单据编码","物料类别","物料及规格","单位","收料数量","发料数量","条只","单价","收入金额","发出金额","收料部门","发料部门","供应商"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `store_storage_info` (`store_name`, `docket_time`, `docket_type`,  `docket_code`, " +
                        " `materiel_type`,  `materiel_specifications`,  `unit`,  `income_no`,  `expenditure_no`,  `strip`,  " +
                        "`unit_price`,  `income_amount`,  `expenditure_amount`,  `income_department`,  `expenditure_department`,`supplier`) " +
                        "values('"+ExcelUtil.getValue(xssfRow.getCell(map.get("门店名称")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("单据日期")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("单据类型")))+"'" +
                        ",'"+ExcelUtil.getValue(xssfRow.getCell(map.get("单据编码")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("物料类别")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("物料及规格")))+"'" +
                        ",'"+ExcelUtil.getValue(xssfRow.getCell(map.get("单位")))+"','"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("收料数量"))))+"','"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("发料数量"))))+"'" +
                        ",'"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("条只"))))+"','"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("单价"))))+"','"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("收入金额"))))+"'" +
                        ",'"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("发出金额"))))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("收料部门")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("发料部门")))+"'" +
                        ",'"+ExcelUtil.getValue(xssfRow.getCell(map.get("供应商")))+"')";
                list_Sql.add(sql);
            }
        }
        return list_Sql;
    }
}
