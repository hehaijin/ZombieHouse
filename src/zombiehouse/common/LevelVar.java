package zombiehouse.common;

import zombiehouse.level.PastSelf;
import zombiehouse.level.house.Tile;
import java.util.ArrayList;
import java.util.Random;
import zombiehouse.level.zombie.Zombie;

/**
 * @author Rob
 *
 * Container class - no methods
 * 
 * Values here are meant to be accessed by a variety of elements
 * and the bottom flags are for changing game play / terminal prints
 */
public class LevelVar
{
  /**
   * Contains a grid of Tile objects - the architecture of the current level
   * The house Tile[][] is used by both graphics and zombies
   * initialized by Level and filled by ProGen
   */
  public static Tile[][] house;
  
  /**
   * Contains a complete list of Zombies spawned in the current level
   * The zombieCollection is used by both graphics and zombies
   * initialized by Level and filled by ProGen
   */
  public static ArrayList<Zombie> zombieCollection;

  public static ArrayList<Zombie> pastZombieCollection;

  public static ArrayList<Zombie> interactedWithZombieCollection;

  public static ArrayList<PastSelf> pastSelfCollection;

  public static ArrayList<Zombie> bifurcatedCollection;

  /**
   * Is the single instance of Random used by all of level generation
   * (It's seed is saved in level as it is not as public a variable)
   */
  public static Random rand;
  
  /**
   * The (internal) level number - or number of completed levels so far
   * is used in level generation to increase dificulty
   */
  public static int levelNum = 0;

  /**
   * This is the incremental Zombie spawn modifier
   * Note: at final level (levelNum = 4) there will be a 2% chance to spawn
   *       which is the 2x the chance of the first level (and makes for a challenging density)
   */
  public static double spawnMax= 10;

  public static final double masterZombieSpeedModifier = 2;

  public static double spawnRate = 0.03;
  
  /**
   * This is the (percent) chance to spawn a pillar at each 'opening'
   * (Not a hugely 'public' scope variable, but was trying to contain all 
   * level progression elements in LevelVar)
   */
  public static double pillarSpawnChance = 0.2;
  
  /**
   * A debugging flag
   * When false, Zombies will not be added to the level
   */
  public static boolean SPAWN_MONSTERS = true;
  
  /**
   * A debugging flag
   * When true, will spawn the MasterZombie regardless of spawnMonsters flag
   */
  public static final boolean SPAWN_MASTER = false;
  
  /**
   * A 2d animation flag.
   * If playing the 2d view game, this will allow for 'limited' lighting
   */
  public static boolean WITH_SIGHT = false;
  
  /**
   * A debugging flag
   * When true, a stream of text related to level generation will be printed to terminal
   */
  public static final boolean LEVEL_DEBUG_TEXT = false;
  
  /**
   * A debugging flag
   * When true, a stream of text related to zombie handling will be printed to terminal
   */
  public static final boolean ZOMBIE_DEBUG_TEXT = false;
  
  /**
   * Walking speed of zombies
   */
  public static double zombieSpeed = 0.5;
  
  public static boolean zombie3D;
  
  public static boolean HOUSE_PRESENTATION = false;

  public static double bookcasechance=0.10; //controls the chance of bookcase spawn relative to wall.

  public static double tapestrychance=0.20; // the chance of tapestry spawn relative to wall.
}
