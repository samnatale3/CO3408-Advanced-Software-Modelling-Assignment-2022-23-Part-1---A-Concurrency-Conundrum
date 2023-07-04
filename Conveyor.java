package CO3408.sources;

import java.util.HashSet;
/**
 *
 * @author Nick
 */
public class Conveyor
{
    int id;
    private Present[] presents; // The requirements say this must be a fixed size array
    public  HashSet<Integer> destinations = new HashSet();
    private int nextIn = 0;
    private int nextOut = 0;
    private int available;
    
    public Conveyor(int id, int size)
    {
        this.id = id;
        presents = new Present[size];
        available = 0;
    }
    public void addDestination(int hopperID)
    {
        destinations.add(hopperID);
    }
    
    public int countBelt() {
        int count = 0;
        for (int i = 0; i < presents.length; i++){
            if (presents[i] != null) {
                count++;
            }
        }
        return count;
    }
    
    public synchronized boolean hasSpace(){
        for (int i = 0; i < presents.length; i++){
            if (presents[i] == null) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized boolean presentAvailable(){
        for (int i = 0; i < presents.length; i++){
            if (presents[i] != null) {
                return true;
            }
        }
        return false;
    }

    public synchronized void addToBelt(Present p, boolean isHopper){
        
        // while all available positions are currently taken, wait
        // if the hoppers are still running
        if (p != null){
            while (available == presents.length){
                //System.out.println(id + " waiting to add to belt");
                try {
                    wait();
                }
                catch(InterruptedException ex){
                    
                }
            }
            
            
            presents[nextIn] = p;
            available += 1;

            try {
                Thread.sleep((int) (Math.random() * 10));
            } 
            catch (InterruptedException ex) {
                
            }
            nextIn++;

            if(nextIn == presents.length){
                nextIn = 0;

            }
        }
        notifyAll();
    }
    
    public synchronized Present removeFromBelt(){
        Present res;
        while (available == 0){
            try {
                wait();
            } catch (InterruptedException ex) {
            }
            

        }
        res = presents[nextOut];
        try {
            Thread.sleep((int) (Math.random() * 10));
        } catch (InterruptedException ex) {
        }
        available--;

        presents[nextOut] = null;

        nextOut++;
        if (nextOut==presents.length)
            nextOut=0;

        notifyAll();
        return res;
    }
    
}
