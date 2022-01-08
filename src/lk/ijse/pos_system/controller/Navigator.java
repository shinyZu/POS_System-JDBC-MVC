package lk.ijse.pos_system.controller;

import javafx.scene.input.MouseEvent;
import java.io.IOException;

public interface Navigator {

    void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException;

    void logoutOnAction(MouseEvent mouseEvent) throws IOException;

}
