
class Truck implements Runnable {
    //declare variables
    Quay q;
    Ship s;
    private Thread t;
    private String threadName;
    private int timeToUnload;
    private int quaySpots;
    private String[] containerNames;
    private int loaded = 0;
    private String container;
    private long millis;
    private boolean loop = true;
    private String nameOfShip;

    Truck(String name, int unload, int spots, long time, String ship) {//truck constructor
        threadName = name;
        timeToUnload = unload;
        quaySpots = spots;
        millis = time;
        nameOfShip = ship;
        containerNames = new String[quaySpots];
    }

    public void setQuay(Quay q) {
        this.q = q;
    }//give truck a pointer to the quay it belongs to
    public void setShip(Ship s) {
        this.s = s;
    }//give truck a pointer to the ship it belongs to

    public void run() {
        while (loop) {
            try {//wait untill there is a container on the quay
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
            }

            for (int i = 0; i < quaySpots; i++) {
                containerNames[i] = q.getContainer(i);
                if (containerNames[i] != "" && loaded == 0) { //check for filled spots that the truck can take and if the truck already has a load
                    loaded = 1;
                    container = containerNames[i];
                    q.lowerCounter(); //reduce the amount of spots filled
                    q.setFreeSpot(i);//free up a spot on the quay
                    s.notifyCrane(); //notify a crane that a spot has opened on the quay
                }
            }

            if (container != "") { //if theres a container to be picked up, pick it up and drive it away
                System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + "(" + nameOfShip + ")" + " loading up " + container + "(" + nameOfShip + ")");
                try {
                    Thread.sleep(timeToUnload * 1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                loaded = 0;
                System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + "(" + nameOfShip + ")" + " returning");
                synchronized(q)
                {
                    q.containerDone();//keeping track of the amount of containers that have been driven away by the trucks
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

    public void endLoop()//end the run loops of this truck
    {
        System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + "(" + nameOfShip + ")" + " ending");
        loop = false;
    }
}
