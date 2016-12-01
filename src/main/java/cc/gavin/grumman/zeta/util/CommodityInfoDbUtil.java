package cc.gavin.grumman.zeta.util;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/1/16.
 */
public class CommodityInfoDbUtil implements DdUtil {
    @Override
    public List<String> insert(List<Record> data) {

        List<String> errMessage = new ArrayList<String>();

        for(int i = 0;i<data.size();i++){

            try{
                Db.save("commodity_info","commodity_id",data.get(i));
            }catch (Exception e){
                StringBuilder sb =  new StringBuilder("[商品信息]");
                sb.append("  商品编号:").append(data.get(i).getColumns().get("commodity_id"));
                sb.append("  商品名称:").append(data.get(i).getColumns().get("name"));
                sb.append("  类别:").append(data.get(i).getColumns().get("category"));
                sb.append("  商品价格:").append(data.get(i).getColumns().get("price"));
                sb.append("  发生异常,异常原因：").append(e.getMessage());
                errMessage.add(sb.toString());
            }
        }
        return errMessage;
    }
}
