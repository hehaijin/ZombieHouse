package zombiehouse.level.zombie;


import zombiehouse.level.house.*;

import java.util.Random;

import static zombiehouse.common.LevelVar.house;

/**
 * RandomWalkZombie class contains the behavior for a
 * RandomWalkZombie
 *
 * @author Stephen Sagartz
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
    if (this.getSmell())
    {
      super.calcPath(house);
      super.move();
    } else
    {
      super.getPath().clear();
      double nextRand = rand.nextDouble();
      super.setHeading(nextRand * 360);
      if (!super.getCollide())
      {
        super.move();
      }
      this.zombie3D.setRotate(this.getHeading()+ 180);
      this.zombie3D.nextFrame();
    }
  }
}