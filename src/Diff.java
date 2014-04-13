
import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

public class Diff
{
    protected static JFileChooser ourChooser = new JFileChooser(".");
    
    public Diff(){
        
    }
    
    public static void showMessage(String message){
        JOptionPane.showMessageDialog(null, message,"Diff Output",
                JOptionPane.INFORMATION_MESSAGE);
    }
    public static boolean doDiffer(File[] files){
        try {
            ProgressMonitorInputStream stream1 = 
                new ProgressMonitorInputStream(
                        null,
                        "reading "+files[0].getName(),
                        new FileInputStream(files[0]));
            ProgressMonitorInputStream stream2 = 
                new ProgressMonitorInputStream(
                        null,
                        "reading "+files[1].getName(),
                        new FileInputStream(files[1]));
            BitInputStream b1 = new BitInputStream(stream1);
            BitInputStream b2 = new BitInputStream(stream2);
            while (true) {
                int x = b1.readBits(8);
                int y = b2.readBits(8);
                if (x == -1) return y == -1;
                if (y == -1) return false;
                if (x != y) return false;
            }
            // never reached
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"trouble reading","Diff Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
      
    }
    public static void main(String[] args){
        ourChooser.setMultiSelectionEnabled(true);
        ourChooser.setDialogTitle("Diff: choose two files");
        int retval = ourChooser.showOpenDialog(null);
        if (retval == JFileChooser.APPROVE_OPTION){
                File[] files = ourChooser.getSelectedFiles();
                if (files.length != 2){
                    JOptionPane.showMessageDialog(null,"Choose Two Files", 
                            "Diff Error",JOptionPane.ERROR_MESSAGE);
                }
                else {
                    if (doDiffer(files)){
                        showMessage("Files are the same");
                    }
                    else {
                        showMessage("Files DIFFER somewhere");
                    }
                }
        }           
        System.exit(0);
    }
}
