package zombiehouse.common;

import zombiehouse.graphics.Attack3D;
import zombiehouse.graphics.Player3D;

/**
 * Class to hold player positioning/stamina data.
 *
 * @author Maxwell Sanchez
 */
public class Player
{
  public static double xPosition = 1.0;
  public static double yPosition = 1.0;
  public static double stamina = 5.0;
  public static double staminaRegen = 0.20;
  public static double maxStamina = 5.0;
  public static double playerSpeed = 2.0;
  public static int life = 5;
  public static Player3D player3D = new Player3D();
  public static Attack3D attack3D = new Attack3D();
  public static int playerSightRange = 7;
  
  /**
   * Used to test the zombie house generation and player spawn point
   * @return character used to represent the player
   */
  public static char getChar()
  {
    return 'O';
  }
}
