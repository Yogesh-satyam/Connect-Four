package sample;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
        System.out.println("main");
    }
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println("start");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Layout.fxml"));
        GridPane rootNode = loader.load();
        MenuBar menuBar=createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane= (Pane) rootNode.getChildren().get(0);
        menuBar.prefHeightProperty().bind(menuPane.heightProperty());
        menuPane.getChildren().add(menuBar);
        controller=loader.getController();
        controller.createPlayground();
        Scene scene = new Scene(rootNode);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    private MenuBar createMenu(){
        Menu fileMenu=new Menu("File");
        MenuItem newGame=new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame());
        MenuItem resetGame=new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem=new SeparatorMenuItem();
        MenuItem exitGame =new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent -> exitGame());
        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);

        Menu helpMenu=new Menu("Help");
        MenuItem aboutGame=new MenuItem("About Connect4");
        aboutGame.setOnAction(actionEvent -> aboutGame());
        SeparatorMenuItem separator=new SeparatorMenuItem();
        MenuItem aboutMe=new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutMe());
        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);

        MenuBar menuBar=new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;
    }
    private void aboutMe() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("About Developer");
        alert.setContentText("I am Learning Java and i love to play with games." +
                "\n So,I developed this game for fun with moto learn with fun");
        alert.show();
    }
    private void aboutGame() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("About  Connect Four");
        alert.setTitle("How to Play?");
        alert.setContentText("Connect Four is a two-player connection \n" +
                " game in which the players first choose \n " +
                "a color and then take turns dropping colored \n " +
                "discs from the top into a seven-column, six-row \n" +
                " vertically suspended grid. The pieces \n " +
                "fall straight down, occupying the next available \n " +
                "space within the column. The objective of \n " +
                "the game is to be the first to form a \n " +
                "horizontal, vertical, or diagonal line of four of \n " +
                "one's own discs. Connect Four is a solved game. \n" +
                " The first player can always win by \n " +
                "playing the right moves.");
        alert.show();
    }
    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }


}
