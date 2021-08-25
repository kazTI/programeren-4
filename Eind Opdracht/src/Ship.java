class Ship implements Runnable {
    //declare variables
    private Thread t;
    private String threadName;
    private int numberOfContainers;
    private int counter = 0;
    public String[] containers;
    public static int amountOfCranes;
    public static String[] containerSpots;
    private boolean loop = true;
    public static boolean[] test;
    private static int counter2 = 0;
    private int counter3 = 0;
    private long millis;
    private int counter4 = 0;
    private String nameOfQuay;
    private int[] bussyCranes;
    Crane[] arrayOfCranes;

    Ship(String name, int containers, int cranes, long time, String quay) {//ship constructor
        threadName = name;
        numberOfContainers = containers;
        amountOfCranes = cranes;
        millis = time;
        nameOfQuay = quay;
        arrayOfCranes = new Crane[amountOfCranes];
        bussyCranes = new int[amountOfCranes];
        for(int i = 0; i < amountOfCranes; i++)
        {
            bussyCranes[i] = 0;
        }
    }


    public void setCrane(Crane c) {//give ship pointers to the cranes that belongs to it
        this.arrayOfCranes[counter2] = c;
        counter2++;
        if(counter2 == amountOfCranes)
        {
            counter2 = 0;
        }
    }


    public void run() {
        containers = new String[numberOfContainers];
        containerSpots = new String[amountOfCranes];
        test = new boolean[amountOfCranes];
        for (int i = 1; i <= numberOfContainers; i++) {
            containers[i - 1] = "container " + i;
        }

        for (int i = 0; i < amountOfCranes; i++) {
            containerSpots[i] = "";
            test[i] = false;
        }

        giveUp(); //give up a container for pickup

        while (loop) {//main loop
            for (int i = 0; i < amountOfCranes; i++) {
                if (containerSpots[i] != "") {
                    synchronized (arrayOfCranes[i]) {
                        arrayOfCranes[i].notify(); //notify a crane if a container has been given up for pickup
                    }
                }
            }

        }
    }

    public void lowerCounter() { //lower the counter if a container has been picked up and offer up a new one
        counter3--;
        giveUp();
    }

    public void giveUp() { //offer up a container
        for (int i = 0; i < amountOfCranes; i++) {
            if (counter3 != 2 && containerSpots[i] == "") {
                if(counter == numberOfContainers)
                {
                    break;
                }
                containerSpots[i] = containers[counter];
                test[i] = true;
                containers[counter] = "";
                System.out.println((System.currentTimeMillis() - millis) + ": " + containerSpots[i] + "(" + threadName + ")" + " being given up for pickup by " + threadName + "(" + nameOfQuay + ")");
                counter++;
                counter3++;
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    public synchronized static String pickUp() { //returns name of the container that was picked up

        String returnStr = "";
        for (int i = 0; i < amountOfCranes; i++) {
            if (containerSpots[i] != "") {
                returnStr = containerSpots[i];
                containerSpots[i] = "";
                test[i] = false;
                return returnStr;
            }
        }
        return returnStr;
    }
    public synchronized void containerDone() //keeping track of the amount of containers that have been moved of the ship
    {
        counter4++;
        if(counter4 == numberOfContainers)
        {
            for (int i = 0; i < amountOfCranes; i++) {
                arrayOfCranes[i].endLoop();
            }
            System.out.println((System.currentTimeMillis() - millis) + ": " + "all containers from " +threadName + "(" + nameOfQuay + ")" + " unloaded");
            System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + "(" + nameOfQuay + ")" + " ending");
            loop = false;
        }
    }

    public void notifyCrane()//notify a crane that a spot has opened on the quay
    {
        for(int i = 0; i < amountOfCranes; i++)
        {
            if(bussyCranes[i] == 1)
            {
                System.out.println("this happens");
                arrayOfCranes[i].endWaitLoop();
            }
        }
    }

    public void setBussyCranes(int craneId)
    {
        bussyCranes[craneId] = 1;
    }
}
