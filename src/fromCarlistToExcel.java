import java.util.List;

public class fromCarlistToExcel {
    List<OutputList> bestList;
    int carNumber;
    String dateInfo;
    public fromCarlistToExcel(List<OutputList> bestList,int carNumber,String dateInfo){
        this.bestList=bestList.subList(0,bestList.size());
        this.carNumber=carNumber;
        this.dateInfo=dateInfo;
    }
}
