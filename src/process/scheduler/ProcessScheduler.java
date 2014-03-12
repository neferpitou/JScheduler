/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package process.scheduler;

import algorithms.CPUAlgorithms;
import graphs.InfoGraph;
import java.util.ArrayList;
import jobs.JobParser;

/**
 *
 * @author marcos
 */
public class ProcessScheduler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JobParser p = new JobParser();
        CPUAlgorithms c = new CPUAlgorithms(p);
        
        System.out.println("");
        System.out.println("Legend Key:\n");
        System.out.println("Red - SJF");
        System.out.println("Yellow - FCFS");
        System.out.println("Orange - Round Robin");
        
        // Draw three different JFrames for each type of metric being measured
        InfoGraph g = new InfoGraph("wait");
        InfoGraph h = new InfoGraph("turnaround");
        InfoGraph i = new InfoGraph("response");
    }
    
}
