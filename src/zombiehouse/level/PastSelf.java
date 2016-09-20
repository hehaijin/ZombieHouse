/**
 *Zombie class written for ZombieHouse CS351 project that contains
 *the pathfinding and behavior algorithms for the Zombie objects in 
 *the game.
 *@Author Stephen Sagartz
 *@version 1.0
 *@since 2016-03-05 
 */

package zombiehouse.level;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Comparator;
import java.util.LinkedList;

import zombiehouse.graphics.PastSelf3D;
import zombiehouse.level.house.*;
import zombiehouse.common.*;
import zombiehouse.graphics.Zombie3D;

/**
 *Zombie class that contains methods inherited by the sub-classes of Zombie
 *as well as all Zombie variables. 
 */
public class PastSelf
{
  /**
   * the number of Tiles a Zombie can traverse over 1 second
   */
  private double zombie_Speed = 0.5;
  /**
   * the amount of time between Zombie heading updates
   */
  private static long zombie_Decision_Rate = 2000;
  /**
   * the number of Tiles away that a Zombie can smell
   */
  private int zombie_Smell = 15;
  /**
   * whether or not a Zombie has scent of the Player
   */
  private boolean canSmell = false;
  /**
   * whether or not a Zombie has collided with an Object
   */
  private boolean collided = false;
  /**
   * this Zombie's ID number
   */
  public int zombieID;
  /**
   * array of Tiles that lead to the Player
   */
  public ArrayList<Tile> path = new ArrayList<>();
  /**
   * a queue to hold Tiles that search for the Player for scent detection
   */
  public Queue<Tile> bfsQueue = new LinkedList<>();
  /**
   * a priority queue that holds Tiles examined while finding Zombie's path to
   * Player
   */
  public PriorityQueue<Tile> searchQueue = new PriorityQueue<>(25,
          new Comparator<Tile>() {

            public int compare(Tile one, Tile two)
            {
              if (one.cost > two.cost)
                return 1;
              if (one.cost < two.cost)
                return -1;
              return 0;
            }
          });
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
   * the Tile the Zombie is currently in inside the ZombieHouse
   */
  private Tile curTile;

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

  /**
   * @return this Zombie's curTile parameter
   */
  public Tile getPosition()
  {
    return curTile;
  }

  /**
   * Sets the Zombie's Tile parameter to tile
   */
  public void setPosition(Tile tile)
  {
    this.curTile = tile;
  }

  /**
   * Sets this Zombie's X coordinate to posX
   */
  public void setPositionX(double posX)
  {
    this.positionX = posX;
  }

  /**
   * Sets this Zombie's Y coordinate to posY
   */
  public void setPositionY(double posY)
  {
    this.positionY = posY;
  }

  /**
   * round method borrowed from Max's MainApplication class
   */
  private int round(double toRound)
  {
    if (toRound - ((int)toRound) < 0.5)
    {
      return (int)toRound;
    }
    else
    {
      return (int)toRound + 1;
    }
  }

  /**
   * Sets the X and Y coordinates of this Zombie to the position
   * altered by a factor of zombie_Speed and by the heading of the Zombie
   * assuming the Zombie's collided value is false, otherwise, it will not
   * change its coordinate or curTile parameters.
   */
  public void move()
  {
      double moveX;
      double moveY;
      double step = (double)1/60;
      moveX = (Math.cos(Math.toRadians(this.heading)) * this.zombie_Speed) * step;
      moveY = (Math.sin(Math.toRadians(this.heading)) * this.zombie_Speed) * step;
      if(this.positionX > 0 && this.positionX <= LevelVar.house[0].length && this.positionY > 0 && this.positionY <= LevelVar.house.length)
      {
        this.positionX += moveX;
        this.positionY += moveY;
        this.curTile = LevelVar.house[(int) this.positionX][(int) this.positionY];
      }
  }
}