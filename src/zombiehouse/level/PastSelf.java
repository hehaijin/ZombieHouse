/**
 *Zombie class written for ZombieHouse CS351 project that contains
 *the pathfinding and behavior algorithms for the Zombie objects in 
 *the game.
 *@Author Stephen Sagartz
 *@version 1.0
 *@since 2016-03-05 
 */

package zombiehouse.level;

import zombiehouse.graphics.PastSelf3D;

/**
 *Zombie class that contains methods inherited by the sub-classes of Zombie
 *as well as all Zombie variables. 
 */
public class PastSelf
{

  /**
   * the direction the Zombie will head in degrees
   */
  public double heading;
  /**
   * the Zombie's current X coordinate in the ZombieHouse
   */
  public double positionX;
  /**
   * the Zombie's current Y coordinate in the ZombieHouse
   */
  public double positionY;

  /**
   * The Zombie3D that represents this zombie in a 3D graphical world
   */
  public PastSelf3D pastSelf3D;

  /**
   * Constructs a Zombie object with the specified heading, X coordinate position,
   * Y coordinate position, and the Tile it is in, preferably as given by its
   * X and Y coordinates
   */
  public PastSelf(double heading, double positionX, double positionY) {
    this.heading = heading;
    this.positionX = positionX;
    this.positionY = positionY;
    pastSelf3D = new PastSelf3D();
  }
}