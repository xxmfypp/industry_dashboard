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
public class AssembleDistributionAllocationSqlImpl implements AssembleSql{

    private XSSFSheet xssfSheet;

    public AssembleDistributionAllocationSqlImpl(XSSFSheet xssfSheet){
        this.xssfSheet = xssfSheet;
    }


    @Override
    public List<String> getSqlList() {
        XSSFRow title_xssfRow = xssfSheet.getRow(0);

        Map<String,Integer> map  =  ExcelUtil.getIndexMap(new String[]{"单据日期","物料","规格","物料类别","物料规格","单位","数量","条只","成本单价","销售单价","成本金额","销售金额","出库部门","配送门店"},title_xssfRow);

        List<String> list_Sql = new ArrayList<String>();

        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                String sql = "insert into `distribution_allocation_info` ( `docket_time`,`materiel`,`specifications`," +
                        "`materiel_type`,`materiel_specifications`,`unit`,`num` ,`strip`,`cost_unit_price`," +
                        "`sale_unit_price`,`cost_amount`,`sale_amount`,`outgoing_department`,`distribution_department`) " +
                        "values('"+ExcelUtil.getValue(xssfRow.getCell(map.get("单据日期")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("物料")))+"'" +
                        ",'"+ExcelUtil.getValue(xssfRow.getCell(map.get("规格")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("物料类别")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("物料规格")))+"'" +
                        ",'"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("单位"))))+"','"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("数量"))))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("条只")))+"'" +
                        ",'"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("成本单价"))))+"','"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("销售单价"))))+"','"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("成本金额"))))+"'" +
                        ",'"+ExcelUtil.getInt(ExcelUtil.getValue(xssfRow.getCell(map.get("销售金额"))))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("出库部门")))+"','"+ExcelUtil.getValue(xssfRow.getCell(map.get("配送门店")))+"')";
                list_Sql.add(sql);
            }
        }
        return list_Sql;
    }
}
