public class Position {
    int x;
    int y;
    float waitTime=0;
    public Position(int x,int y){
        this.x=x;
        this.y=y;
    }
    public Position(int x,int y,float waitTime){
        this.x=x;
        this.y=y;
        this.waitTime=waitTime;
    }
    public String toString(){
        return "("+x+","+y+","+waitTime+")";
    }
}
