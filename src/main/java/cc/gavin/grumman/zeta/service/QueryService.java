package cc.gavin.grumman.zeta.service;

import cc.gavin.grumman.zeta.bean.QueryBean;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * Created by user on 11/30/16.
 */
public class QueryService extends RecursiveTask<JSONObject> {


    private List<QueryBean> list;


    public QueryService(List<QueryBean> list){
        this.list = list;
    }

    @Override
    protected JSONObject compute() {

        if (list.size() == 1) {
            JSONObject jsonResult = new JSONObject();
            List<Record> records = Db.find(list.get(0).getSql());
            if (list.get(0).getXy() == 0) {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < records.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", records.get(i).getColumnValues()[0].toString());
                    jsonObject.put("value", records.get(i).getColumnValues()[1]);
                    jsonArray.add(jsonObject);
                }
                jsonResult.put(list.get(0).getIndex(), jsonArray);
                return jsonResult;
            } else {
                List<String> xAxisDataList = new ArrayList<String>();
                List<Object> seriesDataList = new ArrayList<Object>();
                for (int i = 0; i < records.size(); i++) {
                    xAxisDataList.add(records.get(i).getColumnValues()[0].toString());
                    seriesDataList.add(records.get(i).getColumnValues()[1]);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("xAxisData", xAxisDataList.toArray(new String[xAxisDataList.size()]));
                jsonObject.put("seriesData", seriesDataList.toArray(new Object[seriesDataList.size()]));
                jsonResult.put(list.get(0).getIndex(), jsonObject);
                return jsonResult;
            }
        } else {
            QueryService queryService1 =  new QueryService(list.subList(0, 1));
            QueryService queryService2 = new QueryService(list.subList(1, list.size()));
            queryService1.fork();
            queryService2.fork();
            JSONObject json1 = queryService1.join();
            JSONObject json2 = queryService2.join();
            json1.putAll(json2);
            return json1;

        }
    }





}
