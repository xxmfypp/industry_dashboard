package cc.gavin.grumman.zeta.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
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
    public void intercept(ActionInvocation actionInvocation) {
        Controller controller = actionInvocation.getController();

        if(urlList.contains(actionInvocation.getActionKey())){
            actionInvocation.invoke();
            return;
        }
        Record record = controller.getSessionAttr("customerInfo");
        if ( record==null){
            controller.redirect("/");
        }else{
            actionInvocation.invoke();
        }

    }
}
