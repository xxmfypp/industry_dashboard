package cc.gavin.grumman.zeta.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 12/13/16.
 */
public class SessionInterceptor implements Interceptor {

    private static List<String> urlList = Arrays.asList("/","/login","/logout");


    @Override
    public void intercept(Invocation invocation) {
        Controller controller = invocation.getController();

        if(urlList.contains(invocation.getActionKey())){
            invocation.invoke();
            return;
        }
        Record record = controller.getSessionAttr("customerInfo");
        if ( record==null){
            //controller.redirect("/");
            invocation.invoke();
        }else{
            invocation.invoke();
        }
    }
}
