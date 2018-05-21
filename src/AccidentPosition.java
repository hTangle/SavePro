
import java.util.Calendar;
import java.util.Date;

public class AccidentPosition {
    int x;
    int y;
    Calendar accidentTime;
    long modified=0;
    public AccidentPosition(int x,int y,Calendar date){
        this.x=x;
        this.y=y;
        this.accidentTime=date;
    }
}
