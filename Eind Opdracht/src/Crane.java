import java.util.Random;

class Crane implements Runnable {
    //declare variables
    private Thread t;
    private String threadName;
    private int holdingContainer = 0;
    private String container = "";
    private boolean loop = true;
    private int spots;
    private long millis;
    private String nameOfShip;
    private int quaySpots;
    private boolean waitLoop;
    private int craneId;
    Ship s;
    Quay q;

    Crane(String name, long time, String ship, int spots, int id) {//crane constructor
        threadName = name;
        millis = time;
        nameOfShip = ship;
        quaySpots = spots;
        craneId = id;
    }

    public void setShip(Ship s) {
        this.s = s;
    }//give crane a pointer to the ship it belongs to
    public void setQuay(Quay q) {this.q = q;}//give crane a pointer to the quay it belongs to

    public void run() {
        while (loop) {//main loop

            try {//wait untill the ship has given up atleast one container for pickup
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
            }

            container = Ship.pickUp();//find a container that can be picked up


            if (container != "") { //if thres a container that can be picked up, move it to the quay
                Random rand = new Random();
                System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + "(" + nameOfShip + ")" + " picking up " + container + "(" + nameOfShip + ")");
                holdingContainer = 1;
                try {
                    Thread.sleep(rand.nextInt(5001) + 1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                spots = q.getSpot();
                if(spots == (quaySpots - 1))
                {
                    System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + "(" + nameOfShip + ")" + " waiting...");
                    s.setBussyCranes(craneId);
                }
                System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + "(" + nameOfShip + ")" + " setting down " + container + "(" + nameOfShip + ")");
                s.lowerCounter(); //reduce the amount of containers given up
                q.setSpot(container); //fill a spot on the quay with a container from a crane
                synchronized(s)
                {
                    s.containerDone(); //keeping track of the amount of containers that have been moved of the ship
                }
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    public void endLoop()//end the run loops of this crane
    {
        System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + "(" + nameOfShip + ")" + " ending");
        loop = false;
    }

    public void endWaitLoop()
    {
        waitLoop = false;
    }
}