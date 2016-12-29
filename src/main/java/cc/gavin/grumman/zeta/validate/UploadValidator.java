package cc.gavin.grumman.zeta.validate;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.jfinal.validate.Validator;
import org.apache.commons.lang.StringUtils;

import java.io.File;


/**
 * Created by user on 12/13/16.
 */
public class UploadValidator extends Validator {

    @Override
    protected void validate(Controller controller) {
        super.setShortCircuit(true);

        UploadFile uploadFile = controller.getFile("uploadFile");
        String fileName = uploadFile.getFileName();
        if(fileName.equals("")){
            addError("errorMsg","暂不支持07版excel");
        }
    }

    @Override
    protected void handleError(Controller controller) {
        controller.renderNull();
    }
}
