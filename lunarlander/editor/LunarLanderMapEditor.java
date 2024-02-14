/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileFilter;
import lunarlander.map.Moon;
import lunarlander.map.MoonFileException;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class LunarLanderMapEditor {

    public static void main(String[] args) {

        titleCanvas = new MapEditorTitleCanvas();
        titleCanvas.setPreferredSize(new Dimension(800, 600));
        canvas = titleCanvas;

        createButtonsPanel();

        frame = new JFrame();
        frame.setTitle("Lunar Lander Map Editor");

        frame.setFocusable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.getContentPane().add(titleCanvas, "Center");
        frame.getContentPane().add(buttons, "South");

        setupFrame();

        // center the frame in the middle of the screen
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getWidth()) / 2, (d.height - frame.getHeight()) / 2);

        fileChooser = new JFileChooser("./maps");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                return file.isDirectory() || (file.isFile() && file.getName().endsWith(".moon"));
            }

            public String getDescription() {
                return "Moon files (.moon)";
            }
        };
        fileChooser.addChoosableFileFilter(filter);
    }

    /**
     * packs and refocuses the frame
     */
    public static void setupFrame() {
        frame.pack();
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    private static void createButtonsPanel() {

        buttons = new JPanel();

        JButton btNew = new JButton("Create New Map");
        btNew.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewMap();
            }
        });

        JButton btLoad = new JButton("Load Map");
        btLoad.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                loadMap();
            }
        });

        JButton btSave = new JButton("Save Map");
        btSave.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveMap();
            }
        });
        
        JButton btQuit = new JButton("Quit");
        btQuit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });

        buttons.add(btNew);
        buttons.add(btLoad);
        buttons.add(btSave);
        buttons.add(btQuit);
    }

    private static void switchCanvas(JPanel newCanvas) {
        Dimension dim = canvas.getSize();
        newCanvas.setPreferredSize(dim);
        frame.getContentPane().remove(canvas);
        canvas = newCanvas;
        frame.getContentPane().add(canvas, "Center");
        setupFrame();
    }

    private static void createNewMap() {
        NewMapOptions mapOptions = obtainNewMapOptions();

        if (mapOptions == null) {
            return;
        }

        DraftMap map = new DraftMap(mapOptions);
        editorPane = new MapEditorPane(map);
        switchCanvas(editorPane);
    }

    private static NewMapOptions obtainNewMapOptions() {
        NewMapOptions newMapOptions = new NewMapOptions();

        JOptionPane option = new JOptionPane("What kind of map would you like to make?  " +
        		"(Single maps usually have width 1000, duo maps 600)",
                JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_OPTION, null, new String[] {
                        "Start", "Cancel" });
        option.add(newMapOptions, 1);
        JDialog dialog = option.createDialog(frame, "Creating New Map...");

        // dialog.getContentPane().add(newMapOptions);
        dialog.pack();
        dialog.setVisible(true);
        dialog.requestFocusInWindow();

        if (option.getValue().equals("Start")) {
            System.out.println("CREATING NEW MAP with width " + newMapOptions.getWorldWidth()
                    + " and height " + newMapOptions.getWorldHeight());
            return newMapOptions;
        }
        else {
            System.out.println("CANCELED");
            return null;
        }
    }

    private static void loadMap() {
        int status = fileChooser.showOpenDialog(frame);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Moon moon = new Moon(file);
                DraftMap map = new DraftMap(moon);
                editorPane = new MapEditorPane(map);
                switchCanvas(editorPane);
            }
            catch (MoonFileException e) {
                JOptionPane.showMessageDialog(LunarLanderMapEditor.frame,
                        "Error reading moon file " + file, "Moon File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void saveMap() {
        if (editorPane == null) {
            return;
        }
        
        int status = fileChooser.showSaveDialog(frame);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                if (!file.getName().endsWith(".moon")) {
                    file = new File(file.getAbsolutePath() + ".moon");
                }
                editorPane.getMap().save(file);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(LunarLanderMapEditor.frame,
                        "Error writing moon file " + file + "; " + e, "Moon File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void exit() {
        System.exit(0);
    }


    public static JFrame frame;
    public static JPanel canvas;
    private static JPanel miniOptions;
    private static JPanel buttons;
    private static JFileChooser fileChooser;
    private static MapEditorPane editorPane;
    private static MapEditorTitleCanvas titleCanvas;
}