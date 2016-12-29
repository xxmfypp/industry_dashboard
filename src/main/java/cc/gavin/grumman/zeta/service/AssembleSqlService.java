package cc.gavin.grumman.zeta.service;


import cc.gavin.grumman.zeta.service.util.AssembleSql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Created by user on 12/29/16.
 */
public class AssembleSqlService  extends RecursiveTask<List<List<String>>> {

    private List<AssembleSql> assembleSqlList;

    public AssembleSqlService(List<AssembleSql> assembleSqlList){
        this.assembleSqlList = assembleSqlList;
    }



    @Override
    protected List<List<String>> compute() {
        if (assembleSqlList.size() == 1) {
            return Arrays.asList(assembleSqlList.get(0).getSqlList());
        }else{
            AssembleSqlService assembleSqlService1 =  new AssembleSqlService(assembleSqlList.subList(0, 1));
            AssembleSqlService assembleSqlService2 = new AssembleSqlService(assembleSqlList.subList(1, assembleSqlList.size()));
            assembleSqlService1.fork();
            assembleSqlService2.fork();
            List<List<String>> list = new ArrayList<>();
            list.addAll(assembleSqlService1.join());
            list.addAll(assembleSqlService2.join());
            return list;
        }
    }
}
