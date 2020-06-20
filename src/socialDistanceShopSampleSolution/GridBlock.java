package socialDistanceShopSampleSolution;

// GridBlock class to represent a block in the shop.

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class GridBlock {
    private final AtomicBoolean isOccupied;
    private final boolean isExit;
    private final boolean isCheckoutCounter;
    private int [] coords; // the coordinate of the block.
    private final int ID;

    public static int classCounter=0;
    private Semaphore mutex;

    GridBlock(boolean exitBlock, boolean checkoutBlock) throws InterruptedException {
        isExit=exitBlock;
        isCheckoutCounter=checkoutBlock;
        isOccupied = new AtomicBoolean(false);
        ID=classCounter;
        classCounter++;
        mutex = new Semaphore(1);
    }

    GridBlock(int x, int y, boolean exitBlock, boolean refreshBlock) throws InterruptedException {
        this(exitBlock,refreshBlock);
        coords = new int [] {x,y};
        mutex = new Semaphore(1);
    }

    //getter
    public int getX() {
        return coords[0];
    }

    //getter
    public int getY() {
        return coords[1];
    }

    //for customer to move to a block
    public boolean get() throws InterruptedException {
        mutex.acquire();
        isOccupied.set(true);
        mutex.release();
        return true;
    }

    //for customer to leave a block
    public void release() {
        isOccupied.set(false);
    }

    //getter
    public boolean occupied() {
        return isOccupied.get();
    }

    //getter
    public boolean isExit() {
        return isExit;
    }

    //getter
    public boolean isCheckoutCounter() {
        return isCheckoutCounter;
    }

    //getter
    public int getID() {
        return this.ID;
    }
}
