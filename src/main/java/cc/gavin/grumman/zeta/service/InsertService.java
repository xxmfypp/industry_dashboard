package cc.gavin.grumman.zeta.service;

import com.jfinal.plugin.activerecord.Db;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * Created by user on 12/1/16.
 */
public class InsertService extends RecursiveAction {


    private List<List<String>> sqlList;


    public InsertService(List<List<String>> sqlList){
        this.sqlList = sqlList;
    }

    @Override
    protected void compute() {
        if (sqlList.size() == 1) {
            Db.batch(sqlList.get(0),sqlList.get(0).size());
        }else{
            InsertService insertService1 =  new InsertService(sqlList.subList(0, 1));
            InsertService insertService2 = new InsertService(sqlList.subList(1, sqlList.size()));
            insertService1.fork();
            insertService2.fork();
            insertService1.join();
            insertService2.join();
        }
    }
}
