package cc.gavin.grumman.zeta.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Created by user on 11/30/16.
 */
public class QueryService extends RecursiveTask<JSON> {


    private String sql;

    /**
     * xy 为0 取纵列 为1 取横列
     */
    private int xy;

    public QueryService(String sql,int xy){
        this.sql = sql;
        this.xy = xy;
    }

    @Override
    protected JSON compute() {

        List<Record> records = Db.find(sql);

        if(xy==0){
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<records.size();i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",records.get(i).getColumnValues()[0].toString());
                jsonObject.put("value",records.get(i).getColumnValues()[1]);
                jsonArray.add(jsonObject) ;
            }
            return jsonArray;
        }else{
            List<String> xAxisDataList = new ArrayList<String>();
            List<Object> seriesDataList = new ArrayList<Object>();
            for(int i=0;i<records.size();i++){
                xAxisDataList.add(records.get(i).getColumnValues()[0].toString());
                seriesDataList.add(records.get(i).getColumnValues()[1]);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("xAxisData",xAxisDataList.toArray(new String [xAxisDataList.size()]));
            jsonObject.put("seriesData",seriesDataList.toArray(new Object [seriesDataList.size()]));
            return jsonObject;
        }
    }
}
