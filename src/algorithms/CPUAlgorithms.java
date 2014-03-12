/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jobs.JobParser;
import jobs.JobQueue;

/**
 *
 * @author marcos
 */
public class CPUAlgorithms {

    JobParser parser;

    public CPUAlgorithms(JobParser p) {
        parser = p;
        startSJF(p.getJobQueue("SJF"));
        startFCFS(p.getJobQueue("FCFS"));
        startRR(p.getJobQueue("RR"));
    }

    private void fcfs(JobQueue jobQueue, boolean sjf_flag) {
        boolean emptyJobPool = false;
        JobQueue wait_queue = new JobQueue(0);
        JobQueue turnaround_queue = new JobQueue(0);
        JobQueue response_queue = new JobQueue(-1); // unused
        File wait_time_file, response_time_file, turnaround_time_file;
        Writer wait_writer = null, turnaround_writer = null, response_writer = null;
        String type;

        if (sjf_flag) {
            wait_time_file = new File(JobParser.dir + "/SJF_wait_time");
            response_time_file = new File(JobParser.dir + "/SJF_response_time");
            turnaround_time_file = new File(JobParser.dir + "/SJF_turnaround_time");
            type = "SJF";

        } else {
            wait_time_file = new File(JobParser.dir + "/FCFS_wait_time");
            response_time_file = new File(JobParser.dir + "/FCFS_response_time");
            turnaround_time_file = new File(JobParser.dir + "/FCFS_turnaround_time");
            type = "FCFS";
        }

        try {
            // Write length of wait time, turnaround time, response time
            // into files
            wait_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wait_time_file), "utf-8"));
            response_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(response_time_file), "utf-8"));
            turnaround_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(turnaround_time_file), "utf-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CPUAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        while (!emptyJobPool) {
            int wait_time = -1;
            int turnaround_time = -1;

            int quanta = jobQueue.peek();
            jobQueue.delete();

            wait_time = wait_queue.peek();
            wait_queue.delete();

            turnaround_time = turnaround_queue.peek();
            turnaround_time += quanta;
            turnaround_queue.delete();

            // Since all queues are the same size
            // all incrementations can be done in one loop
            for (int i = 0; i < wait_queue.size(); i++) {
                int wait_info = wait_queue.peek();
                int turnaround_info = turnaround_queue.peek();

                wait_queue.delete();
                turnaround_queue.delete();

                wait_info += quanta;
                turnaround_info += quanta;

                wait_queue.add(wait_info);
                turnaround_queue.add(turnaround_info);
            }

            try {
                wait_writer.append(wait_time + "\n");
                response_writer.append(wait_time + turnaround_time + "\n");
                turnaround_writer.append(turnaround_time + "\n");
            } catch (IOException | NullPointerException e) {
                Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, e);
                System.exit(1);
            }

            if (jobQueue.size() <= 2) {
                double amt = Math.random() * 10;

                emptyJobPool = parser.addMoreJobs(parser.getQueue(type), (int) amt);

                for (int i = 0; i < amt; i++) {
                    wait_queue.add(0);
                    response_queue.add(0);
                    turnaround_queue.add(0);
                }

                System.out.println("Size: " + wait_queue.size());
            }
        }

        try {
            wait_writer.close();
            response_writer.close();
            turnaround_writer.close();
        } catch (IOException ex) {
            Logger.getLogger(CPUAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
        }

        parser.refreshJobPool();
        parser.printInformation(type);
    }

    private void startRR(JobQueue jobQueue) {
        final String type = "RR";
        boolean emptyJobPool = false;
        JobQueue wait_queue = new JobQueue(0);
        JobQueue turnaround_queue = new JobQueue(0);
        JobQueue response_queue = new JobQueue(0);
        File wait_time_file, response_time_file, turnaround_time_file;
        Writer wait_writer = null, turnaround_writer = null, response_writer = null;

        wait_time_file = new File(JobParser.dir + "/RR_wait_time");
        response_time_file = new File(JobParser.dir + "/RR_response_time");
        turnaround_time_file = new File(JobParser.dir + "/RR_turnaround_time");

        try {
            // Write length of wait time, turnaround time, response time
            // into files
            wait_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wait_time_file), "utf-8"));
            response_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(response_time_file), "utf-8"));
            turnaround_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(turnaround_time_file), "utf-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CPUAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        final int TIME_SLICE = 10;

        while (!emptyJobPool) {
            int quanta = quanta = jobQueue.peek();
            jobQueue.delete();

            if (quanta <= TIME_SLICE) {

                int wait_time = wait_queue.peek();
                wait_queue.delete();

                int turnaround_time = turnaround_queue.peek();
                turnaround_time += quanta; // The job will need to finish before it's considered complete
                turnaround_queue.delete();

                for (int i = 0; i < wait_queue.size(); i++) {
                    int wait_info = wait_queue.peek();
                    wait_queue.delete();
                    wait_info += quanta;
                    wait_queue.add(wait_info);

                    int info = turnaround_queue.peek();
                    turnaround_queue.delete();
                    info += quanta;
                    turnaround_queue.add(info);
                }

                try {
                    wait_writer.append(wait_time + "\n");
                    turnaround_writer.append(turnaround_time + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(CPUAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
                }

                int response_time = response_queue.peek();
                response_queue.delete();

                if (response_time != -1) {
                    try {
                        response_writer.append(response_time + "\n");
                    } catch (IOException ex) {
                        Logger.getLogger(CPUAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    for (int i = 0; i < response_queue.size(); i++) {
                        int temp = response_queue.peek();
                        response_queue.delete();

                        if (temp != -1) {
                            temp += quanta;
                        }

                        response_queue.add(temp);
                    }
                }
            } else {
                quanta -= TIME_SLICE;
                jobQueue.add(quanta);

                for (int i = 0; i < wait_queue.size(); i++) {
                    int info = wait_queue.peek();
                    wait_queue.delete();
                    info += quanta;
                    wait_queue.add(info);
                }

                for (int i = 0; i < turnaround_queue.size(); i++) {
                    int info = turnaround_queue.peek();
                    turnaround_queue.delete();
                    info += quanta;
                    turnaround_queue.add(info);
                }

                int response_time = response_queue.peek();
                response_queue.delete();

                if (response_time == -1) {
                    response_queue.add(-1);
                } else {
                    response_queue.add(-1);
                    try {
                        response_writer.append(response_time + "\n");
                    } catch (IOException ex) {
                        Logger.getLogger(CPUAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    for (int i = 0; i < response_queue.size(); i++) {
                        int temp = response_queue.peek();
                        response_queue.delete();

                        if (temp != -1) {
                            temp += quanta;
                        }

                        response_queue.add(temp);
                    }
                }
            }

            if (jobQueue.size() <= 2) {
                double amt = Math.random() * 10;

                emptyJobPool = parser.addMoreJobs(parser.getQueue(type), (int) amt);

                for (int i = 0; i < amt; i++) {
                    wait_queue.add(0);
                    response_queue.add(0);
                    turnaround_queue.add(0);
                }
            }
        }

        try {
            wait_writer.close();
            response_writer.close();
            turnaround_writer.close();
        } catch (IOException ex) {
            Logger.getLogger(CPUAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
        }

        parser.refreshJobPool();
        parser.printInformation(type);
    }

    private void startSJF(JobQueue jobQueue) {
        fcfs(jobQueue, true);
    }

    private void startFCFS(JobQueue jobQueue) {
        fcfs(jobQueue, false);
    }

}
