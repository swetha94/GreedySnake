//package edu.uga.cs1302;

//In directory src,
//Compile: mvn package
//Run: java -cp target/snake-1.0-SNAPSHOT.jar edu.uga.cs1302.App

//TODO: (this is optional) choose an algorithm to make the snake move automatically.
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;
import java.util.Random;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;

public class App extends Application {
    private int NUM_ROWS; //number of rows of a grid
    private int NUM_COLS; //number of cols of a grid
    private int BLOCK_SIZE; //the number of pixels of each grid

    private Pane yard;
    private BorderPane borderPane;
    private TextField scoreTextField; 

    private Snake snake;
    private ArrayList<Rectangle> body; //the body of snake can be think as a list of rectangles.
    private Rectangle head; //head of the snake
    private Rectangle tail; //tail of the snake
    private int snakeHeadRow; //the row that the snake head is located
    private int snakeHeadCol; //the col that the snake head is located
    private int snakeTailRow; //the row where the snake tail is located.
    private int snakeTailCol; //the col where the snake tail is located.

    private Circle egg;
    private int eggRow; //the row of the grid where the egg resides
    private int eggCol; //the col of the grid where the egg resides

    private int numEggsEaten; //number of eggs eaten by the snake so far

    private static Random rand;

    public static void main(String[] args) {
        launch(args);
    }

    public App() {
        NUM_ROWS = 12; //30; //You can choose a larger value if you like
        NUM_COLS = 12; //30;
        BLOCK_SIZE = 20;
        yard = new Pane();  
           //Cannot use yard as a stack pane object, or the rectangle is always in the middle of the stack pane.
           //Reason: A StackPane is a managed layout pane - it controls the layout of the items you place in it 
           //(by default centering items in the stack). So it doesn't matter what co-ordinates you give the Rectangle, 
           //when you place it in the StackPane, the layout manager will move your rectangle so that it is in the center of the stack.

        snake = new Snake();
        body = snake.getBody();

        rand = new Random();

        //Let the snake start at a random place in the grid.
        snakeTailRow = snakeHeadRow = rand.nextInt(NUM_COLS-2) +1; //Do not start with position next to the wall
        snakeTailCol = snakeHeadCol = rand.nextInt(NUM_ROWS-2) +1; //Do not start with position next to the wall
        addToHead(); //You need to implement this method to see the snake
        numEggsEaten = 0;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Greedy Snake");
        final Group root = new Group();

        Canvas canvas = new Canvas(NUM_COLS * BLOCK_SIZE, NUM_ROWS * BLOCK_SIZE);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawGrid(canvas);

        yard.getChildren().add(canvas);

        ////canvas is transparent, to set its background, put it in a pane called yard
        //yard.setStyle("-fx-background-color: lightgreen"); //too many color is confusing

        putEgg();

        ////add yard to group root
        borderPane = new BorderPane();
        borderPane.setCenter(yard);
        
        scoreTextField = new TextField("Eat " + numEggsEaten + " eggs; snake size: " + body.size());
        scoreTextField.setEditable(false);
        scoreTextField.setFocusTraversable(false); //do not get focus for this text field
        scoreTextField.setStyle("-fx-font-size: 16px;"
            + "-fx-font-style: italic;"
            + "-fx-font-weight: bold;"
            + "-fx-font-family: fantasy;"
            + "-fx-text-fill: green;"
            //+ "-fx-background-color: aqua"
            );
        borderPane.setTop(scoreTextField);

        root.getChildren().add(borderPane);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        yard.requestFocus(); //focus needed for key pressed event handler
        yard.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                switch (ke.getCode()) {
                    case LEFT:
//                         //Call invalidMoveDirection Method to detect case
//                         //when the neck is to the left of the head,
//                         //then the snake cannot move to the left.
			if (invalidMoveDirection() == Direction.LEFT)
			  issueWarning("Bite the neck(left).");
//                        
//                         //Call leftBodyNode method to find out whether 
//                         //there is a left node next to the mouth.
                         if (leftBodyNode() != null)
                            issueWarning("Bite a left node.");
			 if(snakeHeadRow < 0){
			     //snakeHeadRow = 0;
			     //head.setX(snakeHeadRow * BLOCK_SIZE);
			     issueWarning("Hit left wall");
                         move(Direction.LEFT); 
			 }
                         break;

                    case RIGHT:
                         //TODO: what happens if right arrow key pressed?
                         //The snake may bite its own neck.
			if(invalidMoveDirection()==Direction.RIGHT)
			  issueWarning("Bite the neck(right).");
                         //The snake may also bite a body node to its right
			if (rightBodyNode() != null)
			    issueWarning("Bite a right node");
                         //The snake may also hit the right wall.
			if(snakeHeadRow == NUM_ROWS)
			    issueWarning("Hit right wall");
                         //If none of the above happens, move the snake to the right.
                    	
                    	move(Direction.RIGHT); 

                         break;

                    case UP:
                         //TODO: handle the case when the up arrow key is pressed. 
                    	if(invalidMoveDirection()==Direction.UP)
			  issueWarning("Bite the neck(up).");
                         //The snake may also bite a body node to its right
			if (upBodyNode() != null)
			    issueWarning("Bite a upper node");
			if(snakeHeadCol < 0)
			    issueWarning("Hit the top");                    
                    	move(Direction.UP); 
			break;
                    case DOWN:
                         //TODO: handle the case when the down arrow key is pressed. 
                    	if(invalidMoveDirection()==Direction.DOWN)
			  issueWarning("Bite the neck(down).");
                         //The snake may also bite a body node to its right
			if (downBodyNode() != null)
			    issueWarning("Bite a lower node");                    	
			if(snakeHeadCol == NUM_COLS)
			    issueWarning("Hit the bottom");
                   	move(Direction.DOWN);
                         break; 
                }

                //Check whether the snake can eat the egg after the movement.
                eatEgg();
           }
        });
    }

    //Find out the relative position of the snake head and its neck.
    //Then if another node in the body is in the direction of the mouth, 
    //the snake is going to bite itself.
    //Note that if the mouth of the snake is not facing one of its body node,
    //the snake does not bite itself. 
    //There are four possible ways for a snake to bite itself:
    //if the neck is to the left of the snake head and the snake is moving right,
    //   if another body node is in the right direction of the head,
    //   then the snake bites itself.
    //if the neck is to the right of the snake head and the snake is moving left,
    //   if another body node is in the left direction of the head,
    //   then the snake bites itself.
    //if the neck is below the snake head and the snake is moving up,
    //   if another body node is in the up direction of the head,
    //   then the snake bites itself.
    //if the neck is up the snake head and the snake is moving down,
    //   if another body node is in the down direction of the head,
    //   then the snake bites itself.

    //Return any body node other than the neck
    //that is to the right of the head.
    //If there is no such node, return null.
    public Rectangle rightBodyNode() {
        Rectangle node;
        int currRow, currCol;
        for (int i = 2; i < body.size(); i++) { 
            //Why do we start i from 2?
            //The head is indexed at 0, the neck is indexed at 1.
            //So i = 2 means all the nodes below the neck.
            node = body.get(i);
            
            //node is a rectangle object in javafx, 
            //its y coordinate is in pixels.
            //We need to tranlate y coordinate to row number
            //by dividing y by BLOCK_SIZE.
            currRow = (int)(node.getY()/BLOCK_SIZE);
            currCol = (int)(node.getX()/BLOCK_SIZE); 
            //if the node is right to the head, return it.
            if (snakeHeadRow == currRow && snakeHeadCol +1 == currCol)
               return node; 
        }
        //There is no body node of the snake to the right of the head.
        return null;
    }

    //TODO: you finish defining the method.
    public Rectangle leftBodyNode() {
        Rectangle node;
        int currRow, currCol;
        for (int i = 2; i < body.size(); i++) { 
            //Why do we start i from 2?
            //The head is indexed at 0, the neck is indexed at 1.
            //So i = 2 means all the nodes below the neck.
            node = body.get(i);
            
            //node is a rectangle object in javafx, 
            //its y coordinate is in pixels.
            //We need to tranlate y coordinate to row number
            //by dividing y by BLOCK_SIZE.
            currRow = (int)(node.getY()/BLOCK_SIZE);
            currCol = (int)(node.getX()/BLOCK_SIZE); 
            //if the node is right to the head, return it.
            if (snakeHeadRow == currRow && snakeHeadCol -1 == currCol)
               return node; 
    }
        return null;
    }

    //TODO: you finish defining the method.
    public Rectangle upBodyNode() {
        Rectangle node;
        int currRow, currCol;
        for (int i = 2; i < body.size(); i++) { 
            //Why do we start i from 2?
            //The head is indexed at 0, the neck is indexed at 1.
            //So i = 2 means all the nodes below the neck.
            node = body.get(i);
            
            //node is a rectangle object in javafx, 
            //its y coordinate is in pixels.
            //We need to tranlate y coordinate to row number
            //by dividing y by BLOCK_SIZE.
            currRow = (int)(node.getY()/BLOCK_SIZE);
            currCol = (int)(node.getX()/BLOCK_SIZE); 
            //if the node is right to the head, return it.
            if (snakeHeadCol == currCol && snakeHeadRow +1 == currRow)
               return node; 
	}
       return null;
    }

    //TODO: you finish defining the method.
    public Rectangle downBodyNode() {
        Rectangle node;
        int currRow, currCol;
        for (int i = 2; i < body.size(); i++) { 
            //Why do we start i from 2?
            //The head is indexed at 0, the neck is indexed at 1.
            //So i = 2 means all the nodes below the neck.
            node = body.get(i);
            
            //node is a rectangle object in javafx, 
            //its y coordinate is in pixels.
            //We need to tranlate y coordinate to row number
            //by dividing y by BLOCK_SIZE.
            currRow = (int)(node.getY()/BLOCK_SIZE);
            currCol = (int)(node.getX()/BLOCK_SIZE); 
            //if the node is right to the head, return it.
            if (snakeHeadCol == currCol && snakeHeadRow -1 == currRow)
               return node; 
	}
       return null;
    }

    private void eatEgg() {
        if ( isEggNextToSnakeHead() ) {
        	
//        	addToTail();
        	// have to add (missing from original code)
        	tail = snake.getTail();
     	   	snakeTailRow = (int)(tail.getX()/BLOCK_SIZE);
     	   	snakeTailCol = (int)(tail.getY()/BLOCK_SIZE);
        	
           if (snakeTailRow +1 < NUM_ROWS-1) { 
        	   //try to add new tail to down direction
               //TODO: attach a new node to the down direction of current tail 
        	   addToTail();
           }
           else if (snakeTailRow -1 > 0) { 
        	   //try to add new tail from up direction 
               //TODO: attach a new node to the up direction of current tail 
        	   addToTail();
           }
           else if (snakeTailCol +1 < NUM_COLS-1) {
        	   //try to add new tail from right position
               //TODO: attach a new node to the right of current tail 
        	   addToTail();
           }
           else if (snakeTailCol -1 > 0) { //try to add new tail from left position
              //TODO: attach a new node to the left of current tail
        	   addToTail();
           } 
           else {
        	   issueWarning("The snake cannot grow without touching a wall. Game over.");
           }
          
           //report the number of eggs eaten
           numEggsEaten++;
           

           scoreTextField.setText("Eat " + numEggsEaten + " eggs; snake size: " + body.size());
           if (numEggsEaten != (body.size() -1)) {
        	   //This is to verify the correctness of our code
        	   issueWarning("egg eaten != body size -1.");
           }

           putEgg();
        }
    }

    //Base on the current locations of head and neck,
    //find out which moving direction is invalid.
    private Direction invalidMoveDirection() {
        int size = body.size();
        if (size == 1)
           return null; //one node snake can move in anywhere

        Rectangle neckNode = body.get(1);
        int neckNodeCol = (int)(neckNode.getX() / BLOCK_SIZE);
        int neckNodeRow = (int)(neckNode.getY() / BLOCK_SIZE);

        //When the head and neck are in the same column,
        //the head is moving either up or down.
        //If the snake is heading down,
        //then Direction.UP would be invalid for the snake,
        //otherwise, the snake is going to bite its own neck.
   if (neckNodeCol == snakeHeadCol && snakeHeadRow == neckNodeRow +1)
       return Direction.UP;
   else if(neckNodeCol == snakeHeadCol && snakeHeadRow == neckNodeRow -1)
       return Direction.DOWN;
   else if (neckNodeRow == snakeHeadRow && snakeHeadCol == neckNodeCol +1)
	return Direction.LEFT;
   else if (neckNodeRow == snakeHeadRow && snakeHeadCol == neckNodeCol +1)
	return Direction.RIGHT;
   else
	return null;
    }
    //To move a snake, a simple way is to move the tail and attach that tail to the front of the head.  
    public void move(Direction dir) { 
        switch (dir) { 
            case LEFT: 
                 //TODO: what happens when a snake is moving left? 
                 //Hint: you may need to use removeTail and addToHead method.
                 //But before you call addToHead method, you need to define some parameter(s)
                 //for the new head.
            	
            	
            	snakeHeadRow--;
            	addToHead();
            	removeTail();
            	
            	
                break; 
            case RIGHT: 
                 //TODO: what happens when a snake is moving right? 
            	
            	snakeHeadRow++;
            	addToHead();
            	removeTail();
            	
                break; 
            case UP: 
                 //TODO: what happens when a snake is moving up? 
            	
            	
            	snakeHeadCol--;
            	addToHead();
            	removeTail();
            	
                 break; 
            case DOWN: 
                 //TODO: what happens when a snake is moving down? 
            	
            	
            	snakeHeadCol++;
            	addToHead();
            	removeTail();
            	
                 break; 
        } 
        
      // test
//      scoreTextField.setText("(" + snakeHeadRow + ", " + snakeHeadCol + "), " + "(" + snakeTailRow + ", " + snakeTailCol + ")");
      scoreTextField.setText("Eat " + numEggsEaten + " eggs; snake size: " + body.size());
    }

    //TODO: remove the tail.
    //You need to consider the case when the snake has one, two, or more nodes
    //before the tail is to be removed.
    //In some cases, the head may be affected.
    //Method removeTail is called by method move. 
    private void removeTail() {

    	
    	tail = snake.getTail();
    	yard.getChildren().remove(tail);
    	body.remove(body.size()-1);
    	tail = snake.getTail();
    	snakeTailRow = (int)(tail.getX() / BLOCK_SIZE);
    	snakeTailCol = (int)(tail.getY() / BLOCK_SIZE);
    	
    }

    //TODO: add to the head.
    //Method addToHead is called by method move.
    //Do not forget to add the head (a rectangle object) to yard (a pane).
    //Otherwise, you do not see the head in the screen.
    private void addToHead() {
        //Add a new node in front of the head, that new node becomes the new head.
        //The location of new head depends the moving direction.

    	Rectangle node = new Rectangle();
    	body.add(0, node);
    	head = snake.getHead();
    	head.setX(snakeHeadRow * BLOCK_SIZE);
    	head.setY(snakeHeadCol * BLOCK_SIZE);
    	head.setWidth(BLOCK_SIZE);
    	head.setHeight(BLOCK_SIZE);
    	head.setFill(Color.RED);
    	yard.getChildren().add(head);
    	
    	if (body.size() > 1) {
    		for (int i = 1; i < body.size(); i++) {
    			body.get(i).setFill(Color.BLUE);
    		}
    	}

    	
    }

    //TODO: add to the tail.
    //This method is being called when the snake eat an egg
    //and a new segment is added to its tail.
    private void addToTail() {
    	Rectangle node = new Rectangle(); 
    	body.add(node);
    }

    public void drawGrid(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setStroke(Color.LIGHTBLUE);
        gc.setLineWidth(1);

        //Draw vertical lines
        for (int i = 1; i < NUM_COLS; i++)
            gc.strokeLine(i * BLOCK_SIZE, 0, i * BLOCK_SIZE, NUM_ROWS * BLOCK_SIZE);

        //Draw horizontal lines
        for (int i = 1; i < NUM_ROWS; i++)
            gc.strokeLine(0, i * BLOCK_SIZE, NUM_COLS * BLOCK_SIZE, i * BLOCK_SIZE);
    }
    
    public void putEgg() {
        if (egg != null) //remove the old egg, if any. This line will guarantee that there is only one egg in the screen.
           yard.getChildren().remove(egg); 

        do {
            eggRow = rand.nextInt(NUM_ROWS); //row of the left corner of the egg
            eggCol = rand.nextInt(NUM_COLS); //col of the left corner of the egg
        } while ( isEggOnSnake(eggRow, eggCol) 
                  || (eggRow == 0 && eggCol == 0) //egg is in one of the four corners; the snake cannot eat that egg.
                  || (eggRow == NUM_ROWS-1 && eggCol == 0)
                  || (eggRow == 0 && eggCol == NUM_COLS-1)
                  || (eggRow == NUM_ROWS-1 && eggCol == NUM_COLS-1)
                  || isEggNextToSnakeHead());  

        egg = new Circle((eggCol + eggCol +1) * BLOCK_SIZE/2., (eggRow + eggRow +1) * BLOCK_SIZE/2., BLOCK_SIZE/2, Color.ORANGE);
            //Divide by 2. not just an integer 2 in the above formula. Or sometimes the egg might not be inside a block.
        yard.getChildren().add(egg); 
    }

    //TODO: see whether the egg is to the left, right, up, or down of the snake head.
    public boolean isEggNextToSnakeHead() {
    	
    	if (eggRow == snakeHeadCol && eggCol == snakeHeadRow) {
    		return true;
    	}    	
    	
        return false; //TODO: You need to modify this statement.
    }

    //TODO: the egg cannot collide with the nodes of the body of the snake.
    public boolean isEggOnSnake(int eggRow, int eggCol) {
        return false; //TODO: you need to add more to this method.
    }

    private void issueWarning(String message) {
        //Source: https://docs.oracle.com/javafx/2/api/javafx/stage/Stage.html
        TextField warningTextField = new TextField(message);
        warningTextField.setStyle("-fx-font-size: 16px;"
            + "-fx-font-style: italic;"
            + "-fx-font-weight: bold;"
            + "-fx-font-family: fantasy;"
            + "-fx-text-fill: blue;"
            //+ "-fx-background-color: aqua"
        );

        Scene scene = new Scene(warningTextField); 
        Stage stage = new Stage();
        stage.setScene(scene); 
        //stage.sizeToScene(); //Does not seem to help to get the text field size to the scene. 
        stage.showAndWait();
        System.exit(0);
   }
}
