package lunarlander.options;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import lunarlander.*;
import lunarlander.thread.*;

/**
 * @author Chung
 * 
 * Options class provides a base class for Options panels for the game. The hiearchy and methods are
 * a little convoluted, but work pretty well. Duplicate code is pushed up the hiearchy tree as far
 * as possible.
 */
public abstract class Options extends JPanel {

    /**
     * Immediately after a constructor call to create an Options, one should call the initialize()
     * method on it. The constructor does not call initialize() because an inheriting class might
     * need to do some things in the constructor before calling initialize().
     * 
     * initialize() first creates JWidgets, then it creates Options contained within this Options.
     * It then calls initialize() on all these subOptions. Then it fills in default values for
     * everything, create containing panels, and set up all the panels for display
     */
    public void initialize() {
        // create/initialize all JWidgets
        createWidgets();

        // create subOptions
        createSubOptions();

        // initialize subOptions
        initializeSubOptions();

        // assign default values into widgets
        fillDefaults();

        // create subpanels
        createSubPanels();

        // set up the actual JPanel by inserting the widgets into "this", etc.
        setupPanel();
    }

    /**
     * creates and initializes all widgets
     */
    protected abstract void createWidgets();

    /**
     * create and fill the subOptions ArrayList; note that you don't need to call initialize() on
     * the Options that you create here, since they'll be initialized by initializeSubOptions(), for
     * convenience
     */
    protected void createSubOptions() {
        subOptions = new ArrayList<Options>();
    }

    /**
     * create any contained subpanels
     */
    protected void createSubPanels() {

    }

    /**
     * call initialize on all subOptions
     */
    protected void initializeSubOptions() {
        for (int i = 0; i < subOptions.size(); i++) {
            ((Options) subOptions.get(i)).initialize();
        }
    }

    /**
     * fill widgets with default values; recursively calls the same for all subOptions
     */
    protected void fillDefaults() {
        for (int i = 0; i < subOptions.size(); i++) {
            ((Options) subOptions.get(i)).fillDefaults();
        }
        ;
    }

    /**
     * transfer values from Settings to widgets; recursively calls the same for all subOptions
     */
    protected void fillSettings() {
        for (int i = 0; i < subOptions.size(); i++) {
            ((Options) subOptions.get(i)).fillSettings();
        }
    }

    /**
     * transfer values from widgets to Settings; recursively calls the same for all subOptions
     */
    public void saveSettings() {
        for (int i = 0; i < subOptions.size(); i++) {
            ((Options) subOptions.get(i)).saveSettings();
        }
    }

    /**
     * sets up the JPanel; by default, creates a "Main" panel in the "Center"
     */
    protected void setupPanel() {
        this.setLayout(new BorderLayout());
        mainPanel = createMainPanel();

        this.add(mainPanel, "Center");
    }

    /**
     * creates and returns the "Main" panel containing all settable widgets
     * 
     * @return "Main" JComponent
     */
    protected abstract JComponent createMainPanel();

    /**
     * the method to call to display this Options panel in the game's frame. It pauses the
     * SimulationThread, removes the current canvas from the frame and adds this one in its place.
     */
    public void displayOptions() {
        LocalSimulationThread.getInstance().pauseSimulation();

        this.setPreferredSize(new Dimension(LunarLanderLauncher.canvas.getWidth(),
                LunarLanderLauncher.canvas.getHeight() + LunarLanderLauncher.buttons.getHeight()));
        
        prepareForDisplay();

        JFrame frame = LunarLanderLauncher.frame;

        oldContentPane = (Container) frame.getContentPane();
        frame.setContentPane(new JPanel(new BorderLayout()));
        frame.getContentPane().add(this, "Center");

        LunarLanderLauncher.setupFrame();
        frame.repaint();
    }

    /**
     * exit the options panel; restores the previous content and unpauses SimulationThread
     */
    protected void exitOptions() {
        LunarLanderLauncher.frame.setContentPane(oldContentPane);
        LunarLanderLauncher.setupFrame();
        LunarLanderLauncher.frame.repaint();
        prepareForExit();
        LocalSimulationThread.getInstance().unpauseSimulation();
    }

    /**
     * called when displayOptions() is called, or when a containing Options is told to prepareForDisplay().
     * 
     * prepares the panel for display; recursively calls the same for all subOptions
     */
    protected void prepareForDisplay() {
        for (int i = 0; i < subOptions.size(); i++) {
            subOptions.get(i).prepareForDisplay();
        }

        this.fillSettings();

        this.doLayout();
    }

    /**
     * called when exitOptions() is called, or when a containing Options is told to prepareForExit().
     * 
     * prepares for exit; recursively calls the same for all subOptions
     */
    protected void prepareForExit() {
        for (int i = 0; i < subOptions.size(); i++) {
            subOptions.get(i).prepareForExit();
        }
    }


    protected ArrayList<Options> subOptions;
    protected Container oldContentPane;
    protected JComponent mainPanel;
}