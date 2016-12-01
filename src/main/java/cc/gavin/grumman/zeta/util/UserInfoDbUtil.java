package cc.gavin.grumman.zeta.util;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/1/16.
 */
public class UserInfoDbUtil implements DdUtil {
    @Override
    public List<String> insert(List<Record> data) {
        List<String> errMessage = new ArrayList<String>();
        for(int i = 0;i<data.size();i++){

            try{
                Db.save("user_info","username",data.get(i));
            }catch (Exception e){
                StringBuilder sb =  new StringBuilder("[用户信息]");
                sb.append("  用户名:").append(data.get(i).getColumns().get("username"));
                sb.append("  性别:").append(data.get(i).getColumns().get("gender"));
                sb.append("  出生日期:").append(data.get(i).getColumns().get("birthday"));
                sb.append("  发生异常,异常原因：").append(e.getMessage());
                errMessage.add(sb.toString());
            }
        }
        return errMessage;
    }
}
