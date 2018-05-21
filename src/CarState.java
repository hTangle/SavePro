import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CarState {
    boolean isUsed = false;
    int x = 0;
    int y = 0;
    Date beginTime;
    List<Position> movePosition=new ArrayList();

    public CarState(Date beginTime) {
        this.beginTime = beginTime;
    }

    public boolean isFree(Date tempDate) {
        if (beginTime.getTime() <= tempDate.getTime()) {
            return true;
        } else {
            return false;
        }
    }
    public String toString(){
        return "x:"+x+",y:"+y+",Date:"+beginTime;
    }
    public int getDistance(int x, int y) {
        return Math.abs(x - this.x) + Math.abs(y - this.y);
    }

    public float getMovePrice(float movePrice, int x, int y) {
        return movePrice * getDistance(x, y);
    }

    public float getMoveTime(float speed, int x, int y) {
        return getDistance(x, y) / speed;
    }

    public void updateState(int x, int y, int duriationTime) {
        this.x = x;
        this.y = y;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginTime);
        calendar.add(Calendar.MINUTE, duriationTime);
        beginTime = calendar.getTime();
    }

    /***
     * 更新车辆的状态 考虑到beginTime是指车辆的空闲开始时间
     * 计算为beginTime=beginTime+waitTime+30min
     * 计算公式是用毫秒计算的，因此用毫秒作为基准单位
     * @param x
     * @param y
     * @param spendTime:花费时间，此时间起始点车辆空闲时间起始时刻，然后包括车辆等待事故发生时间，车辆行进耗费时间，车辆维修耗费时间
     *                     spendTime=waitTime+speedTime+repairTime
     */
    public void updateState(int x,int y,float spendTime){
        //
        this.x=x;
        this.y=y;
        this.beginTime=new Date((long)(beginTime.getTime()+spendTime+30*60000));
    }
    public void updateState(int x, int y, Calendar targetTime) {
        this.x = x;
        this.y = y;
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(targetTime);
        beginTime = targetTime.getTime();

    }
    public void expendMovePositionList(int xP,int yP,float moveTime){
        movePosition.add(new Position(xP,yP,moveTime));
    }
    public void printMovePositionList(){
        for(Position position:movePosition){
            System.out.print("("+position.x+","+position.y+","+position.waitTime+")");
        }
    }
}
