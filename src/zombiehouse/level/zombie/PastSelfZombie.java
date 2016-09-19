package zombiehouse.level.zombie;


import zombiehouse.common.*;
import zombiehouse.level.house.Tile;

import java.util.List;

import static zombiehouse.common.LevelVar.house;

/**
 * MasterZombie class contains the behavior for a
 * MasterZombie
 * @author Stephen Sagartz
 * @since 2016-03-05
 */
public class PastSelfZombie extends Zombie
{
    public PastSelfZombie(List<Double> xPos, List<Double> yPos)
    {
        super(100, xPos.get(10), yPos.get(10), house[2][4], 30);
    }

    @Override
    public void move() {
    }

    @Override
    public boolean collide() {
        return false;
    }

    @Override
    public boolean scentDetection(int searchDepth, Tile[][] house){
        return false;
    }

    @Override
    public void calcPath(Tile[][] house){
    }

    @Override
    public void makeHeading(){
    }
}