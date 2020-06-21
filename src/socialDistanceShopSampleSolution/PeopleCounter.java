package socialDistanceShopSampleSolution;

import java.util.concurrent.Semaphore;

//class to keep track of people inside and outside and left shop
public class PeopleCounter {
    private int peopleOutSide; //counter for people arrived but not yet in the building
    private int peopleInside; //people inside the shop
    private int peopleLeft; //people left the shop
    private final int maxPeople; //maximum for lockdown rules
    private Semaphore mutex;

    PeopleCounter(int max) {
        peopleOutSide = 0;
        peopleInside = 0;
        peopleLeft = 0;
        maxPeople = max;
        mutex = new Semaphore(1);
    }

    //getter
    public int getWaiting() {
        return peopleOutSide;
    }

    //getter
    public int getInside() {
        return peopleInside;
    }

    //getter
    public int getTotal() {
        return (peopleOutSide+peopleInside+peopleLeft);
    }

    //getter
    public int getLeft() {
        return peopleLeft;
    }

    //getter
    public int getMax() {
        return maxPeople;
    }

    //getter
    public void personArrived() throws InterruptedException {
        mutex.acquire();
        peopleOutSide++;
        mutex.release();
    }

    //update counters for a person entering the shop
    public void personEntered() throws InterruptedException {
        mutex.acquire();
        peopleOutSide--;
        peopleInside++;
        mutex.release();
    }

    //update counters for a person exiting the shop
    public void personLeft() throws InterruptedException {
        mutex.acquire();
        peopleInside--;
        peopleLeft++;
        mutex.release();

    }

    //reset - not really used
    synchronized public void resetScore() {
        peopleInside = 0;
        peopleOutSide = 0;
        peopleLeft = 0;
    }
}
