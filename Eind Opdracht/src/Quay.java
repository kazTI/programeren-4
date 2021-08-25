class Quay implements Runnable {

    //declare variables
    private Thread t;
    private String threadName;
    private int amountOfSpots;
    public String[] spots;
    private int counter = 0;
    boolean loop = true;
    Truck[] arrayOfTrucks;
    private int counter2 = 0;
    private int counter3 = 0;
    private int amountOfTrucks;
    private int[] freeSpots;
    private boolean atleastOne = false;
    private long millis;
    private int counter4 = 0;
    private int numberOfContainers;
    private int numberOfShips;

    Quay(String name, int quaySpots, int trucks, long time, int containers, int ships) { //quay constructor
        threadName = name;
        amountOfSpots = quaySpots;
        amountOfTrucks = trucks;
        numberOfContainers = containers;
        numberOfShips = ships;
        millis = time;
        spots = new String[amountOfSpots];
        arrayOfTrucks = new Truck[amountOfTrucks];
        freeSpots = new int[amountOfSpots];
        for (int i = 0; i < amountOfSpots; i++) {
            spots[i] = "";
        }
        for (int i = 0; i < amountOfSpots; i++) {
            freeSpots[i] = 0;
        }
    }

    public void setTruck(Truck t) {//give quay pointers to the trucks that belongs to it
        this.arrayOfTrucks[counter2] = t;
        counter2++;
        if(counter2 == amountOfTrucks)
        {
            counter2 = 0;
        }
    }

    public void run() {

        while (loop) {
            notifyTruck(); //call a function to notify a truck on loop
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }


    public void setSpot(String name) { //fill a spot on the quay with a container from a crane
        int foundSpot = 0;
        boolean loop2 = true;
        while(loop2) {//loop untill a free spot is found
            for (int i = 0; i < amountOfSpots; i++) {
                if (freeSpots[i] == 0) { //check if theres a freespot
                    counter++;
                    spots[i] = name;
                    foundSpot = 1;
                    freeSpots[i] = 1;
                    atleastOne = true; //making sure the program knows theres atleast one spot filled
                    foundSpot = 1;
                    notifyTruck(); //call a function to notify a truck
                    break; //break out of the for loop to make sure that only one spot gets filled
                }
            }
            if(foundSpot == 1) //break out of the loop if a spot has been filled
            {
                loop2 = false;
            }

        }
    }

    public int getSpot(){
        return counter;
    } //return the amount of the spots filled

    public String getContainer(int spot){
        return spots[spot];
    } //returns the name of the container in a spot

    public void lowerCounter() {
        counter--;
    } //reduce the amount of spots filled

    public void setFreeSpot(int var) { //free up a spot on the quay
        freeSpots[var] = 0;
        spots[var] = "";
    }

    public void notifyTruck() //function to notify a truck when theres a container that can be picked
    {
        for (int i = 0; i < amountOfSpots; i++) {
            if (atleastOne) { //making sure atleast one spot is filled on the quay
                synchronized (arrayOfTrucks[counter3]) { //synchronizing with the truck thread
                    arrayOfTrucks[counter3].notify(); //notify the truck
                    atleastOne = false; //since a spot has been emptyd, this might not be true anymore
                    counter3++;
                    if(counter3 == (amountOfTrucks))
                    {
                        counter3 = 0;
                    }
                }
            }
        }
    }

    public synchronized void containerDone()
    {
        counter4++; //keeping track of the amount of containers that have been driven away by the trucks
        if(counter4 == (numberOfContainers * numberOfShips)) //checking if the amount of containers that have been driven away, equals the total amount of containers
        {
            for (int i = 0; i < amountOfTrucks; i++) {
                arrayOfTrucks[i].endLoop(); //end the run loops of every truck
            }
            System.out.println((System.currentTimeMillis() - millis) + ": " + "all containers from " +threadName+ " unloaded");
            System.out.println((System.currentTimeMillis() - millis) + ": " + threadName + " ending");
            loop = false; //end the run loop of this quay
        }
    }

}