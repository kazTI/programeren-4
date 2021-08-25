import java.util.Scanner;

public class thread {
    //declare variables
    private static int cranes;
    private static int ships;
    private static int trucks;
    private static int quays;
    private static int timeToUnload;
    private static int containers;
    private static int quaySpots;
    public static long millis = System.currentTimeMillis();

    public static void main(String args[]) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter the number of cranes: "); //read amount of cranes
        cranes = reader.nextInt();
        System.out.println("Enter the number of ships: "); //read amount of ships
        ships = reader.nextInt();
        System.out.println("Enter the number of trucks "); //read amount of trucks
        trucks = reader.nextInt();
        System.out.println("Enter the number of quays: "); //read amount of quays
        quays = reader.nextInt();
        System.out.println("Enter the time it takes a truck to unload a container: "); //read the time it takes to unload a container
        timeToUnload = reader.nextInt();
        System.out.println("Enter the number of containers: "); //read the number of containers
        containers = reader.nextInt();
        System.out.println("Enter the number of spots for containers on the quay: "); //read the number of container spots on the quay
        quaySpots = reader.nextInt();
        reader.close();


        //make arrays of the threads
        Crane[] Cranes = new Crane[cranes];
        Ship[] Ships = new Ship[ships];
        Truck[] Trucks = new Truck[trucks];
        Quay[] Quays = new Quay[quays];


        for (int q = 0; q < quays; q++)
        {
            String quayName = "Quay-" + (q+1); //set thread name of quay
            Quays[q] = new Quay(quayName, quaySpots, trucks, millis, containers, ships); //put new quay into array
            for (int s = 0; s < ships; s++)
            {
                String shipName = "Ship-" + (s+1);//set thread name of ship
                Ships[s] = new Ship(shipName, containers, cranes, millis, quayName);//put new ship into array
                for (int c = 0; c < cranes; c++)
                {
                    String craneName = "Crane-" + (c+1);//set thread name of crane
                    Cranes[c] = new Crane(craneName, millis, shipName, quaySpots, c);//put new crane into array
                    Cranes[c].setShip(Ships[s]);//give crane a pointer to the ship it belongs to
                    Cranes[c].setQuay(Quays[q]);//give crane a pointer to the quay it belongs to
                    Cranes[c].start();//start the thread
                    Ships[s].setCrane(Cranes[c]);//give ship a pointer to the crane that belongs to it
                }

                for (int t = 0; t < trucks; t++)
                {
                    String truckName = "Truck-" + (t+1);//set thread name of truck
                    Trucks[t] = new Truck(truckName, timeToUnload, quaySpots, millis, shipName);//put new truck into array
                    Trucks[t].setQuay(Quays[q]);//give truck a pointer to the quay it belongs to
                    Trucks[t].setShip(Ships[s]);//give truck a pointer to the ship it belongs to
                    Trucks[t].start();//start the thread
                    Quays[q].setTruck(Trucks[t]);//give quay a pointer to the truck that belongs to it
                }
                Ships[s].start();//start the thread
            }
            Quays[q].start();//start the thread
        }
    }
}