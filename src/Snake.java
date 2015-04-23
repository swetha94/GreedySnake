//File name: /Users/tongyi/Documents/Courses/JavaII/snake/src/main/java/edu/uga/cs1302/Snake.java
//package edu.uga.cs1302;

import java.util.ArrayList;
import javafx.scene.shape.Rectangle;

public class Snake {
    private ArrayList<Rectangle> body;

    public Snake() {
        body = new ArrayList<Rectangle>();
//        Rectangle head = new Rectangle(); 
           //In the very beginning, a snake has one node in the body.
           //And each node is represented by a rectangle.
           //We do not fill in each node's location and size yet
           //until we are in the class with main method (App.java),
           //where a yard is defined.
//        body.add(head);
    }

    public ArrayList<Rectangle> getBody() {
        return body;
    }

    public Rectangle getHead() {
        return body.get(0); //get the first segment of the body
    }
    
    public Rectangle getTail() {
        return body.get(body.size()-1); //get the last segment of the body
    }
}
