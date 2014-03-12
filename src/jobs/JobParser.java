/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcos
 */
public class JobParser {

    public static final int JOB_POOL_SIZE = 100;
    static final File homeDir = new File(System.getProperty("user.home"));
    public static final File dir = new File(homeDir, "quanta");
    final File stats = new File(dir + "/stats.txt");
    JobQueue fcfs, sjf, rr;
    ArrayList<Integer> t;

    public JobParser() {
        createJobs();
        refreshJobPool();
        fcfs = createNewQueue("FCFS");
        sjf = createNewQueue("SJF");
        rr = createNewQueue("RR");
    }

    public void printInformation(String type) {
        String sCurrentLine;
        int wait_sum = 0;
        int turnaround_sum = 0;
        int response_sum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + "/" + type + "_wait_time"));
            BufferedReader br2 = new BufferedReader(new FileReader(dir + "/" + type + "_response_time"));
            BufferedReader br3 = new BufferedReader(new FileReader(dir + "/" + type + "_turnaround_time"));

            while ((sCurrentLine = br.readLine()) != null) {
                wait_sum += Integer.parseInt(sCurrentLine);
            }

            while ((sCurrentLine = br2.readLine()) != null) {
                response_sum += Integer.parseInt(sCurrentLine);
            }

            while ((sCurrentLine = br3.readLine()) != null) {
                turnaround_sum += Integer.parseInt(sCurrentLine);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Results of " + type + "(ms):");
        System.out.println("Wait Time: " + wait_sum / JOB_POOL_SIZE);
        System.out.println("Response Time: " + response_sum / JOB_POOL_SIZE);
        System.out.println("Turnaround Time: " + turnaround_sum / JOB_POOL_SIZE);
        System.out.println("");
    }
    
    public boolean addMoreJobs(Queue<Integer> arr, int sway) {
        int intrand = sway;
        boolean emptyList;
        int numElements;

        if (arr.size() <= intrand) {
            numElements = arr.size();
            emptyList = true;
        } else {
            numElements = intrand;
            emptyList = false;
        }

        for (int i = 0; i < numElements; i++) {
            boolean success = arr.add(t.get(i));

            if (!success) {
                break;
            }

            t.remove(i);
        }

        return emptyList;
    }

    public void createJobs() {
        OutputStreamWriter osw = null;
        FileOutputStream is = null;

        if (!dir.exists() && !dir.mkdirs()) {
            System.out.println("Unable to create " + dir.getAbsolutePath());
            System.exit(1);
        }

        try {
            is = new FileOutputStream(stats);
            osw = new OutputStreamWriter(is, "utf-8");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        Writer w = new BufferedWriter(osw);

        for (int i = 0; i < JOB_POOL_SIZE; i++) {
            try {
                double num = 1.0 + (Math.random() * 99);
                int intnum = (int) num;

                w.append(intnum + "\n");

            } catch (IOException e) {
                System.err.println("Problem writing quantas to the file.");
            }
        }

        try {
            w.close();
        } catch (IOException ex) {
            Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(2);
        }
    }

    public ArrayList<Integer> initMainQueue() {
        String sCurrentLine;
        ArrayList<Integer> arr = new ArrayList<>(JOB_POOL_SIZE);

        try {
            BufferedReader br = new BufferedReader(new FileReader(stats));

            while ((sCurrentLine = br.readLine()) != null) {
                arr.add(Integer.parseInt(sCurrentLine));
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return arr;
    }

    private JobQueue createNewQueue(String type) {
        JobQueue queue;

        if (type.equals("FCFS")) {
            queue = new JobQueue(t);
        } else if (type.equals("SJF")) {
            Collections.sort(t);
            queue = new JobQueue(t);
        } else {
            // Round Robin scheduling
            queue = new JobQueue(t);
        }
        
        for (int i = 0; i < JobQueue.QUEUE_SIZE; i++){
            t.remove(i);
        }

        return queue;
    }

    public JobQueue getJobQueue(String type) {
        if (type.equals("FCFS")) {
            return fcfs;
        } else if (type.equals("SJF")) {
            return sjf;
        } else if (type.equals("RR")) {
            return rr;
        } else {
            return null;
        }
    }
    
    public void setJobQueue(String type, JobQueue a){
        if (type.equals("FCFS")) {
            fcfs = a;        
        } else if (type.equals("SJF")) {
            sjf = a;
        } else {
            rr = a;
        }
    }

    public Queue<Integer> getQueue(String type) {
        if (type.equals("FCFS")) {
            return fcfs.getQueue();
        } else if (type.equals("SJF")) {
            return sjf.getQueue();
        } else {
            return rr.getQueue();
        }
    }

    public void refreshJobPool() {
        t = initMainQueue();
    }
}
