package sample;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
public class Controller implements Initializable {
    private static final int COLUMNS=7;
    private static final int ROWS=6;
    private static final double CIRCLE_DIAMETER=80;
    private static final String discColour1="#24303E";
    private static final String discColour2="#4CAA88";
    private static String PLAYER_ONE="Player One";
    private static  String PLAYER_TWO="Player Two";
    private boolean isPlayerOneTurn=true;
    private boolean isAllowedToInsert = true;
    private final Disc[][] insertedDiscsArray=new Disc[ROWS][COLUMNS];
    public GridPane rootGridpane;
    public Pane insertedDiscsPane;
    public Label playerNameLabel;
    public TextField playerOneTextField,playerTwoTextField;
    public Button setNamesButton;
    public void createPlayground(){
        Shape rectangleWithCircle=gameStructureGrid();
        rootGridpane.add(rectangleWithCircle,0,1);
        List<Rectangle>rectangleList=clickAbleColumns();
        for (Rectangle rectangle:rectangleList) {
            rootGridpane.add(rectangle,0,1);
        }
        setNamesButton.setOnAction(actionEvent -> {
            PLAYER_ONE=playerOneTextField.getText();
            PLAYER_TWO=playerTwoTextField.getText();
            playerNameLabel.setText(PLAYER_ONE);
        });
    }
    private  Shape gameStructureGrid(){
        Shape rectangleWithCircle=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
        for (int row=0;row<ROWS;row++)
        {
            for (int col=0;col<COLUMNS;col++)
            {
                Circle circle=new Circle();
                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);

                circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                rectangleWithCircle=Shape.subtract(rectangleWithCircle,circle);
            }
        }
        rectangleWithCircle.setFill(Color.WHITE);
        return rectangleWithCircle;
    }
    private List<Rectangle> clickAbleColumns(){
        List<Rectangle>rectangleList=new ArrayList<>();
        for (int col=0;col<COLUMNS;col++)
        {
            Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

            final int Column = col;
            rectangle.setOnMouseClicked(event ->{
                    if(isAllowedToInsert){
                        isAllowedToInsert=false;
                        insertDisc(new Disc(isPlayerOneTurn), Column);
                    }
                    });
            rectangleList.add(rectangle);
        }
        return rectangleList;
    }
    private void insertDisc(Disc disc, int column){
        int row=ROWS-1;
        while (row>=0){
            if(getDiscIfPresent(row,column)==null){
                break;
            }
            row--;
        }
        if(row<0) return;
        insertedDiscsArray[row][column]=disc;
        insertedDiscsPane.getChildren().add(disc);
        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        int currentRow=row;
        TranslateTransition transition=new TranslateTransition(Duration.seconds(0.5),disc);
        transition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

        transition.setOnFinished(actionEvent -> {
            isAllowedToInsert=true;
           if (gameEnded(currentRow, column)) {
               gameOver();
               return;
			}
            isPlayerOneTurn=!isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);
        });
        transition.play();
    }
    private boolean gameEnded(int row, int column) {
        List<Point2D> verticalPoints= IntStream.rangeClosed(row-3,row+3)
                .mapToObj(r->new Point2D(r,column)).collect(Collectors.toList());

        List<Point2D> horizontalPoints= IntStream.rangeClosed(column-3,column+3)
                .mapToObj(c->new Point2D(row,c)).collect(Collectors.toList());
        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());

        return checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
    }
    private void gameOver() {
        String winner=isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO;
        System.out.println("Winner is: "+winner);
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("Winner is "+winner);
        alert.setContentText("Want to play again?");
        ButtonType yesBtn=new ButtonType("Yes");
        ButtonType noBtn=new ButtonType("NO,Exit");
        alert.getButtonTypes().setAll(yesBtn,noBtn);

        Platform.runLater(()->{
            Optional<ButtonType>btnClicked=alert.showAndWait();
            if(btnClicked.isPresent()&&btnClicked.get()==yesBtn){
                resetGame();
            }else {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    public void resetGame() {
        insertedDiscsPane.getChildren().clear();
        for(int row=0;row<insertedDiscsArray.length;row++){
            for (int col=0;col<insertedDiscsArray[row].length;col++){
                insertedDiscsArray[row][col]=null;
            }
        }
        isPlayerOneTurn=true;
        playerNameLabel.setText(PLAYER_ONE);
        createPlayground();
    }
    private boolean checkCombinations(List<Point2D> points) {
        int chain=0;
        for (Point2D point:points) {

            int rowIndexForArray=(int)point.getX();
            int columnIndexForArray=(int)point.getY();
            Disc disc=getDiscIfPresent(rowIndexForArray,columnIndexForArray);

            if(disc!=null && disc.isPlayerOneMove==isPlayerOneTurn){
                chain++;
                if(chain==4)
                    return true;
            }else {
                chain=0;
            }
        }
        return false;
    }
    private Disc getDiscIfPresent(int row,int column){
        if(row>=ROWS||row<0||column>=COLUMNS||column<0)
            return null;
        return insertedDiscsArray[row][column];
    }
    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove=isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayerOneMove?Color.valueOf(discColour1):Color.valueOf(discColour2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
