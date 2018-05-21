import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SavePro {
    public static void main(String[] args){
//        ExcelProcess.writeExcelRandom();//生成输入数据，如有需要可取消注释
        AccidentPositionList accidentPositionList=ExcelProcess.readExcelToList(new File("input.xls"));//读取输入文件
        CarArrange carArrange=new CarArrange();
        carArrange.greedyAlgorithm(accidentPositionList);//主要算法入口
    }
}
