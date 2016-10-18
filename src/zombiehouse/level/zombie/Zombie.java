package zombiehouse.level.zombie;

import zombiehouse.common.LevelVar;
import zombiehouse.common.Player;
import zombiehouse.graphics.Zombie3D;
import zombiehouse.level.house.Tile;
import zombiehouse.level.house.Wall;
import java.util.*;

/**
 * Zombie class written for ZombieHouse CS351 project that contains
 * the A* path finding and behavior algorithms for the Zombie objects in
 * the game.
 *
 * @Author Stephen Sagartz & Anton Kuzmin
 * @version 1.0
 * @since 2016-03-05
 */

/**
 * Zombie class that contains methods inherited by the sub-classes of Zombie
 * as well as all Zombie variables.
 */
public class Zombie
{
  /**
   * This declares what type of a zombie this is
   * 0:Random walk zombie
   * 1:Line walk zombie
   * 2:Master zombie
   */
  public int type;
  
  /**
   * if zombie is added to the scene
   */
  public boolean isAddedToScene = false;
  
  /**
   * the number of Tiles a Zombie can traverse over 1 second
   */
  private double zombie_Speed = 1;
  
  /**
   * the amount of time between Zombie heading updates
   */
  private static long zombie_Decision_Rate = 2000;
  
  /**
   * the number of Tiles away that a Zombie can smell
   */
  private int zombie_Smell = 14;
  
  /**
   * Frame at which A* starts working
   */
  public int aStarFrame = -1;
  
  /**
   * whether or not a Zombie has scent of the Player
   */
  private boolean canSmell = false;
  
  /**
   * whether or not a Zombie has collided with an Object
   */
  private boolean collided = false;
  
  /**
   * whether or not this zombie interacted with the past self
   */
  public boolean interactedWithPS = false;
  
  /**
   * whether or not this zombie dies to the past self
   */
  public boolean diesToPastSelf = false;
  
  /**
   * frame at which the zombie bifurcated
   */
  public int bifurcatedFrame = 0;
  
  /**
   * the position at which the zombie bifurcated
   */
  public int positionForBifurcated = 0;
  
  /**
   * this Zombie's ID number
   */
  public int zombieID;
  
  /**
   * array of Tiles that lead to the Player
   */
  private LinkedList<Tile> path = new LinkedList<>();
  
  /**
   * a queue to hold Tiles that search for the Player for scent detection
   */
  private Queue<Tile> bfsQueue = new LinkedList<>();
  
  /**
   * a priority queue that holds Tiles examined while finding Zombie's path to
   * Player
   */
  private PriorityQueue<Tile> searchQueue = new PriorityQueue<>(1,
          new Comparator<Tile>()
          {
            
            public int compare(Tile one, Tile two)
            {
              if (one.cost > two.cost)
              {
                return 1;
              }
              if (one.cost < two.cost)
              {
                return -1;
              }
              return 0;
            }
          });
  /**
   * the direction the Zombie will head in degrees
   */
  private double heading;
  
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
  public Tile curTile;
  
  /**
   * The Zombie3D that represents this zombie in a 3D graphical world
   */
  public Zombie3D zombie3D;
  
  /**
   * health points of the zombie
   */
  private int life;
  
  /**
   * frame at which the zombie died
   */
  private int deathFrame;
  
  /**
   * frame at which the zombie spawns when bifurcated
   */
  private int bifurcatedZombieSpawnFrame = 0;
  
  /**
   * last time the zombie was updated
   */
  private long lastTimeUpdated = 0;
  
  /**
   * history of x-positions of the zombie that is used for the past self
   */
  private ArrayList<Double> xPos = new ArrayList<>();
  
  /**
   * history of y-positions of the zombie that is used for the past self
   */
  private ArrayList<Double> yPos = new ArrayList<>();
  
  /**
   * history of camera positions of the zombie that is used for the past self
   */
  private ArrayList<Double> cameraPos = new ArrayList<>();
  
  /**
   * Constructs a Zombie object with the specified heading, X coordinate position,
   * Y coordinate position, and the Tile it is in, preferably as given by its
   * X and Y coordinates
   */
  public Zombie(double heading, double positionX, double positionY,
                Tile curTile, int id, int life, int deathFrame, int type)
  {
    this.heading = heading;
    this.positionX = positionX;
    this.positionY = positionY;
    this.curTile = curTile;
    this.zombieID = id;
    this.life = life;
    this.deathFrame = deathFrame;
    this.type = type;
    if (LevelVar.zombie3D)
    {
      zombie3D = new Zombie3D(type);
    }
  }
  
  /**
   * Get health points of zombie
   *
   * @return health points
   */
  public int getLife()
  {
    return life;
  }
  
  /**
   * Set the health points of the zombie
   *
   * @param lifeLeft health points
   */
  public void setLife(int lifeLeft)
  {
    life = lifeLeft;
  }
  
  /**
   * Get the frame at which zombie died
   *
   * @return frame
   */
  public int getDeathFrame()
  {
    return deathFrame;
  }
  
  /**
   * Set the frame at which zombie died
   *
   * @param frameOfDeath frame of death
   */
  public void setDeathFrame(int frameOfDeath)
  {
    deathFrame = frameOfDeath;
  }
  
  /**
   * Add x position to the history of the zombie when past self spawns
   *
   * @param xPosition
   */
  public void addXPos(double xPosition)
  {
    xPos.add(xPosition);
  }
  
  /**
   * Add y position to the history of the zombie when past self spawns
   *
   * @param yPosition
   */
  public void addYPos(double yPosition)
  {
    yPos.add(yPosition);
  }
  
  /**
   * Add camera position to the history of the zombie when past self spawns
   *
   * @param cPosition
   */
  public void addCPos(double cPosition)
  {
    cameraPos.add(cPosition);
  }
  
  /**
   * Get the camera position from the zombie's history
   *
   * @return camera angle
   */
  public ArrayList<Double> getCameraPos()
  {
    return cameraPos;
  }
  
  /**
   * Get the x position from the zombie's history
   *
   * @return x position
   */
  public ArrayList<Double> getXPos()
  {
    return xPos;
  }
  
  /**
   * Get the y position from the zombie's history
   *
   * @return y position
   */
  public ArrayList<Double> getYPos()
  {
    return yPos;
  }
  
  /**
   * Get the frame at which the zombie bifurcated
   *
   * @return frame
   */
  public int getBifurcatedSpawnFrame()
  {
    return bifurcatedZombieSpawnFrame;
  }
  
  /**
   * Set the frame at which the zombie bifurcated
   *
   * @param frame bifurcation frame
   */
  public void setBifurcatedSpawnFrame(int frame)
  {
    bifurcatedZombieSpawnFrame = frame;
  }
  
  /**
   * Get the last time updated
   *
   * @return time updated
   */
  public long getLastTimeUpdated()
  {
    return lastTimeUpdated;
  }
  
  /**
   * Set the last time updated
   *
   * @param time time updated
   */
  public void setLastTimeUpdated(long time)
  {
    lastTimeUpdated = time;
  }
  
  /**
   * @return the Zombie class' zombie_Smell
   */
  public int getZombieSmell()
  {
    return this.zombie_Smell;
  }
  
  /**
   * @return the Zombie class' zombie_Decision_Rate
   */
  public static long getDecisionRate()
  {
    return zombie_Decision_Rate;
  }
  
  /**
   * Sets this Zombie object's collided value to value
   */
  public void setCollided(boolean value)
  {
    this.collided = value;
  }
  
  /**
   * @return this Zombie's collided value
   */
  public boolean getCollide()
  {
    return this.collided;
  }
  
  /**
   * Sets this Zombie's canSmell value to value
   */
  public void setSmell(boolean value)
  {
    this.canSmell = value;
  }
  
  /**
   * @return this Zombie's canSmell value
   */
  public boolean getSmell()
  {
    return this.canSmell;
  }
  
  /**
   * @return this Zombie's heading parameter
   */
  public double getHeading()
  {
    return this.heading;
  }
  
  /**
   * Sets this Zombie's heading parameter to heading
   */
  public void setHeading(double heading)
  {
    this.heading = heading;
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
    if (toRound - ((int) toRound) < 0.5)
    {
      return (int) toRound;
    }
    else
    {
      return (int) toRound + 1;
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
    if (!collided)
    {
      //Use A* path to player
      if (path.size() > 1)
      {
        makeHeading();
      }
      double moveX;
      double moveY;
      double step = (double) 1 / 40;
      if (this instanceof MasterZombie)
      {
        moveX = (Math.cos(Math.toRadians(heading)) * (zombie_Speed * LevelVar.masterZombieSpeedModifier)) * step;
        moveY = (Math.sin(Math.toRadians(heading)) * (zombie_Speed * LevelVar.masterZombieSpeedModifier)) * step;
      }
      else
      {
        moveX = (Math.cos(Math.toRadians(heading)) * zombie_Speed) * step;
        moveY = (Math.sin(Math.toRadians(heading)) * zombie_Speed) * step;
      }
      collide(moveX, moveY);
      if (path.size() > 1)
      {
        if (heading == 270)
        {
          positionY -= 0.045;
        }
        else if (heading == 180)
        {
          positionX -= 0.045;
        }
        else if (heading == 90)
        {
          positionY += 0.045;
        }
        else if (heading == 0)
        {
          positionX += 0.045;
        }
      }
      else
      {
        if (positionX > 0 && positionX <= LevelVar.house[0].length && positionY > 0 &&
                positionY <= LevelVar.house.length && !getCollide())
        {
          positionX += moveX;
          positionY += moveY;
          curTile = LevelVar.house[(int) positionX][(int) positionY];
        }
        //Avoid wall and zombie collision
        else if (getCollide())
        {
          positionX -= (moveX + 0.035);
          positionY -= (moveY + 0.035);
        }
        //Avoid wall and zombie collision
        else
        {
          positionX += 0.0;
          positionY += 0.0;
        }
      }
    }
    //Avoid wall and zombie collision
    else
    {
      positionX -= 0.035;
      positionY -= 0.035;
    }
  }
  
  /**
   * Calculates whether the Zombie has collided with an object
   * and sets the Zombie's collided value accordingly
   */
  public void collide(double desiredX, double desiredY)
  {
    setCollided(false);
    
    //Zombie collision
    for (Zombie z : LevelVar.zombieCollection)
    {
      if (z.positionX != this.positionX && z.positionY != this.positionY)
      {
        double diffX = (z.positionX - this.positionX);
        double diffY = (z.positionY - this.positionY);
        if ((diffX * diffX) + (diffY * diffY) <= 4)
        {
          setCollided(true);
        }
      }
    }
  
    //Wall collision
    double desiredPositionX = desiredX + positionX;
    double desiredPositionY = desiredY + positionY;
    double WALL_COLLISION_OFFSET = 0.25;
    if (LevelVar.house[round(desiredPositionX + WALL_COLLISION_OFFSET)][round(positionY)] instanceof Wall ||
            (LevelVar.house[round(desiredPositionX - WALL_COLLISION_OFFSET)][round(positionY)] instanceof Wall) ||
            (LevelVar.house[round(positionX)][round(desiredPositionY + WALL_COLLISION_OFFSET)] instanceof Wall) ||
            (LevelVar.house[round(positionX)][round(desiredPositionY - WALL_COLLISION_OFFSET)] instanceof Wall) ||
            (LevelVar.house[round(desiredPositionX + WALL_COLLISION_OFFSET)][round(desiredPositionY +
                    WALL_COLLISION_OFFSET)] instanceof Wall) ||
            (LevelVar.house[round(desiredPositionX - WALL_COLLISION_OFFSET)][round(desiredPositionY -
                    WALL_COLLISION_OFFSET)] instanceof Wall) ||
            (LevelVar.house[round(positionX)][round(positionY)] instanceof Wall))
    {
      setCollided(true);
    }
  }
  
  /**
   * Tests to see if this Zombie can smell the player
   *
   * @param searchDepth the Zombie's zombie_Smell
   * @param house       the 2d array of Tiles to search through
   * @return true if the Zombie can smell the player, otherwise returns false
   */
  public boolean scentDetection(int searchDepth, Tile[][] house)
  {
    int depth = 0;
    int numTillDepthIncrease = 0;
    boolean increaseDepth = false;
    ArrayList<Tile> visitedTiles = new ArrayList<>();
    Tile destTile = LevelVar.house[(int) Player.xPosition][(int) Player.yPosition];
    
    this.bfsQueue.clear();
    this.bfsQueue.add(this.curTile);
    numTillDepthIncrease++;
    this.curTile.setVisited(true);
    visitedTiles.add(this.curTile);
    while (!(this.bfsQueue.isEmpty()))
    {
      Tile currentTile = this.bfsQueue.poll();
      if (increaseDepth)
      {
        numTillDepthIncrease += this.bfsQueue.size();
        increaseDepth = false;
      }
      if (--numTillDepthIncrease == 0)
      {
        depth++;
        increaseDepth = true;
        if (depth > searchDepth)
        {
          for (Tile t : visitedTiles)
          {
            t.setVisited(false);
          }
          return false;
        }
      }
      if (currentTile == destTile)
      {
        for (Tile t : visitedTiles)
        {
          t.setVisited(false);
        }
        return true;
      }
      if (currentTile.neighbors.size() == 0) currentTile.setNeighbors(house);
      for (int i = 0; i < currentTile.neighbors.size(); i++)
      {
        if (!(currentTile.neighbors.get(i).visited))
        {
          this.bfsQueue.add(currentTile.neighbors.get(i));
          currentTile.neighbors.get(i).setVisited(true);
          visitedTiles.add(currentTile.neighbors.get(i));
        }
      }
    }
    for (Tile t : visitedTiles)
    {
      t.setVisited(false);
    }
    return false;
  }
  
  /**
   * A* path calculations adopted from:
   * http://www.redblobgames.com/pathfinding/a-star/introduction.html
   *
   * @param house
   */
  public void calcPath(Tile[][] house)
  {
    Tile destTile = house[(int) Player.xPosition][(int) Player.yPosition];
    curTile = house[(int) positionX][(int) positionY];
    
    searchQueue.clear();
    path.clear();
    
    Tile lastTile = null;
    curTile.setCost(0);
    searchQueue.add(curTile);
    LinkedHashMap<Tile, Tile> came_from = new LinkedHashMap<>();
    LinkedHashMap<Tile, Integer> cost_so_far = new LinkedHashMap<>();
    came_from.put(curTile, null);
    cost_so_far.put(curTile, 0);
    
    while (searchQueue.size() > 0)
    {
      Tile currentTile = searchQueue.poll();
      
      if (currentTile.xCor == destTile.xCor && currentTile.yCor == destTile.yCor)
      {
        lastTile = currentTile;
        reconstructPath(came_from, destTile);
        break;
      }
      
      List<Tile> neighbors = currentTile.getNeighbors();
      for (Tile neighbor : neighbors)
      {
        int new_cost = cost_so_far.get(currentTile) + 1;
        if (!cost_so_far.containsKey(neighbor) || new_cost < cost_so_far.get(neighbor))
        {
          cost_so_far.put(neighbor, new_cost);
          int priority = new_cost + distance();
          neighbor.setCost(priority);
          came_from.put(neighbor, currentTile);
          searchQueue.add(neighbor);
        }
      }
    }
    if (lastTile == null)
    {
      path.clear();
      return;
    }
  }
  
  /**
   * Heuristic for A*
   *
   * @return distance
   */
  private int distance()
  {
    int xCor = curTile.xCor;
    int yCor = curTile.yCor;
    int distance = ((int) Math.sqrt((xCor - ((int) Player.xPosition)) * (xCor - ((int) Player.xPosition)) + ((yCor -
            ((int) Player.yPosition)) * (yCor - ((int) Player.yPosition)))));
    return distance;
  }
  
  /**
   * Reconstruct the path from A* and store it in path
   *
   * @param came_from hash map of the path
   * @param finish    end tile which is the player's tile
   */
  private void reconstructPath(LinkedHashMap<Tile, Tile> came_from, Tile finish)
  {
    Tile currentTile = finish;
    while (currentTile != null)
    {
      path.addFirst(currentTile);
      currentTile = came_from.get(currentTile);
    }
  }
  
  /**
   * Used to test if the path was being calculated correctly
   */
  public void printPath()
  {
    for (Tile t : path)
    {
      System.out.print(t.xCor + "," + t.yCor + " ");
    }
    System.out.println();
    System.out.println("Player:" + Player.xPosition + "," + Player.yPosition);
  }
  
  /**
   * Used to tell the Zombie where to go once using the A* path obtained
   * from calcPath()
   */
  public void makeHeading()
  {
    Tile destTile = path.get(0);
    curTile = LevelVar.house[(int) positionX][(int) positionY];
    
    if (destTile.xCor == curTile.xCor
            && destTile.yCor == curTile.yCor)
    {
      path.removeFirst();
      destTile = path.get(0);
    }
    
    if (destTile.xCor > curTile.xCor)
    {
      setHeading(0.0);
      positionX += 0.01;
    }
    else if (destTile.xCor < curTile.xCor)
    {
      setHeading(180.0);
      positionX -= 0.01;
    }
    
    if (destTile.yCor > curTile.yCor)
    {
      setHeading(90.0);
      positionY += 0.01;
    }
    else if (destTile.yCor < curTile.yCor)
    {
      setHeading(270.0);
      positionY -= 0.01;
    }
  }
  
  /**
   * An abstract method inherited and implements by all sub-classes
   * of Zombie
   */
  public void makeDecision()
  {
  }
}