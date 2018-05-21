import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

public class ExcelProcess {
    //一下几个参数表示8-10 18-20点的额外发生的事故数
    public static int xRestrictSmall = 55;
    public static int xRestrictBig = 60;
    public static int yRestrictSmall = 40;
    public static int yRestrictBig = 60;

    public static String inputFileName="input.xls";//输入xls完整路径
    public static String sheetName="sheet";//sheet名
    public static String outputFileName="output.xls";//输出xls完整路径

    public static Date randomDate(Date beginDate, Date endDate) {
        try {
            long date = 0;
            if (beginDate.getTime() < endDate.getTime()) {
                date = (long) ((Math.random() * (endDate.getTime() - beginDate.getTime())) + beginDate.getTime());
            } else {
                date = (long) ((Math.random() * (beginDate.getTime() - endDate.getTime())) + endDate.getTime());
            }
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Calendar randomDate(Calendar beginDate, Calendar endDate) {
        try {
            long date = 0;
            if (beginDate.getTime().getTime() < endDate.getTime().getTime()) {
                date = (long) ((Math.random() * (endDate.getTime().getTime() - beginDate.getTime().getTime())) + beginDate.getTime().getTime());
            } else {
                date = (long) ((Math.random() * (beginDate.getTime().getTime() - endDate.getTime().getTime())) + endDate.getTime().getTime());
            }
            Calendar calendar = Calendar.getInstance();
            Date newDay = new Date(date);
            calendar.setTime(newDay);
            return calendar;
//            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     *
     * @param dayLen：时间长度
     * @return 生成乱序时间序列，因此需要排序（贪心算法需求）
     */
    public static AccidentPositionList newRandomAccPosList(int dayLen) {
        AccidentPositionList accidentPositionList = new AccidentPositionList();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDay = format.parse(format.format(new Date()));//今天开始第一天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beginDay);
            Calendar later = Calendar.getInstance();
            later.setTime(beginDay);

            for (int i = 0; i < dayLen * 84; i++) {
                int extraCondition = 0;
                int accidentNum = 0;//表示发生是事故数
                if (calendar.get(Calendar.HOUR_OF_DAY) < 8) {
                    //0-8
                    later.add(Calendar.HOUR, 1);//每次加一个小时
                    accidentNum = 2;
                } else if (calendar.get(Calendar.HOUR_OF_DAY) < 20) {
                    later.add(Calendar.MINUTE, 10);//每次加十分钟
                    accidentNum = 3;
                    int temp = calendar.get(Calendar.HOUR_OF_DAY);
                    if (temp < 10 || temp >= 18) {
                        extraCondition = 1;//表示限制情况
                    }
                    //3/10min
                } else {
                    //21-24
                    later.add(Calendar.HOUR, 1);
                    accidentNum = 2;
                    //2/h
                }
                ExcelProcess.getRandomDataToList(calendar, later, accidentNum, accidentPositionList, extraCondition);//更新数据表，插入随机生成的时间和坐标
//                for (int j = 0; j < accidentNum; j++) {
//                    int posX = (int) (Math.random() * 101);
//                    int posY = (int) (Math.random() * 101);
//                    Date dateH = ExcelProcess.randomDate(calendar.getTime(), later.getTime());
//                    accidentPositionList.expendList(new AccidentPosition(posX, posY, dateH));
//                }
                calendar.setTime(later.getTime());//更新时间参数
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accidentPositionList;
    }

    /***
     *
     * @param begin：开始时刻
     * @param later：结束时刻
     * @param accidentNum：发生是事故数
     * @param accidentPositionList：事故时间list
     * @param extraCondition：限制情况 1表示有限制区域
     */
    public static void getRandomDataToList(Calendar begin, Calendar later, int accidentNum, AccidentPositionList accidentPositionList, int extraCondition) {
        if (accidentNum == 2) {//2起
            Calendar date1 = ExcelProcess.randomDate(begin, later);
            Calendar date2 = ExcelProcess.randomDate(begin, later);
            accidentPositionList.expendList(new AccidentPosition((int) (Math.random() * 101), (int) (Math.random() * 101), date1));
            accidentPositionList.expendList(new AccidentPosition((int) (Math.random() * 101), (int) (Math.random() * 101), date2));
        } else {//3起
            Calendar date1 = ExcelProcess.randomDate(begin, later);
            Calendar date2 = ExcelProcess.randomDate(begin, later);
            Calendar date3 = ExcelProcess.randomDate(begin, later);
            accidentPositionList.expendList(new AccidentPosition((int) (Math.random() * 101), (int) (Math.random() * 101), date1));
            accidentPositionList.expendList(new AccidentPosition((int) (Math.random() * 101), (int) (Math.random() * 101), date2));
            accidentPositionList.expendList(new AccidentPosition((int) (Math.random() * 101), (int) (Math.random() * 101), date3));
            if (extraCondition == 1) {
                for (int i = 0; i < 3; i++) {
                    accidentPositionList.expendList(new AccidentPosition((int) (Math.random() * (xRestrictBig - xRestrictSmall + 1) + xRestrictSmall), (int) (Math.random() * (yRestrictBig - yRestrictSmall + 1) + yRestrictSmall), ExcelProcess.randomDate(begin, later)));
                }
            }
        }
    }

    /***
     * 该函数完成了生成测试数据
     */
    public static void writeExcelRandom() {
        AccidentPositionList accidentPositionList = ExcelProcess.newRandomAccPosList(30);
        accidentPositionList.sort();//需要将得到的数据进行排序
//        accidentPositionList.printString();
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(
                    inputFileName));//
            // 生成名为“sheet1”的工作表，参数0表示这是第一页
            WritableSheet sheet = book.createSheet(sheetName, 0);
            //DateTime x y
            Label label = new Label(0, 0, "DateTime");
            sheet.addCell(label);
            label = new Label(1, 0, "X");
            sheet.addCell(label);
            label = new Label(2, 0, "Y");
            sheet.addCell(label);
            for (int i = 0; i < accidentPositionList.length; i++) {
                Number numberX = new Number(1, i + 1, accidentPositionList.getListX(i));
                Number numberY = new Number(2, i + 1, accidentPositionList.getListY(i));
                label = new Label(0, i + 1, accidentPositionList.gerListDate(i));
//                DateTime dtime = new DateTime(0, i+1, accidentPositionList.getListDate(i));
                sheet.addCell(numberX);
                sheet.addCell(numberY);
                sheet.addCell(label);
            }
            // 写入数据并关闭文件
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void writeExcels(List<fromCarlistToExcel> totalOutput) throws Exception{
        int day=0;
        WritableWorkbook book = Workbook.createWorkbook(new File(outputFileName));
        WritableSheet sheet = book.createSheet(sheetName, 0);
        for(fromCarlistToExcel outputLists:totalOutput){
//            writeExcel(book,outputLists.bestList,outputLists.carNumber,outputLists.dateInfo,number);
            Label temp=new Label(0+day,0,"totalCarNum");
            sheet.addCell(temp);
            temp=new Label(1+day,0,outputLists.carNumber+"");
            sheet.addCell(temp);

            Label label = new Label(1+day, 3, "Start Position");
            sheet.addCell(label);
            label = new Label(2+day, 3, "End Position");
            sheet.addCell(label);
            label = new Label(3+day, 3, "Move Distance");
            sheet.addCell(label);
            label = new Label(0+day, 3, "Car Number");
            sheet.addCell(label);
            label = new Label(4+day, 3, "Waiting Time");
            sheet.addCell(label);
            label = new Label(5+day, 3, "Happend Time");
            sheet.addCell(label);
            int tempNum = 4;
            for (OutputList outputList : outputLists.bestList) {
                label = new Label(0+day, tempNum, "" + outputList.carNum);
                sheet.addCell(label);
                label = new Label(1+day, tempNum, outputList.xStart + "," + outputList.yStart);
                sheet.addCell(label);
                label = new Label(2+day, tempNum, outputList.xEnd + "," + outputList.yEnd);
                sheet.addCell(label);
                label = new Label(3+day, tempNum, "" + outputList.moveDistance);
                sheet.addCell(label);
                label = new Label(4+day, tempNum, "" + outputList.waitTime);
                sheet.addCell(label);
                label = new Label(5+day, tempNum, "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(outputList.happendTime));
                sheet.addCell(label);
                tempNum++;
            }
            long totalWaitTime=0;
            int totalMoveDistance=0;
            for (OutputList outputList : outputLists.bestList){
                totalMoveDistance+=outputList.moveDistance;
                totalWaitTime+=outputList.waitTime;
            }
            System.out.println((outputLists.carNumber*100000+totalMoveDistance*0.5)*0.382+totalWaitTime/outputLists.carNumber*0.618+","+outputLists.carNumber);
            label = new Label(0+day, 1, "totalWaitTime");
            sheet.addCell(label);
            label = new Label(0+day, 2, "totalMoveDistance");
            sheet.addCell(label);
            label = new Label(1+day, 1, totalWaitTime+"ms");
            sheet.addCell(label);
            label = new Label(1+day, 2, totalMoveDistance+"km");
            sheet.addCell(label);
            day+=8;
        }
        book.write();
        book.close();
    }
    /***
     *
     * @param outputLists:输出list，存储的是车辆安排信息
     */
    public static void writeExcel(WritableWorkbook book,List<OutputList> outputLists,int carNumber,String dates,int day) {
        try {
//            WritableWorkbook book = Workbook.createWorkbook(new File(
//                    outputFileName));
            WritableSheet sheet = book.createSheet(sheetName, 0);
            //DateTime x y
            Label temp=new Label(0+day,0,"totalCarNum");
            sheet.addCell(temp);
            temp=new Label(1+day,0,carNumber+"");
            sheet.addCell(temp);

            Label label = new Label(1+day, 3, "Start Position");
            sheet.addCell(label);
            label = new Label(2+day, 3, "End Position");
            sheet.addCell(label);
            label = new Label(3+day, 3, "Move Distance");
            sheet.addCell(label);
            label = new Label(0+day, 3, "Car Number");
            sheet.addCell(label);
            label = new Label(4+day, 3, "Waiting Time");
            sheet.addCell(label);
            label = new Label(5+day, 3, "Happend Time");
            sheet.addCell(label);
            int tempNum = 4;
            for (OutputList outputList : outputLists) {
                label = new Label(0+day, tempNum, "" + outputList.carNum);
                sheet.addCell(label);
                label = new Label(1+day, tempNum, outputList.xStart + "," + outputList.yStart);
                sheet.addCell(label);
                label = new Label(2+day, tempNum, outputList.xEnd + "," + outputList.yEnd);
                sheet.addCell(label);
                label = new Label(3+day, tempNum, "" + outputList.moveDistance);
                sheet.addCell(label);
                label = new Label(4+day, tempNum, "" + outputList.waitTime);
                sheet.addCell(label);
                label = new Label(5+day, tempNum, "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(outputList.happendTime));
                sheet.addCell(label);
                tempNum++;
            }
//            for (int i = 0; i < outputLists.length; i++) {
//                Number numberX = new Number(1, i + 1, accidentPositionList.getListX(i));
//                Number numberY = new Number(2, i + 1, accidentPositionList.getListY(i));
//                DateTime dtime = new DateTime(0, i + 1, accidentPositionList.getListDate(i).getTime());
//                sheet.addCell(numberX);
//                sheet.addCell(numberY);
//                sheet.addCell(dtime);
//            }
            // 写入数据并关闭文件
            book.write();
//            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void writeExcel(AccidentPositionList accidentPositionList) {
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(
                    inputFileName));
            // 生成名为“sheet1”的工作表，参数0表示这是第一页
            WritableSheet sheet = book.createSheet(sheetName, 0);
            //DateTime x y
            Label label = new Label(0, 0, "DateTime");
            sheet.addCell(label);
            label = new Label(1, 0, "X");
            sheet.addCell(label);
            label = new Label(2, 0, "Y");
            sheet.addCell(label);
            for (int i = 0; i < accidentPositionList.length; i++) {
                Number numberX = new Number(1, i + 1, accidentPositionList.getListX(i));
                Number numberY = new Number(2, i + 1, accidentPositionList.getListY(i));
                DateTime dtime = new DateTime(0, i + 1, accidentPositionList.getListDate(i).getTime());
                sheet.addCell(numberX);
                sheet.addCell(numberY);
                sheet.addCell(dtime);
            }
            // 写入数据并关闭文件
            book.write();
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void readExcel(File file) {
        try {
            InputStream is = new FileInputStream(file.getAbsolutePath());
            Workbook wb = Workbook.getWorkbook(is);
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                Sheet sheet = wb.getSheet(index);
                for (int i = 0; i < sheet.getRows(); i++) {
                    for (int j = 0; j < sheet.getColumns(); j++) {
                        String cellinfo = sheet.getCell(j, i).getContents();
                        System.out.println(cellinfo);
                    }
                }
            }
            is.close();
            wb.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AccidentPositionList readExcelToList(File file) {
        try {
            AccidentPositionList accidentPositionList = new AccidentPositionList();
            InputStream is = new FileInputStream(file.getAbsolutePath());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Workbook wb = Workbook.getWorkbook(is);
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                Sheet sheet = wb.getSheet(index);
                for (int i = 1; i < sheet.getRows(); i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(format.parse(sheet.getCell(0, i).getContents()));
                    AccidentPosition accidentPosition = new AccidentPosition(Integer.parseInt(sheet.getCell(1, i).getContents()), Integer.parseInt(sheet.getCell(2, i).getContents()), calendar);
                    accidentPositionList.expendList(accidentPosition);
                }
            }
            is.close();
            wb.close();
            return accidentPositionList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
