import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AccidentPositionList {
    int length = 0;
    List<AccidentPosition> accPosList;

    public AccidentPositionList() {
        accPosList = new ArrayList();
    }

    //将时间序列进行排序
    public void sort() {
        accPosList.sort((o1, o2) -> (int) (o1.accidentTime.getTime().getTime() - o2.accidentTime.getTime().getTime()));
    }

    /***
     * 限制区域
     * @param xRestrictSmall
     * @param xRestrictBig
     * @param yRestrictSmall
     * @param yRestrictBig
     */
    public void modifiedTheData(int xRestrictSmall, int xRestrictBig, int yRestrictSmall, int yRestrictBig) {
        for (AccidentPosition accidentPosition : accPosList) {
            int temp = accidentPosition.accidentTime.get(Calendar.HOUR_OF_DAY);
            if ((temp >= 8 && temp < 10) || (temp >= 18 && temp < 20)) {
                if ((accidentPosition.x < xRestrictBig && accidentPosition.x > xRestrictSmall) && (accidentPosition.y < yRestrictBig && accidentPosition.y > yRestrictSmall)) {
                    try {
                        if (temp < 10) {
//                        Date date=new Date(accidentPosition.accidentTime.get(Calendar.YEAR),)
                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(accidentPosition.accidentTime.get(Calendar.YEAR) + "-" + (accidentPosition.accidentTime.get(Calendar.MONTH)+1) + "-" + accidentPosition.accidentTime.get(Calendar.DAY_OF_MONTH) + " 10:00:00");
//                            Date date1=accidentPosition.accidentTime.getTime();
                            accidentPosition.modified = date.getTime() - accidentPosition.accidentTime.getTime().getTime();//更新偏差值
//                            accidentPosition.accidentTime.set(Calendar.HOUR_OF_DAY, 10);
                            accidentPosition.accidentTime.setTime(date);
                        } else {
                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(accidentPosition.accidentTime.get(Calendar.YEAR) + "-" + (accidentPosition.accidentTime.get(Calendar.MONTH)+1) + "-" + accidentPosition.accidentTime.get(Calendar.DAY_OF_MONTH) + " 20:00:00");
                            accidentPosition.modified =date.getTime() - accidentPosition.accidentTime.getTime().getTime();///更新偏差值
                            accidentPosition.accidentTime.setTime(date);
//                            accidentPosition.accidentTime.set(Calendar.HOUR_OF_DAY, 20);
                        }
//                        accidentPosition.accidentTime.set(Calendar.MINUTE, 0);
//                        accidentPosition.accidentTime.set(Calendar.SECOND, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void expendList(AccidentPosition accidentPosition) {
        accPosList.add(accidentPosition);
        length++;
    }

    public int getListX(int index) {
        return accPosList.get(index).x;
    }

    public int getListY(int index) {
        return accPosList.get(index).y;
    }

    public Calendar getListDate(int index) {
        return accPosList.get(index).accidentTime;
    }

    public long getModifiedTime(int index){
        return accPosList.get(index).modified;
    }

    public String gerListDate(int index) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(accPosList.get(index).accidentTime.getTime());
//        return accPosList.get(index).accidentTime.toString();
    }

    public String getListDateNoTime(int index) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(accPosList.get(index).accidentTime.getTime());
    }

    public void printString() {
        for (AccidentPosition accidentPosition : accPosList) {
            System.out.println(accidentPosition.x + "," + accidentPosition.y + "," + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(accidentPosition.accidentTime.getTime()));
        }
    }
}
