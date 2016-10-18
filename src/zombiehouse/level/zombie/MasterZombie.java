package zombiehouse.level.zombie;


import zombiehouse.common.LevelVar;
import zombiehouse.level.house.Tile;

/**
 * MasterZombie class contains the behavior for a
 * MasterZombie
 *
 * @author Stephen Sagartz & Joshua Donckels
 * @since 2016-03-05
 */
public class MasterZombie extends Zombie
{
  /**
   * Creates a MasterZombie that behaves uniquely
   *
   * @param heading   this MasterZombie's heading
   * @param positionX this MasterZombie's positionX
   * @param positionY this MasterZombie's positionY
   * @param curTile   this MasterZombie's curTile
   */
  public MasterZombie(double heading, double positionX, double positionY, Tile curTile, int id)
  {
    super(heading, positionX, positionY, curTile, id, 5, 0, 2);
  }
  
  /**
   * Master Zombie finds the shortest path to the player and walks through walls if any
   * zombie can smell the player.
   */
  @Override
  public void makeDecision()
  {
    boolean canDetectPlayer = false;
    super.setCollided(false);
    for (Zombie z : LevelVar.zombieCollection)
    {
      if (z.getSmell() && z.type != 2)
      {
        canDetectPlayer = true;
      }
    }
    if (canDetectPlayer)
    {
      super.setSmell(true);
      calcPath(LevelVar.house);
      super.move();
    }
    else
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
      this.zombie3D.setRotate(this.getHeading());
      this.zombie3D.nextFrame();
    }
  }
}