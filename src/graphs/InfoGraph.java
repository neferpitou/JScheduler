/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import jobs.JobParser;
import static jobs.JobParser.JOB_POOL_SIZE;
import static jobs.JobParser.dir;


/**
 *
 * @author marcos
 */ 


 
public class InfoGraph extends JPanel {
    
    final int PAD = JobParser.JOB_POOL_SIZE / 2;
    private int[] sjf;
    private int[] fcfs;
    private int[] rr;
        
    public InfoGraph (String type){
        fill(type);
        
        JFrame f = new JFrame();
        f.setTitle("Graph of " + type + " time");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(this);
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);      
    }
    
    private void fill(String type){
        String sCurrentLine;
        
        ArrayList<Integer> data_sjf = new ArrayList();
        ArrayList<Integer> data_fcfs = new ArrayList();
        ArrayList<Integer> data_rr = new ArrayList();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + "/" + "SJF_" + type + "_time"));
            BufferedReader br2 = new BufferedReader(new FileReader(dir + "/" + "FCFS_" + type + "_time"));
            BufferedReader br3 = new BufferedReader(new FileReader(dir + "/" + "RR_" + type + "_time"));

            while ((sCurrentLine = br.readLine()) != null) {
                data_sjf.add(Integer.parseInt(sCurrentLine));
            }

            while ((sCurrentLine = br2.readLine()) != null) {
                data_fcfs.add(Integer.parseInt(sCurrentLine));
            }

            while ((sCurrentLine = br3.readLine()) != null) {
                data_rr.add(Integer.parseInt(sCurrentLine));
            }
            
            sjf = new int[data_sjf.size()];
            fcfs = new int[data_fcfs.size()];
            rr = new int[data_rr.size()];
            
            for (int i = 0; i < data_sjf.size(); i++){
                sjf[i] = (int) data_sjf.get(i);
            }
            
            for (int i = 0; i < data_fcfs.size(); i++){
                fcfs[i] = (int) data_fcfs.get(i);
            }
            
            for (int i = 0; i < data_rr.size(); i++){
                rr[i] = (int) data_rr.get(i);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JobParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
        
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        
        // Ordinate label.
        String s = "Time elapsed (ms)";
        float sy = PAD + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
        for(int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float)font.getStringBounds(letter, frc).getWidth();
            float sx = (PAD - sw)/2;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
        
        // Abcissa label.
        s = "Number of jobs completed";
        sy = h - PAD + (PAD - sh)/2 + lm.getAscent();
        float sw = (float)font.getStringBounds(s, frc).getWidth();
        float sx = (w - sw)/2;
        g2.drawString(s, sx, sy);
        
        // Draw lines.
        double xInc = (double)(w - 2*PAD)/(sjf.length-1);
        double scale = (double)(h - 2*PAD)/getMax();
        g2.setPaint(Color.green.darker());
        for(int i = 0; i < sjf.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h - PAD - scale*sjf[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*sjf[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        
        for(int i = 0; i < fcfs.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h - PAD - scale*fcfs[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*fcfs[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        
        for(int i = 0; i < rr.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h - PAD - scale*rr[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*rr[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        
        // Mark data points for SJF
        g2.setPaint(Color.red);
        for(int i = 0; i < sjf.length; i++) {
            double x = PAD + i*xInc;
            double y = h - PAD - scale*sjf[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }
        
        // Mark data points for FCFS
        g2.setPaint(Color.yellow);
        for(int i = 0; i < fcfs.length; i++) {
            double x = PAD + i*xInc;
            double y = h - PAD - scale*fcfs[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }
        
        // Mark data points for RR
        g2.setPaint(Color.orange);
        for(int i = 0; i < rr.length; i++) {
            double x = PAD + i*xInc;
            double y = h - PAD - scale*rr[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }
    }
 
    private int getMax() {
        int max = -Integer.MAX_VALUE;
        for(int i = 0; i < sjf.length; i++) {
            if(sjf[i] > max)
                max = sjf[i];
        }
        
        for(int i = 0; i < fcfs.length; i++) {
            if(fcfs[i] > max)
                max = fcfs[i];
        }
        
        for(int i = 0; i < rr.length; i++) {
            if(rr[i] > max)
                max = rr[i];
        }
        
        return max;
    }
 
  
}
