package zombiehouse.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 9/15/2016.
 */
public class PastSelf
{
  private List<Integer> xPos;
  private List<Integer> yPos;
  private List<Integer> cameraPos;
  
  public PastSelf(List<Integer> xPos, List<Integer> yPos, List<Integer> cameraPos)
  {
    this.xPos = xPos;
    this.yPos = yPos;
    this.cameraPos = cameraPos;
  }
  
  
}
