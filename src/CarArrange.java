import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CarArrange {
    /*
     *首先需要车辆
     * 考虑到勘察车成本只和最大勘察车数量有关，因此直接固定勘察车数量
     * 使用线性规划算法
     * 每次算法首先确定车的数量
     * 然后分配车：分配方案，判定等待时间和车辆运行成本之间的关系 选择最优化分配
     * 基本思路是使用贪心算法，即每次选择分数最小的车辆
     */
    private int carNumber = 1;
    private int accidentEveryday = 312;
    private int source = 0;
    private float carPrice = 100000;
    private float speed = (float) (2.0 / 3.0);//    km/h
    private float movePrice = 0.5f;   // /km
    private static int waitTime = 30;  //minute
    private float sumPoint = 0; //0.618*平均每起事故等待时间+0.382*总成本
    private int accidentNumDay = 312;
    AccidentPositionList accidentPositionList;

    public void greedyAlgorithm(AccidentPositionList accidentPositionLists) {
        this.accidentPositionList = accidentPositionLists;
        setRestrict();//不可到达区域
        try {
            List<fromCarlistToExcel> totalOutput = new ArrayList();//总天数 输出
            for (int day = 0; day < accidentPositionList.length / accidentNumDay; day++) {
                totalOutput.add(getBestArrange(day));//按天分割
            }
            ExcelProcess.writeExcels(totalOutput);//写入到输出文件

        } catch (Exception e) {
            e.printStackTrace();
        }


//        try {
//            Date date = format.parse(beginDateString);
//            Calendar dateD = Calendar.getInstance();
//            dateD.setTime(date);
//            carNumber = 20;//设置车的数量
//            double bestOutput=Double.MAX_VALUE;
//            int bestCarNumber=0;
//            for (carNumber = 10; carNumber < 100; carNumber++) {
//                List<CarState> list = new ArrayList();
//                List<OutputList> outputLists = new ArrayList();
//                for (int i = 0; i < carNumber; i++) {
//                    list.add(new CarState(date));
//                }
//                int counts = 0;
//                while (true) {
//                    Calendar currentTime = accidentPositionLists.getListDate(counts);//发生事故的时间
//                    int X = accidentPositionLists.getListX(counts);
//                    int Y = accidentPositionLists.getListY(counts);
////                    System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime.getTime()));
//                    if (currentTime.get(Calendar.DATE) == dateD.get(Calendar.DATE) && currentTime.get(Calendar.MONTH) == dateD.get(Calendar.MONTH) && currentTime.get(Calendar.YEAR) == dateD.get(Calendar.YEAR)) {
//                        findBestTrace(X, Y, currentTime, list, outputLists);
//                    } else {
//                        break;
//                    }
//                    counts++;
//                }
//                double tempOutput=judgeTheOutput(outputLists);
//                if(tempOutput<bestOutput){
//                    bestOutput=tempOutput;
//                    bestCarNumber=carNumber;
//                }
//            }
//            System.out.println("best:"+bestOutput+","+bestCarNumber);
////            ExcelProcess.writeExcel(outputLists);
////            for (CarState carState : list) {
////                carState.printMovePositionList();
////                System.out.println();
//////                System.out.println(counts + ":" + carState.printMovePositionList());
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

    public fromCarlistToExcel getBestArrange(int day) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//取得当天时间，为了初始化车辆的开始时间
        String beginDateString = accidentPositionList.getListDateNoTime(day * accidentNumDay);//从数据中取出需要的时间序列部分
        Date date = format.parse(beginDateString);
        double bestOutput = Double.MAX_VALUE;
        int bestCarNumber = 0;
        List<OutputList> bestList = new ArrayList();
        for (carNumber = 10; carNumber < 100; carNumber++) {
            for (int mychoose = 0; mychoose < 2; mychoose++) {
                List<CarState> list = new ArrayList();
                List<OutputList> outputLists = new ArrayList();
                for (int i = 0; i < carNumber; i++) {
                    list.add(new CarState(date));
                }
                for (int index = day * accidentNumDay; index < (day + 1) * accidentNumDay; index++) {
                    Calendar currentTime = accidentPositionList.getListDate(index);//发生事故的时间
                    int X = accidentPositionList.getListX(index);
                    int Y = accidentPositionList.getListY(index);
                    long modifiedTime = accidentPositionList.getModifiedTime(index);//获取偏差值，引申条件2
                    findBestTrace(X, Y, currentTime, list, outputLists, modifiedTime, carNumber,mychoose);//获取最佳arrangement
                }
                double tempOutput = judgeTheOutput(outputLists);//计算安排分数
                if (tempOutput < bestOutput) {//更新最佳分数
                    bestOutput = tempOutput;
                    bestCarNumber = carNumber;
                    bestList = outputLists.subList(0, outputLists.size());
                }
            }
        }
//       System.out.println(bestCarNumber+","+beginDateString+","+bestOutput);
        return new fromCarlistToExcel(bestList, bestCarNumber, beginDateString);
    }

    public <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    public void setRestrict() {
        int xRestrictSmall = 20;
        int xRestrictBig = 25;
        int yRestrictSmall = 20;
        int yRestrictBig = 40;
        //设定限制区域
        accidentPositionList.modifiedTheData(xRestrictSmall, xRestrictBig, yRestrictSmall, yRestrictBig);
        accidentPositionList.sort();
    }

    public double judgeTheOutput(List<OutputList> outputLists) {
        double bestOutput = 0;
        long meanWaitTime = 0;
        long sumMoveDistance = 0;
        for (OutputList outputList : outputLists) {
            sumMoveDistance += outputList.moveDistance;
            meanWaitTime += outputList.waitTime;
        }
        meanWaitTime /= outputLists.size();
        bestOutput = 0.618 * meanWaitTime + 0.382 * (carNumber * 100000 + sumMoveDistance * 0.5);
//        System.out.println("bestOutput:" + bestOutput);
        return bestOutput;
    }

    public void findBestTrace(int dirX, int dirY, Calendar targetTime, List<CarState> list, List<OutputList> outputLists, long modifiedTime, int totalCarNumber,int mychoose) {
        //以每起事故等待时间最少为评价标准
        //等待时间分为两种情况 一种是当前车辆都处于忙的状态 因此等待时间就是 车辆能够空闲时刻-事故发生时刻
        //                     第二种是当前有车辆处于空闲时刻因此等待时间就是 事故发生时刻-车辆空闲时刻
        //                      很明显第一种情况下等待时间要包括车辆从忙到闲的时间，因此占用时间很长
        //再选择最佳车辆的时候，需要设定一个标准bestSource
        //bestSource越小，选择的可能性越高（模拟退火）
        float bestSource = Float.MAX_VALUE;
        int num = 0;
        int targetNum = 0;
        float moveTime = 0;
        float spendTime = 0;
        float waitTimeFinal = 0;
        int moveDistance = 0;
//        float waitTime=0;
        for (CarState carState : list) {
            float currentSource = 0;//等待时间需要统一单位为毫秒 贪心算法评价标准
            float fixSource = 0;//fixSource=(0.618/S)*T+0.191D+0.382*100000/S*N
            float waitTime = 0;
            if(mychoose==0){
                if (carState.isFree(targetTime.getTime())) {
                    currentSource = carState.getMoveTime(speed, dirX, dirY) * 60000f + targetTime.getTime().getTime() - carState.beginTime.getTime();//speed->km/min->min
                    //以等待时间为标准是否合适
                    waitTime = carState.getMoveTime(speed, dirX, dirY) * 60000f + modifiedTime;
                    fixSource = (0.618f / accidentNumDay) * waitTime + 0.191f * carState.getDistance(dirX, dirY) + 0.382f * 100000f * totalCarNumber / accidentNumDay;
                    if (bestSource > waitTime) {
                        bestSource = waitTime;
                        waitTimeFinal = waitTime;
                        moveTime = carState.getMoveTime(speed, dirX, dirY);
                        targetNum = num;
                        spendTime = currentSource;
                        moveDistance = carState.getDistance(dirX, dirY);
                    }
                } else {
                    currentSource = carState.getMoveTime(speed, dirX, dirY) * 60000f + carState.beginTime.getTime() - targetTime.getTime().getTime();
                    waitTime = currentSource + modifiedTime;
                    fixSource = (0.618f / accidentNumDay) * waitTime + 0.191f * carState.getDistance(dirX, dirY) + 0.382f * 100000f * totalCarNumber / accidentNumDay;
                    if (bestSource > waitTime) {
                        bestSource = waitTime;
                        waitTimeFinal = waitTime;
                        moveTime = carState.getMoveTime(speed, dirX, dirY);
                        targetNum = num;
                        spendTime = carState.getMoveTime(speed, dirX, dirY) * 60000f;
                        moveDistance = carState.getDistance(dirX, dirY);
                    }
                }
                num++;
            }else{
                if (carState.isFree(targetTime.getTime())) {
                    currentSource = carState.getMoveTime(speed, dirX, dirY) * 60000f + targetTime.getTime().getTime() - carState.beginTime.getTime();//speed->km/min->min
                    //以等待时间为标准是否合适
                    //
                    waitTime = carState.getMoveTime(speed, dirX, dirY) * 60000f + modifiedTime;
                    fixSource = (0.618f / accidentNumDay) * waitTime + 0.191f * carState.getDistance(dirX, dirY) + 0.382f * 100000f * totalCarNumber / accidentNumDay;
                    if (bestSource > fixSource) {
                        bestSource = fixSource;
                        waitTimeFinal = waitTime;
                        moveTime = carState.getMoveTime(speed, dirX, dirY);
                        targetNum = num;
                        spendTime = currentSource;
                        moveDistance = carState.getDistance(dirX, dirY);
                    }
                } else {
                    currentSource = carState.getMoveTime(speed, dirX, dirY) * 60000f + carState.beginTime.getTime() - targetTime.getTime().getTime();
                    waitTime = currentSource + modifiedTime;
                    fixSource = (0.618f / accidentNumDay) * waitTime + 0.191f * carState.getDistance(dirX, dirY) + 0.382f * 100000f * totalCarNumber / accidentNumDay;
                    if (bestSource > fixSource) {
                        bestSource = fixSource;
                        waitTimeFinal = waitTime;
                        moveTime = carState.getMoveTime(speed, dirX, dirY);
                        targetNum = num;
                        spendTime = carState.getMoveTime(speed, dirX, dirY) * 60000f;
                        moveDistance = carState.getDistance(dirX, dirY);
                    }
                }
                num++;
            }

        }
        outputLists.add(new OutputList(list.get(targetNum).x, list.get(targetNum).y, targetNum, (long) (waitTimeFinal), moveDistance, dirX, dirY, targetTime.getTime()));
        list.get(targetNum).updateState(dirX, dirY, spendTime);
        list.get(targetNum).expendMovePositionList(dirX, dirY, moveTime);
    }
}
