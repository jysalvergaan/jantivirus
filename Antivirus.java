package com.antivir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class Antivirus {
    int count = 0;
    int size = 0;
    int occur = 0;
    HashMap virusHashMap = new HashMap();
    String directory;
    File[] listedDirectory;
    List<String> infectedFiles = new ArrayList<String>();

    static final String SIGNATURES_FILE = "source files/signatures.txt";
    static final String FILE_TO_CHECK = "source files/virus.exe";
    private JPanel formMain;
    private JButton buttonPickDir;
    private JButton buttonStart;
    private JTextField textFieldDir;
    private JTextArea textAreaViruses;

    @SuppressWarnings("unchecked")
    void readSignatures(String filename) throws Exception {
        try {
            FileReader in = new FileReader(filename);
            BufferedReader br = new BufferedReader(in);
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                virusHashMap.put(line.substring(0, line.indexOf("/")), line.substring(line.indexOf("/") + 1, line.length()));
                ++i;
            }
            size = i;
            br.close();
        } catch(Exception err) {
            System.out.println("Error: " + err);
        }
    }

    @SuppressWarnings("unchecked")
    void searchForVirus(String file) throws Exception {
        FileReader in = new FileReader(file);
        BufferedReader br = new BufferedReader(in);
        String line;
        while ((line = br.readLine()) != null) {
            Set keys = virusHashMap.keySet();
            count++;
            boolean containsKey = keys.contains(String.valueOf(count));
            if (containsKey) {
                String virus = (String)virusHashMap.get(String.valueOf(count));
                if (line.indexOf(virus) > -1) {
                    occur++;
                }
            }
        }
        System.out.println("size" + String.valueOf(size) + " | occur " + String.valueOf(occur));
        br.close();
        if (size == occur && !(size == 0 && occur == 0)) {
            JOptionPane.showMessageDialog(null, "Infected File", "Virus Detected ", JOptionPane.ERROR_MESSAGE);
            System.out.println("Virus Detected");
            infectedFiles.add(file);
            textAreaViruses.append(file);
        }
        else{
            JOptionPane.showMessageDialog(null, "Clean File", "No Virus Found ", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("No Viruses Detected");
        }
    }

    public Antivirus() {
        buttonPickDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //directory = "source files/virus.exe";
                directory = textFieldDir.getText();
                File f = new File(directory);
                listedDirectory = f.listFiles();
                System.out.println(Arrays.toString(listedDirectory));
            }
        });
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (File f : listedDirectory) {
                        String filename = f.toString();
                        int isSysFile = filename.substring(f.toString().lastIndexOf(".") + 1).compareTo("sys");
                        boolean isSysFileBool = false;

                        if(isSysFile == 0)
                            isSysFileBool = true;

                        if ( f.isFile() && !isSysFileBool) {
                            System.out.println(f.toString());
                            searchForVirus(f.toString());
                        }
                    }

                } catch(Exception err) {
                    System.out.println("Startup Error: " + err);
                }
            }
        });
    }


    public static void main(String[] args) {
        /*try {
            Antivirus fr = new Antivirus();
            fr.showDialog(fr);
        } catch (Exception err) {
            System.out.println("Error: " + err);
        }*/

        Antivirus av = new Antivirus();

        JFrame mainFrame = new JFrame("Main");
        mainFrame.setContentPane(av.formMain);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }
}
