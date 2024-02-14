package lunarlander;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.*;

import lunarlander.canvas.TitleCanvas;
import lunarlander.game.LunarLanderDeathmatch;
import lunarlander.game.LunarLanderDuo;
import lunarlander.game.LunarLanderGame;
import lunarlander.game.LunarLanderSingle;
import lunarlander.network.NetworkManager;
import lunarlander.options.MainOptions;
import lunarlander.options.StartDuoGameOptions;
import lunarlander.options.StartNetworkGameOptions;
import lunarlander.options.StartSingleGameOptions;
import lunarlander.player.*;



/*
 * Created by Chung on Dec 14, 2004
 */

/**
 * LunarLanderLauncher: launches the LunarLanderGame in screen where player chooses what variant of
 * LL to play
 */
public class LunarLanderLauncher {

    /**
     * runs the LunarLanderLauncher
     * 
     * @param args ignored
     */
    public static void main(String[] args) {

        // load in settings
        Settings.load();
        
        // create the frame
        initializeFrame();
        
        // create title canvas
        titleCanvas = canvas = new TitleCanvas();
        
        // create all options

        mainOptions = new MainOptions();
        mainOptions.initialize();
        
        singleOptions = new StartSingleGameOptions();
        singleOptions.initialize();

        multiOptions = new StartDuoGameOptions();
        multiOptions.initialize();
        /*
        networkOptions = new StartNetworkGameOptions();
        networkOptions.initialize();        
        */
        
        // display the title screen
        displayTitleFrame();
        
        // center the frame in the middle of the screen
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getWidth()) / 2, (d.height - frame.getHeight()) / 2);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });
    }

    /**
     * displays only the TitleCanvas and the game choice buttons
     */
    public static void displayTitleFrame() {
        frame.getContentPane().removeAll();
        setupTitlePanels();
        setupFrame();
    }

    /**
     * creates the frame
     */
    private static void initializeFrame() {
        // Create frame
        frame = new JFrame();
        frame.setTitle("Lunar Lander");

        frame.setFocusable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }

    /**
     * creates a TitleCanvas and a JPanel for title buttons, and adds both to the frame
     */
    private static void setupTitlePanels() {
        frame.getContentPane().removeAll();
        
        // Create and add lander canvas
        canvas = titleCanvas;
        canvas.setPreferredSize(preferredCanvasDimension());
        frame.getContentPane().add(canvas, "Center");

        // Create and add btnStartOver button at the top
        createTitleButtonPanel();
        frame.getContentPane().add(buttons, "South");
    }

    /**
     * packs and refocuses the frame
     */
    public static void setupFrame() {
        frame.pack();
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    /**
     * create buttons for launching single-player, two-player and networking variants of the Lunar
     * Lander game. Also a button for quitting.
     */
    private static void createTitleButtonPanel() {
        buttons = new JPanel();

        btSingle = new JButton("Single Player Game");
        btSingle.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                singleOptions.displayOptions();
            }
        });
        buttons.add(btSingle);

        btMulti = new JButton("Two-Player Game");
        btMulti.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                multiOptions.displayOptions();
            }
        });
        buttons.add(btMulti);
        /*
        btNetwork = new JButton("Network Game");
        btNetwork.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                networkOptions.displayOptions();
            }
        });
        buttons.add(btNetwork);
        */
        btOptions = new JButton("Options");
        btOptions.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                mainOptions.displayOptions();
            }
        });
        buttons.add(btOptions);

        btExit = new JButton("Quit");
        btExit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
        buttons.add(btExit);
    }

    /**
     * launch the single-player game
     */
    public static void launchSingle() {
        frame.getContentPane().removeAll();
        game = new LunarLanderSingle();
        canvas = game.canvas;
        setupFrame();
        game.reset();
    }

    /**
     * launch the two-player game
     */
    public static void launchMulti() {
        frame.getContentPane().removeAll();
        game = new LunarLanderDuo();
        canvas = game.canvas;
        setupFrame();
        game.reset();
    }

    /**
     * launch the network game
     */
    public static void launchNetwork(NetworkManager manager, List<? extends Player> players) {
        frame.getContentPane().removeAll();
        game = new LunarLanderDeathmatch(manager, players);
        canvas = game.canvas;
        setupFrame();
    }

    /**
     * quit
     */
    private static void exitGame() {
    	/*
        mainOptions.saveSettings();
        singleOptions.saveSettings();
        multiOptions.saveSettings();
        */
        //networkOptions.saveSettings();
        Settings.save();
        System.exit(0);
    }

    /**
     * currently-preferred dimension; we find this out by looking at mainOptions
     * @return preferred Dimension
     */
    public static Dimension preferredCanvasDimension() {
        int[] res = CANVAS_RESOLUTIONS[Settings.getInt(Settings.RESOLUTION)];
        return new Dimension(res[0], res[1]);
        
    }
    
    /**
     * Prints out an error message and exits
     * 
     * @param method the name of the method where the fatal error occured
     * @param message a description of the problem
     */
    public static void handleFatalError(String method, String message) {
        System.err.println("Error in " + method + "(): " + message);
        System.exit(-1);
    }

    
    // COMPONENTS
    public static LunarLanderGame game; // LunarLanderGame currently running
    public static Thread simulation; // Thread that handles simulation
    public static JPanel canvas; // canvas currently used
    public static JPanel titleCanvas; // canvas for title
    public static JFrame frame; // Graphics window

    private static JButton btSingle; // launch single-player game
    private static JButton btMulti; // launch two-player game
    private static JButton btNetwork; // launch network game
    private static JButton btOptions; // launch options screen
    private static JButton btExit; // exit game
    public static JPanel buttons;

    public static MainOptions mainOptions;
    public static StartSingleGameOptions singleOptions;
    public static StartDuoGameOptions multiOptions;
    //public static StartNetworkGameOptions networkOptions;

    // CANVAS RESOLUTIONS
    public static final int[][] CANVAS_RESOLUTIONS = { { 800, 600 }, { 1024, 768 }, { 1280, 960 } };

}