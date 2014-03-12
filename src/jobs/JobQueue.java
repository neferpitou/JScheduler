/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jobs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 *
 * @author marcos
 */
public class JobQueue {

    public static final int QUEUE_SIZE = 20;
    Queue<Integer> q = new ArrayDeque<>(QUEUE_SIZE);

    public JobQueue(int initial_value){
        q = new ArrayDeque<>(QUEUE_SIZE);
        
        for (int i = 0; i < QUEUE_SIZE; i++){
            q.add(initial_value);
        }
    }
    
    public JobQueue(ArrayList<Integer> t) {
        for (int i = 0; i < QUEUE_SIZE; i++){
            q.add(t.get(i));
        }
    }
    
    public boolean add(Integer i){
        if (q.size() < QUEUE_SIZE){
            boolean success = q.offer(i);
            
            if (!success){
            System.out.println("Failed to insert element into queue");
            }
            
            return true;
        } else {
            return false;
        }
        
    }
    
    public int peek(){      
        return q.peek();                
    }
    
    public boolean delete(){
        Object success = q.poll();
        
        if (success == null){
            return false;
        } else {
            return true;
        }
    }
    
    public int size(){
        return q.size();
    }

    public Queue<Integer> getQueue() {
        return q;
    }
    
}
