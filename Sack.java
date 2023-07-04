
package CO3408.sources;

/**
 *
 * @author Nick
 */
public class Sack
{
    int id;
    Present[] accumulation;
    int nextIn = 0;
    
    public Sack(int id, int capacity)
    {
        accumulation = new Present[capacity];
        this.id = id;
    }
    
    public synchronized boolean hasSpace(){
        for (int i = 0; i < accumulation.length; i++){
            if (accumulation[i] == null) {
                return true;
            }
        }
        return false;
    }
    
    public int getId(){
        return id;
    }
    
    public synchronized void addToSack(Present p){
        while (nextIn == accumulation.length){
            System.out.println("Sack " + id + " full");
            try {
                wait();
            }
            catch(InterruptedException ex){
                
            }
        }
        if (nextIn != accumulation.length){
            /*try {
                Thread.sleep((int) (Math.random() * 10));
            } catch (InterruptedException ex) {}*/
            accumulation[nextIn] = p;
            //System.out.println(p + " added to sack " + id);
            
            nextIn++;
            notifyAll();
        } else{
            //System.out.println("Sack " + id + " full");
        }
    }
}
