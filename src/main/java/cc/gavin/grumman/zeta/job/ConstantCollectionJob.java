package cc.gavin.grumman.zeta.job;

import cc.gavin.grumman.zeta.service.CollectionConstatService;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by user on 12/29/16.
 */
public class ConstantCollectionJob implements Job {

    private Logger logger = Logger.getLogger(ConstantCollectionJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        CollectionConstatService.collection();

    }

}