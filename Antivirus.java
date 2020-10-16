package com.antivir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

class Antivirus {
    int count = 0;
    int size = 0;
    int occur = 0;
    int filesChecked = 0;
    HashMap virusHashMap = new HashMap(); //HashMap for virus' keys
    String directory; //saved path
    List<String> listedFiles = new ArrayList<String>(); //List of files & directories at the path
    List<String> infectedFiles = new ArrayList<String>(); //List of all found infected files

    static final String SIGNATURES_FILE = "source files/signatures.txt";

    //Using Swing UI designer
    private JPanel formMain;
    private JButton buttonPickDir;
    private JButton buttonStart;
    private JTextField textFieldDir;
    private JTextArea textAreaViruses;
    private JLabel textFieldFilesCheckedNum;
    private JLabel labelCurrentDir;
    private JLabel labelInfectedFilesNum;

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
        occur = 0;
        count = 0;
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
        System.out.println("size" + size + " | occur " + occur);
        br.close();
        if (size == occur && size != 0) { //checking the file to contain a virus condition
            System.out.println("Virus Detected");
            infectedFiles.add(file);
            textAreaViruses.append(file + "\n"); //GUI logging element
        }
        else{
            System.out.println("No Virus Detected");
        }
    }

    public Antivirus() {
        buttonPickDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Select directory:");

                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    directory = chooser.getSelectedFile().toString();
                }
                labelCurrentDir.setText("Searching in: " + directory);
            }
        });

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buttonStart.setEnabled(false);
                    readSignatures(SIGNATURES_FILE);

                    try {
                        Files.walkFileTree(Paths.get(directory), new HashSet<>(), 10, new FileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                //System.out.println("Visited file: " + file);
                                if(Files.size(file) < 5000000) {
                                    try {
                                        searchForVirus(file.toString());
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                    filesChecked++;
                                    textFieldFilesCheckedNum.setText("Files checked: " + filesChecked);
                                    textFieldFilesCheckedNum.setVerticalTextPosition(SwingConstants.CENTER);
                                    listedFiles.add(file.toString());
                                    return FileVisitResult.CONTINUE;
                                }
                                else
                                    return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                                return FileVisitResult.SKIP_SUBTREE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                    catch (Exception exc) {
                        System.out.println("Tree walking error: " + exc);
                    }
                    labelInfectedFilesNum.setText("Infected files found: " + infectedFiles.size());
                    buttonStart.setEnabled(true);



                } catch(Exception err) {
                    System.out.println("Startup Error: " + err);
                }
            }
        });
    }


    public static void main(String[] args) {
        Antivirus av = new Antivirus();

        JFrame mainFrame = new JFrame("Simple Antivirus");
        mainFrame.setContentPane(av.formMain);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

    }
}
