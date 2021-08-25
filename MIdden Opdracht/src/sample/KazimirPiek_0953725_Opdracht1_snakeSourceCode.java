/* ik heb heeft lichte extra functionaliteit, omdat de snake steeds sneller
gaat bewegen naarmate je meer voedsel blokjes eet en de voedselblokjes hebben een random kleur
 */
/*
probeer recursieve formule met array ofzo
 */

package sample;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class KazimirPiek_0953725_Opdracht1_snakeSourceCode extends Application {
    //declare variables
    static int speed = 5;
    static int foodcolor = 0;
    static int width = 20;
    static int height = 20;
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 25;
    static String direction = "left"; //set the starting move direction to the left
    static boolean gameOver = false;
    static Random rand = new Random();
    int idCounter = 1;

    abstract class Segment { //make the basic segment class
        int id;
        int x;
        int y;
        String type;
        Segment next, prev = null;

        Segment(int id, int x, int y, String type, Segment next) { //constructor
            this.id = id;
            this.x = x;
            this.y = y;
            this.type = type;
            this.next = next;
            if (next != null) next.prev = this;
        }

        //have to test
        int GetY(int id){ //get the y coördinate of the segment with the corresponding id
            System.out.println("GetY = "+this.y+"ID = "+id);
            if(this.id == id) {
                System.out.println("this happens with ID: "+id+"and y: "+this.y);
                return this.y;
            }
            else next.GetY(id);
            return 1;
        }

        int GetX(int id){ //get the x of the coordinate of the segment with the corresponding id
            if(this.id == id){
                System.out.println("reset on return");
                return (this.x);
            }
            else{
                System.out.println("reset on recall");
                next.GetX(id);
            }
            System.out.println("this never actually happens");
            return 1;
        }

        int GetHeadY(){ //get the y coordinate of the head segment
            if(this.type == "head") {
            return this.y;
            }
            else next.GetHeadY();
            return 1;
        }

        int GetHeadX(){//get the x coordinate of the head segment
            if(this.type == "head") {
                return this.x;
            }
            else next.GetHeadX();
            return 1;
        }

        void append(Segment game){ //append another segment
            if(next == null) next = game;
            else next.append(game);
        }

        void Move(String direction) { //move the snake
            if (type == "head") {

                if (direction == "up") this.y--;
                if (direction == "down") this.y++;
                if (direction == "left") this.x--;
                if (direction == "right") this.x++;

            } else {
                this.x = next.x;
                this.y = next.y;
            }

            if (this.type != "head") next.Move(direction);
        }

    }

    class HeadSegment extends Segment { //the headsegment
        boolean head;

        HeadSegment(int id, int x, int y, String type, boolean head, Segment next){
            super(id, x, y, type, next);
            this.head = head;
        }

    }

    class SnakeSegment extends Segment { //a regular body segment
        boolean snake;

        SnakeSegment(int id, int x, int y, String type, boolean snake, Segment next){
            super(id, x, y, type, next);
            this.snake = snake;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //making the UI
        VBox root = new VBox();
        Canvas c = new Canvas(width * cornersize, height * cornersize);
        GraphicsContext gc = c.getGraphicsContext2D();
        root.getChildren().add(c);

        Segment game = new HeadSegment(1, width/2, height/2, "head", true, null); //make the headsegment


        new AnimationTimer() {
            long lastTick = 0;

            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    tick(gc, game);
                    return;
                }

                if (now - lastTick > 1000000000 / speed) {
                    lastTick = now;
                    tick(gc, game);
                }
            }

        }.start();

        Scene scene = new Scene(root, width * cornersize, height * cornersize);


        scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> { //check if a key has been pressed and act accordingly
            if (key.getCode() == KeyCode.UP) {
                direction = "up";
            }
            if (key.getCode() == KeyCode.LEFT) {
                direction = "left";
            }
            if (key.getCode() == KeyCode.DOWN) {
                direction = "down";
            }
            if (key.getCode() == KeyCode.RIGHT) {
                direction = "right";
            }

        });




        //snake.add(new Corner(width / 2, height / 2));

        primaryStage.setTitle("Snake");
        primaryStage.setScene(scene);
        // primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public void tick(GraphicsContext gc, Segment game) {
        if (gameOver) { //give score and tell the player its game over when its game over
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 100, 250);
            gc.fillText("Final Score: " + (speed - 5), 100, 350);
            return;
        }

        switch (direction) { //change direction depending on the value of the String direction
            case "up":

                game.Move("up");
                if (game.GetY(1) < 0) {
                    gameOver = true;
                }
                break;
            case "down":
                game.Move("down");
                if (game.GetY(1) > height) {
                    gameOver = true;
                }
                break;
            case "left":
                game.Move("left");
                if (game.GetX(1) < 0) {
                    gameOver = true;
                }
                break;
            case "right":
                game.Move("right");
                if (game.GetX(1) > width) {
                    gameOver = true;
                }
                break;

        }


        if (foodX == game.GetX(1) && foodY == game.GetY(1)) { //act if the head segment eats some food
            idCounter++;
            Segment add = new SnakeSegment(idCounter, game.GetHeadY() + 1,game.GetHeadX(), "bodyl", true, null);
           // game.SetHeadToBody();=-
            game.append(add);//add a segment
            newFood(game);//spawn a new food block
        }



        for (int i = 2; i < idCounter; i++) { //set game over to true if the snake hits a side of the playing field
            if (game.GetX(1) == game.GetX(i) && game.GetY(1) == game.GetY(i)) {
                gameOver = true;
            }
        }

        //do some UI things
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cornersize, height * cornersize);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("Score: " + (speed - 5), 10, 30);

        Color cc = Color.WHITE;

        switch (foodcolor) { //change the food color
            case 0:
                cc = Color.PURPLE;
                break;
            case 1:
                cc = Color.LIGHTBLUE;
                break;
            case 2:
                cc = Color.YELLOW;
                break;
            case 3:
                cc = Color.PINK;
                break;
            case 4:
                cc = Color.ORANGE;
                break;
        }
        gc.setFill(cc);
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

 //       game.Move(gc);


        for (int i = 1; i <= idCounter; i++) {//draw the snake
            System.out.println("start X");
            int x = game.GetX(i);
            System.out.println("done X, Start Y");
            int y = game.GetY(i);
            System.out.println("done Y");
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(x * cornersize, y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.GREEN);
            gc.fillRect(x * cornersize, y * cornersize, cornersize - 2, cornersize - 2);
        }
    }


    public void newFood(Segment game) { //spawn a new food block
        start:
        while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (int i = 1; i < idCounter; i++) {
                if (game.GetX(idCounter) == foodX && game.GetY(idCounter) == foodY) {
                    continue start;
                }
            }
            foodcolor = rand.nextInt(5);
            speed++;
            break;

        }
    }




    public static void main(String[] args) {
        launch(args);
    }
}
