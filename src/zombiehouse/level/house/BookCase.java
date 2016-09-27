package zombiehouse.level.house;

import javafx.scene.paint.Color;
import zombiehouse.common.LevelVar;

/**
 * Created by Haijin on 9/27/2016.
 */
public class BookCase extends Tile
{

  /**
   * constructor
   * @param xCor
   * @param yCor
   * @param zone
   */
  public BookCase(int xCor, int yCor, int zone)
  {
    super(xCor, yCor, zone);
  }

  /**
   * For printHouse method in Level class
   * @return
   */
  public char getChar()
  {
    return '&';
  }


  /**
   *
   * @return
   */
  public Color getColor()
 {
    return null;
  }




}
