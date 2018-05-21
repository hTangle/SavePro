import java.util.Date;

public class OutputList {
    int xStart;
    int yStart;
    int xEnd, yEnd;
    int carNum;
    long waitTime;
    int moveDistance;
    Date happendTime;

    public OutputList(int x, int y, int carNum, long waitTime, int moveDistance, int xEnd, int yEnd,Date happendTime) {
        this.xStart = x;
        this.yStart = y;
        this.carNum = carNum;
        this.waitTime = waitTime;
        this.moveDistance = moveDistance;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.happendTime=happendTime;
    }
    public String toString(){
        return "("+carNum+","+waitTime+")";
    }
}
