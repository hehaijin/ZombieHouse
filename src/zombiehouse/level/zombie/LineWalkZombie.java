package zombiehouse.level.zombie;

import zombiehouse.level.house.*;
import zombiehouse.common.*;

/**
 * The LineWalkZombie class contains the behavior for a
 * LineWalkZombie
 *
 * @author Stephen Sagartz
 * @since 2016-03-05
 */
public class LineWalkZombie extends Zombie
{
  /**
   * creates a LineWalkZombie that behaves uniquely
   *
   * @param heading   this LineWalkZombie's heading
   * @param positionX this LineWalkZombie's positionX
   * @param positionY this LineWalkZombie's positionY
   * @param curTile   this LineWalkZombie's curTile
   */
  public LineWalkZombie(double heading, double positionX, double positionY,
                        Tile curTile, int id)
  {
    super(heading, positionX, positionY, curTile, id, 5, -1, 1);
  }

  /**
   * Updates and sets this Zombie's heading every zombie_Decision_Rate milliseconds
   * and adjusts the behavior according to the ZombieHouse Project specifications.
   */
  @Override
  public void makeDecision()
  {
    if (this.getSmell())
    {
      super.calcPath(LevelVar.house);
      super.move();
    } else
    {
      if (super.getCollide())
      {
        double curHeading = super.getHeading();
        super.setHeading(curHeading + 180);
      }
      else
      {
        super.move();
      }
      this.zombie3D.setRotate(getHeading());
      this.zombie3D.nextFrame();
    }
  }
}
