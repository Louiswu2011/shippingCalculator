import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;

import java.io.*;

public class Calculator {

    public void calculate(String priceBookLocation, String dataBookLocation, Boolean isWinterRule, int row) {
        int priceRowCount = 9;

        System.out.println("Program starting up...");
        System.out.println("Opening price workbook...");

        FileInputStream priceFis = null;
        Workbook priceBook;

        FileInputStream dataFis;
        Workbook dataBook = null;

        String[] regionInfo = {"广东","福建","广西","海南","湖南","湖北","江西","安徽","江苏","浙江","上海","河南","河北","山西","四川","陕西","山东","重庆","云南","贵州","天津","辽宁","吉林","黑龙江","甘肃","宁夏","内蒙古","青海","新疆","西藏","北京"};
        double[][] priceInfo = new double[7][24];

        int rowCount = row;

        // String test = "广西壮族自治区";
        // System.out.println(test.contains(regionInfo[2]));

        // 读取运费信息
        try {
            priceFis = new FileInputStream(priceBookLocation);
            priceBook = WorkbookFactory.create(priceFis);

            System.out.println("Success");

            System.out.println("Reading the price info...");
            Sheet priceSheet = priceBook.getSheetAt(0);
            for(int i = 3; i <=23; i++){                                    // j 是区 ， i 是重量区间
                Row priceRow = priceSheet.getRow(i);
                for(int j = 1; j <= 6; j++){
                    Cell priceCell = priceRow.getCell(j);
                    priceInfo[j][i] = priceCell.getNumericCellValue();
                    // System.out.println(priceInfo[j][i]);
                }
            }
            System.out.println("Successfully read the price info");
            // System.out.println(priceInfo[1][3]);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closing the price workbook...");
            try {
                priceFis.close();
                System.out.println("Successfully closed the price workbook");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 读取数据，判断所属区域并计算运费
        System.out.println("Opening the data workbook...");
        try {
            dataFis = new FileInputStream(dataBookLocation);
            dataBook = WorkbookFactory.create(dataFis);

            CellStyle redStyle = dataBook.createCellStyle();
            redStyle.setFillForegroundColor(IndexedColors.RED.index);
            redStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            // redStyle.setFillForegroundColor(IndexedColors.DARK_RED.index);

            // Font redFont = dataBook.createFont();
            // redFont.setColor(IndexedColors.DARK_RED.getIndex());

            System.out.println("Success");

            System.out.println("Reading the data info...");
            Sheet dataSheet = dataBook.getSheetAt(0);
            Cell resultCell;
            int regionCode;

            // 判断区域并记录区域代码
            for(int i = 4; i <= rowCount; i++){
                Row dataRow = dataSheet.getRow(i);
                Cell dataCell = dataRow.getCell(3);
                String regionText = dataCell.getStringCellValue();
                int regionCount = 0;
                for(String region : regionInfo){
                    if(regionText.contains(region)){
                        regionCount++;
                        break;
                    }else{
                        regionCount++;
                    }
                }
                if(regionCount == 1){
                    regionCode = 1;
                }else if(regionCount > 1 && regionCount <= 7){
                    regionCode = 2;
                }else if(regionCount > 7 && regionCount <= 20){
                    regionCode = 3;
                }else if(regionCount > 20 && regionCount <= 27){
                    regionCode = 4;
                }else if(regionCount > 27 && regionCount <= 30){
                    regionCode = 5;
                }else if(regionCount == 31){
                    regionCode = 6;
                }else{
                    regionCode = -1;
                }
                Row weightRow = dataSheet.getRow(i);
                Cell weightInfo = weightRow.getCell(6);
                Cell specialWeightInfo = weightRow.getCell(5);
                double weight;
                // 检查是否计算计泡重量
                if(specialWeightInfo.getNumericCellValue() != 0 && specialWeightInfo.getNumericCellValue() > weightInfo.getNumericCellValue()){
                    weight = specialWeightInfo.getNumericCellValue() / 1000;
                }else{
                    weight = weightInfo.getNumericCellValue() / 1000;
                }

                // 匹配重量所对应的运费区间 并计算运费
                double weightRounded;
                double result = 0;
                // System.out.println(regionCode);
                if(weight <= 0.5){
                    // weightRounded = 1;
                    result = priceInfo[regionCode][3];
                    weightRounded = 0.5;
                }else{
                    weightRounded = Math.ceil(weight);
                    if(weightRounded <= 20){
                        result = priceInfo[regionCode][(int)weightRounded + 3];


                    }
                }

                // 冬季邮政
                if(isWinterRule){
                    if(regionCount == 29 || regionCount == 30){
                        if(weightRounded <= 5){
                            result += 2;
                        }else if(weightRounded > 5 && weightRounded <= 20){
                            result += 2.5;
                        }
                    }else if(regionCount >= 22 && regionCount <= 28){
                        if(weightRounded <= 5){
                            result += 1;
                        }else if(weightRounded >5 && weightRounded <= 20){
                            result += 1.6;
                        }
                    }
                }

                resultCell = weightRow.createCell(priceRowCount);
                // Cell regionCodeCell = weightRow.createCell(10);
                // Cell regionCountCell = weightRow.createCell(12);

                if(result == 0){
                    resultCell.setCellValue(result);
                    resultCell.setCellStyle(redStyle);
                    System.out.println("Find zero price!");
                }else{
                    resultCell.setCellValue(result);
                }

                // regionCodeCell.setCellValue(regionCode);
                // regionCountCell.setCellValue(regionCount);
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Saving the data workbook...");
            try {
                priceFis.close();
                FileOutputStream dataOut = new FileOutputStream(new File(dataBookLocation));
                dataBook.write(dataOut);
                dataOut.close();
                System.out.println("Successfully saved the data workbook");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public int detectInfoCount(String dataFileLocation){
        Workbook wb;
        Row wbRow;
        Cell wbCell;
        int rowCount = 4;
        try {
            FileInputStream data = new FileInputStream(dataFileLocation);
            wb = WorkbookFactory.create(data);
            Sheet wbSheet = wb.getSheetAt(0);
            try{
                while(true){
                    wbRow = wbSheet.getRow(rowCount);
                    wbCell = wbRow.getCell(0);
                    if(wbCell.getNumericCellValue() != 0){
                        rowCount++;
                    }else{
                        return rowCount - 1;
                    }
                }
            } catch (NullPointerException e){
                return rowCount - 1;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            return -2;
        }

    }
}
