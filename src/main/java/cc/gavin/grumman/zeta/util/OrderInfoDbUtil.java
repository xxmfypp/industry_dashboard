package cc.gavin.grumman.zeta.util;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/1/16.
 */
public class OrderInfoDbUtil implements DdUtil {
    @Override
    public List<String> insert(List<Record> data) {

        List<String> errMessage = new ArrayList<String>();
        for(int i = 0;i<data.size();i++){

            try{
                Db.save("order_info","order_id",data.get(i));
            }catch (Exception e){
                StringBuilder sb =  new StringBuilder("[订单信息]");
                sb.append("  订单编号:").append(data.get(i).getColumns().get("order_id"));
                sb.append("  用户名:").append(data.get(i).getColumns().get("username"));
                sb.append("  所属地区:").append(data.get(i).getColumns().get("area"));
                sb.append("  订单金额:").append(data.get(i).getColumns().get("amount"));
                sb.append("  订单日期:").append(data.get(i).getColumns().get("create_time"));
                sb.append("  发生异常,异常原因：").append(e.getMessage());
                errMessage.add(sb.toString());
            }
        }
        return errMessage;
    }
}
