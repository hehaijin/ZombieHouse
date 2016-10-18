package zombiehouse.level.zombie;


import zombiehouse.common.LevelVar;
import zombiehouse.level.house.Tile;

import java.util.Random;

/**
 * RandomWalkZombie class contains the behavior for a
 * RandomWalkZombie
 *
 * @author Stephen Sagartz & Joshua Donckels
 * @since 2016-03-05
 */
public class RandomWalkZombie extends Zombie
{
  /**
   * Creates a RandomWalkZombie that behaves uniquely
   *
   * @param heading   this RandomWalkZombie's heading
   * @param positionX this RandomWalkZombie's positionX
   * @param positionY this RandomWalkZombie's positionY
   * @param curTile   this RandomWalkZombie's curTile
   */
  public RandomWalkZombie(double heading, double positionX, double positionY, Tile curTile, int id)
  {
    super(heading, positionX, positionY, curTile, id, 5, -1, 0);
  }
  
  /**
   * Updates and sets this Zombie's heading every zombie_Decision_Rate milliseconds
   * and adjusts the behavior according to the ZombieHouse Project specifications.
   */
  @Override
  public void makeDecision()
  {
    Random rand = new Random();
    //Use A* if it can smell you to find shortest distance
    if (this.getSmell())
    {
      super.calcPath(LevelVar.house);
      super.move();
    }
    //Walk in a random direction otherwise
    else
    {
      double nextRand = rand.nextDouble();
      super.setHeading(nextRand * 360);
      if (!super.getCollide())
      {
        super.move();
      }
      this.zombie3D.setRotate(this.getHeading());
      this.zombie3D.nextFrame();
    }
  }
}