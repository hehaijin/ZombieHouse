package zombiehouse.level.zombie;


import zombiehouse.level.house.*;
import zombiehouse.common.*;

import static zombiehouse.common.LevelVar.house;

/**
 * MasterZombie class contains the behavior for a
 * MasterZombie
 * @author Stephen Sagartz
 * @since 2016-03-05
 */
public class MasterZombie extends Zombie
{
  /**
   * Creates a MasterZombie that behaves uniquely
   * @param heading this MasterZombie's heading
   * @param positionX this MasterZombie's positionX
   * @param positionY this MasterZombie's positionY
   * @param curTile this MasterZombie's curTile
   */
  public MasterZombie(double heading, double positionX, double positionY, Tile curTile, int id)
  {
    super(heading, positionX, positionY, curTile, id, 5, 0);
    this.type = 2;
  }

    /**
     * Updates and sets this Zombie's heading every zombie_Decision_Rate milliseconds
     * and adjusts the behavior according to the ZombieHouse Project specifications.
     */
  @Override
  public void makeDecision()
  {
    boolean canDetectPlayer = false;
    for(Zombie z : LevelVar.zombieCollection) {
      if(z.getSmell() && z.type != 2) {
        canDetectPlayer = true;
      }
    }
    if(canDetectPlayer)
    {
      this.setCollided(false);
      super.setSmell(true);
      super.calcPath(house);
    }
    else
    {
      super.setSmell(false);
      if(super.getCollide())
      {
        double curHeading = super.getHeading();
        double boundA = (curHeading + 90)%360;
        double boundB = (curHeading - 90)%360;
        if(boundA < boundB)
        {
          super.setHeading((180 + curHeading)%360);
          super.setCollided(false);
        }
        else
        {
          super.setHeading((180 + curHeading)%360);
          super.setCollided(false);
        }
      }
    }
  }
}