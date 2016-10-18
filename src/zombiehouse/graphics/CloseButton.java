package zombiehouse.graphics;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * @author Joshua Donckels.
 *         This button is used on the end game dialog, will close the game if pressed.
 */
public class CloseButton extends Button implements EventHandler<ActionEvent>
{
  Stage main;
  Stage dialog;
  
  /**
   * This button is used to close everything
   *
   * @param main   the main
   * @param dialog the end dialog's
   */
  public CloseButton(Stage main, Stage dialog)
  {
    this.main = main;
    this.dialog = dialog;
    this.setOnAction(this);
    this.setText("Close");
  }
  
  @Override
  public void handle(ActionEvent e)
  {
    main.close();
    dialog.close();
  }
}
