package cc.gavin.grumman.zeta.util;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by user on 12/1/16.
 */
public interface DdUtil {

    public List<String> insert(List<Record> data);
}
