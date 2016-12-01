package cc.gavin.grumman.zeta.service;

import cc.gavin.grumman.zeta.util.DdUtil;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Created by user on 12/1/16.
 */
public class InsertService extends RecursiveTask<List<String>> {


    private DdUtil ddUtil;

    private List<Record> data;


    public InsertService(DdUtil ddUtil,List<Record> data){
        this.ddUtil = ddUtil;
        this.data = data;
    }




    @Override
    protected List<String> compute() {
        return ddUtil.insert(data);
    }
}
