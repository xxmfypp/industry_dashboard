package cc.gavin.grumman.zeta.validate;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.apache.commons.lang.StringUtils;


/**
 * Created by user on 12/13/16.
 */
public class LoginValidator extends Validator {

    @Override
    protected void validate(Controller controller) {
        super.setShortCircuit(true);
        String login_name = controller.getPara("login_name");
        String pwss = controller.getPara("pwss");
        if(StringUtils.isBlank(login_name)){
            addError("errorMsg","登录名不能为空");
        }else if(StringUtils.isBlank(pwss)){
            addError("errorMsg","密码不能为空");
        }
    }

    @Override
    protected void handleError(Controller controller) {
        controller.keepPara("login_name");
        controller.renderJsp("login.jsp");
    }
}
