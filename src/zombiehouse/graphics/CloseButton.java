package zombiehouse.graphics;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Created by joshu on 10/16/2016.
 */
public class CloseButton extends Button implements EventHandler<ActionEvent>
{
  Stage main;
  Stage dialog;
  public CloseButton(Stage main, Stage dialog) {
    this.main = main;
    this.dialog = dialog;
    this.setOnAction(this);
    this.setText("Close");
  }
  @Override
  public void handle(ActionEvent e) {
    main.close();
    dialog.close();
  }
}
