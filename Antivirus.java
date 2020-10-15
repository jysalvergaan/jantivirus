package com.antivir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Antivirus {
    int count = 0;
    int size = 0;
    int occur = 0;
    HashMap virusHashMap = new HashMap(); //HashMap for virus' keys
    String directory; //saved path
    List<String> listedFiles = new ArrayList<String>(); //List of files & directories at the path
    List<File> subDirectories; //TODO
    List<String> infectedFiles = new ArrayList<String>(); //List of all found infected files
    int[] depthCounter = {0, 0, 0};
    int depthLevel = 0;

    static final String SIGNATURES_FILE = "source files/signatures.txt";

    //Using Swing UI designer
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
            while ((line = br.readLine()) != null) { //filling in the HashMap
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
        if (size == occur && !(size == 0 && occur == 0)) { //checking the file to contain a virus condition
            JOptionPane.showMessageDialog(null, "Infected File", "Virus Detected ", JOptionPane.ERROR_MESSAGE);
            System.out.println("Virus Detected");
            infectedFiles.add(file);
            textAreaViruses.append(file); //GUI logging element
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
                directory = textFieldDir.getText();
                File f = new File(directory);
            }
        });

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    readSignatures(SIGNATURES_FILE);

                    try (Stream<Path> filesTree = Files.walk(Paths.get(directory), 10


                            )) {
                        listedFiles = filesTree.map(path -> Files.isRegularFile(path) ? path.toString() + '/' : path.toString()).collect(Collectors.toList());
                    }
                    catch (Exception exc) {
                        System.out.println("Tree walking error: " + exc);
                    }

                    for (String f: listedFiles) {
                        searchForVirus(f);
                    }

                } catch(Exception err) {
                    System.out.println("Startup Error: " + err);
                }
            }
        });
    }


    public static void main(String[] args) {

        Antivirus av = new Antivirus();

        JFrame mainFrame = new JFrame("Main");
        mainFrame.setContentPane(av.formMain);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }
}
