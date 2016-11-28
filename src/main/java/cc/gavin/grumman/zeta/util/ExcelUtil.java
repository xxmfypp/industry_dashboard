package cc.gavin.grumman.zeta.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * Created by xxmfypp on 11/29/16.
 */
public class ExcelUtil {

    public static void main(String[] args) throws IOException {
        String filePath = "/home/xxmfypp/Desktop/temp.xls";
        readXls(filePath);
    }

    private static void readXls(String filePath) throws IOException{
        InputStream is = new FileInputStream(filePath);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    HSSFCell name = hssfRow.getCell(0);
                    HSSFCell passwd = hssfRow.getCell(1);
                    HSSFCell birthday = hssfRow.getCell(2);
                    HSSFCell sex = hssfRow.getCell(3);
                    System.out.println(getValue(name) + "_" + getValue(passwd) + "_" + getValue(birthday) + "_" + getValue(sex));
                }
            }
        }
    }

    private static String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

}


