package cc.gavin.grumman.zeta.service;

import com.jfinal.plugin.activerecord.Db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Created by user on 12/1/16.
 */
public class InsertService extends RecursiveTask<List<String>> {


    private List<String> data;


    public InsertService(List<String> data){
        this.data = data;
    }

    private List<String> insert(List<String> data) {
        List<String> errMessage = new ArrayList<String>();
        try{
            Db.batch(data,data.size());
        }catch (Exception e){
            errMessage.add(e.getMessage());
        }

        return errMessage;
    }




    @Override
    protected List<String> compute() {
        return insert(data);
    }
}
