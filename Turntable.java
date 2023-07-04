
package CO3408.sources;

import java.util.HashMap;
import java.util.*;
import static java.lang.Math.abs;

/**
 *
 * @author Nick
 */
public class Turntable extends Thread
{
    String id;
    private Present p;

    static int N = 0;
    static int E = 1;
    static int S = 2;
    static int W = 3;
    
    static int turnTime = 500;
    static int movePresent = 750;
    
    Connection[] connections = new Connection[4];
        
    // global lookup: age-range -> SackID
    static HashMap<String, Integer> destinations = new HashMap<>();
    
    // this individual table's lookup: SackID -> output port
    HashMap<Integer, Integer> outputMap = new HashMap<>();
    
    public Turntable (String ID)
    {
        id = ID;
    }
    
    public void addConnection(int port, Connection conn)
    {
        connections[port] = conn;
        
        if(conn != null)
        {
            if(conn.connType == ConnectionType.OutputBelt)
            {
                Iterator<Integer> it = conn.belt.destinations.iterator();
                while(it.hasNext())
                {
                    outputMap.put(it.next(), port);
                }
            }
            else if(conn.connType == ConnectionType.OutputSack)
            {
                outputMap.put(conn.sack.id, port);
            }
        }
    }
    
    public boolean getOccupiedStatus() {
        return p != null;
    }
    
    private synchronized void operateTurnTable(Connection con, int i){
        int waitTime = movePresent;
        if(con.belt.presentAvailable() && p == null){
            if (Thread.interrupted()){
                return;
            }
            p = con.belt.removeFromBelt();
            
            
            // sleep after removing from belt to simulate the time
            try{
                Thread.sleep(waitTime);
                //System.out.println(p + " added to turntable " + id);
            }
            catch(InterruptedException ex){
                return;
            }
            
            
            
            int dest = destinations.get(p.readDestination());
            int outputPort = outputMap.get(dest);
            
            // if difference between the two ports is not 2 then the table needs to turn to reach dest
            if (Math.abs(outputPort - i) != 2){
                waitTime += turnTime;
            }
            
            
            //System.out.println(connections[outputPort].connType);
            
            // Check the type of output port
            switch(connections[outputPort].connType){
                case OutputBelt -> { // Moves from turntable to belt, if full it waits
                    if (Thread.interrupted()){
                        return;
                    }
                    
                    connections[outputPort].belt.addToBelt(p, false);
                    p = null;
                    
                }
                case OutputSack -> {
                    // Moves from turntable to sack, if full it waits
                    if (Thread.interrupted()){
                        return;
                    }
                   
                    connections[outputPort].sack.addToSack(p);
                    p = null;
                    
                }
            }
            try{
                Thread.sleep(waitTime);
            }
            catch(InterruptedException ex){
                return;
            }
        }
        return;
    }
    
    public void run()
    {
        while(true){
            for(int i = 0; i < connections.length; i++){
                if(connections[i] != null){
                    if (connections[i].connType == ConnectionType.InputBelt){
                        if(Thread.interrupted()){
                            return;
                        }
                        operateTurnTable(connections[i], i);
                    }
                }
            }
        }
    }
}
