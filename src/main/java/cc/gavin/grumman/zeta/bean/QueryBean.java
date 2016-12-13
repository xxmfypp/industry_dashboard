package cc.gavin.grumman.zeta.bean;

/**
 * Created by user on 12/5/16.
 */
public class QueryBean {

    private String index;

    private String sql;

    private int xy;

    public QueryBean(String index,String sql,int xy){
        this.index = index;
        this.sql = sql;
        this.xy = xy;
    }


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getXy() {
        return xy;
    }

    public void setXy(int xy) {
        this.xy = xy;
    }
}
