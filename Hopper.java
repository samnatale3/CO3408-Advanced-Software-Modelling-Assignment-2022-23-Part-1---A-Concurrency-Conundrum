
package CO3408.sources;

/**
 *
 * @author Nick
 */
public class Hopper extends Thread
{
    int id;
    long timeWaiting;
    Conveyor belt;
    int speed;
    private int presentsLoaded;
    static boolean running;

    Present[] collection;
    
    public Hopper(int id, Conveyor con, int capacity, int speed)
    {
        collection = new Present[capacity];
        this.id = id;
        belt = con;
        this.speed = speed;
        running = true;
        timeWaiting = 0;
        presentsLoaded = 0;
    }
    
    // Stop the hopper from adding to the belt
    static public void finish() {
        running = false;
    }
    
    public int getPresentsLoaded(){
        return presentsLoaded;
    }
    
    public void fill(Present p)
    {
        for(int i = 0; i < collection.length; i++){
            
            if (collection[i] == null){
                collection[i] = p;
                presentsLoaded++;
                break;
            }
        }
    }

    public void run()
    {
        for(int i = 0; i < collection.length; i++){
            if (running){
                long startTime = System.currentTimeMillis();
                if(Thread.interrupted()){
                    return;
                }
                belt.addToBelt(collection[i], true);
                long endTime = System.currentTimeMillis();
                timeWaiting += (endTime - startTime);
                collection[i] = null;
                try{
                    Thread.sleep(speed * 1000);
                }
                catch(InterruptedException ex){
                    return;
                }
            } else{
                return;
            }
        }
    }
    // TODO Add more methods?
}
