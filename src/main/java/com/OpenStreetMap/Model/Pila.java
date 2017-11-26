package com.OpenStreetMap.Model;

import java.util.EmptyStackException;
import java.util.Stack;

public class Pila {
    private Stack<Node> stack;

    public Pila() {
        this.stack = new Stack<>();
    }
    
    public boolean isEmpty(){
        return this.stack.empty();
    }
    
    public void push(Node n){
        this.stack.push(n);
    }
    
    public Node pop() throws EmptyStackException{
        if(!isEmpty()){
            return this.stack.pop();
        }else{
            System.out.println("Stack vuoto");
            return null;
        }
    }
    
    public Node top() throws EmptyStackException{
        if(!isEmpty()){
            return this.stack.peek();
        }else{
            System.out.println("Stack vuoto");
            return null;
        }
    }
    
    public int getLength(){
        return this.stack.capacity();
    }
    
    public int getSize(){
        return this.stack.size();
    }
    
}
