package com.antivir;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;

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
    static final int MAX_FILE_SIZE = 5000000;

    //Using Swing UI designer
    private JPanel formMain;
    private JButton buttonPickDir;
    private JButton buttonStart;
    private JTextField textFieldDir;
    private JTextArea textAreaViruses;
    private JLabel labelFilesCheckedNum;
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
        } catch (Exception err) {
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
                String virus = (String) virusHashMap.get(String.valueOf(count));
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
        } else {
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
                chooser.setCurrentDirectory(new File("."));
                chooser.setDialogTitle("Select directory:");

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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
                                if (Files.size(file) < MAX_FILE_SIZE) {
                                    try {
                                        searchForVirus(file.toString());
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                    filesChecked++;
                                    labelFilesCheckedNum.setText("Files checked: " + filesChecked);
                                    labelFilesCheckedNum.setVerticalTextPosition(SwingConstants.CENTER);
                                    listedFiles.add(file.toString());
                                    return FileVisitResult.CONTINUE;
                                } else
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
                    } catch (Exception exc) {
                        System.out.println("Tree walking error: " + exc);
                    }
                    labelInfectedFilesNum.setText("Infected files found: " + infectedFiles.size());
                    buttonStart.setEnabled(true);


                } catch (Exception err) {
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        formMain = new JPanel();
        formMain.setLayout(new GridLayoutManager(8, 2, new Insets(10, 10, 10, 10), -1, -1));
        formMain.setPreferredSize(new Dimension(800, 500));
        labelInfectedFilesNum = new JLabel();
        Font labelInfectedFilesNumFont = this.$$$getFont$$$(null, Font.BOLD, 16, labelInfectedFilesNum.getFont());
        if (labelInfectedFilesNumFont != null) labelInfectedFilesNum.setFont(labelInfectedFilesNumFont);
        labelInfectedFilesNum.setForeground(new Color(-1834752));
        labelInfectedFilesNum.setInheritsPopupMenu(false);
        labelInfectedFilesNum.setText("Infected files found:!!!");
        labelInfectedFilesNum.setVerifyInputWhenFocusTarget(false);
        formMain.add(labelInfectedFilesNum, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(100, 70), null, 0, false));
        textAreaViruses = new JTextArea();
        textAreaViruses.setEditable(false);
        formMain.add(textAreaViruses, new GridConstraints(3, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        buttonPickDir = new JButton();
        buttonPickDir.setMargin(new Insets(0, 0, 0, 0));
        buttonPickDir.setText("Select directory");
        formMain.add(buttonPickDir, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelCurrentDir = new JLabel();
        Font labelCurrentDirFont = this.$$$getFont$$$(null, Font.BOLD, 16, labelCurrentDir.getFont());
        if (labelCurrentDirFont != null) labelCurrentDir.setFont(labelCurrentDirFont);
        labelCurrentDir.setText("Searching in:");
        formMain.add(labelCurrentDir, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelFilesCheckedNum = new JLabel();
        Font textFieldFilesCheckedNumFont = this.$$$getFont$$$(null, Font.BOLD, 16, labelFilesCheckedNum.getFont());
        if (textFieldFilesCheckedNumFont != null) labelFilesCheckedNum.setFont(textFieldFilesCheckedNumFont);
        labelFilesCheckedNum.setHorizontalAlignment(0);
        labelFilesCheckedNum.setHorizontalTextPosition(0);
        labelFilesCheckedNum.setText("Files checked:");
        formMain.add(labelFilesCheckedNum, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textFieldDir = new JTextField();
        textFieldDir.setEnabled(false);
        textFieldDir.setText("D:/TO_CHECK");
        formMain.add(textFieldDir, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonStart = new JButton();
        Font buttonStartFont = this.$$$getFont$$$(null, Font.BOLD, 14, buttonStart.getFont());
        if (buttonStartFont != null) buttonStart.setFont(buttonStartFont);
        buttonStart.setText("Start");
        formMain.add(buttonStart, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return formMain;
    }

}
