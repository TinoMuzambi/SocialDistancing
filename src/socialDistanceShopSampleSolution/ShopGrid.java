package socialDistanceShopSampleSolution;

import java.util.concurrent.Semaphore;

public class ShopGrid {
    private final GridBlock [][] Blocks;
    private final int x;
    private final int y;
    public final int checkout_y;
    private final static int minX =5;//minimum x dimension
    private final static int minY =5;//minimum y dimension
    private Semaphore maxInside;    // Semaphore for max people inside shop.
    private Semaphore entranceLock; // Semaphore for ensuring only one person at the entrance at any one time.
    private Semaphore full;         // Semaphore to check if shop is has people.

    ShopGrid() {
        this.x=20;
        this.y=20;
        this.checkout_y=y-3;
        Blocks = new GridBlock[x][y];
        int [] [] dfltExit= {{10,10}};
        this.initGrid(dfltExit);
    }

    ShopGrid(int x, int y, int [][] exitBlocks,int maxPeople) {
        if (x<minX) x=minX; //minimum x
        if (y<minY) y=minY; //minimum x
        this.x=x;
        this.y=y;
        this.checkout_y=y-3;
        Blocks = new GridBlock[x][y];
        this.initGrid(exitBlocks);
        maxInside = new Semaphore(maxPeople);       // Max of maxPeople at a time inside.
        entranceLock = new Semaphore(1);     // One person at the entrance at a time.
        full = new Semaphore(0);
    }

    private  void initGrid(int [][] exitBlocks) {
        for (int i=0;i<x;i++) {
            for (int j=0;j<y;j++) {
                boolean exit=false;
                boolean checkout=false;
                for (int e=0;e<exitBlocks.length;e++)
                    if ((i==exitBlocks[e][0])&&(j==exitBlocks[e][1]))
                        exit=true;
                if (j==(y-3)) {
                    checkout=true;
                }//checkout is hardcoded two rows before  the end of the shop
                Blocks[i][j]=new GridBlock(i,j,exit,checkout);
            }
        }
    }

    //get max X for grid
    public  int getMaxX() {
        return x;
    }

    //get max y  for grid
    public int getMaxY() {
        return y;
    }

    public GridBlock whereEntrance() { //hard coded entrance
        return Blocks[getMaxX()/2][0];
    }

    //is a position a valid grid position?
    public  boolean inGrid(int i, int j) {
        if ((i>=x) || (j>=y) ||(i<0) || (j<0))
            return false;
        return true;
    }

    //called by customer when entering shop
    public GridBlock enterShop() throws InterruptedException  {
        maxInside.acquire();    // decrement whilst there's space in shop.
        entranceLock.acquire(); // enter if no one else is at entrance.
        GridBlock entrance = whereEntrance();
        entrance.get();
        full.release(); // add a permit to full.
        return entrance;
    }

    //called when customer wants to move to a location in the shop
    public GridBlock move(GridBlock currentBlock,int step_x, int step_y) throws InterruptedException {
        //try to move in

        int c_x= currentBlock.getX();
        int c_y= currentBlock.getY();

        int new_x = c_x+step_x; //new block x coordinates
        int new_y = c_y+step_y; // new block y  coordinates

        //restrict i an j to grid
        if (!inGrid(new_x,new_y)) {
            //Invalid move to outside shop - ignore
            return currentBlock;
        }

        if ((new_x==currentBlock.getX())&&(new_y==currentBlock.getY())) //not actually moving
            return currentBlock;

        GridBlock newBlock = Blocks[new_x][new_y];

        if (!newBlock.occupied())  {  //get successful because block not occupied
            newBlock.get();
            currentBlock.release(); //must release current block
            entranceLock.release(); // release once moved.
        }
        else {
            newBlock=currentBlock;
            ///Block occupied - giving up
        }
        return newBlock;
    }

    //called by customer to exit the shop
    public void leaveShop(GridBlock currentBlock) throws InterruptedException {
        full.acquire();
        entranceLock.acquire();
        currentBlock.release();
        entranceLock.release();
        maxInside.release();
    }

}