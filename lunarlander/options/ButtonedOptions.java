package lunarlander.options;


import java.awt.BorderLayout;
import javax.swing.JPanel;

import lunarlander.LunarLanderLauncher;

/**
 * @author Chung
 *
 * ButtonedOptions is an Options panel with a panel of buttons at the bottom.
 */
public abstract class ButtonedOptions extends Options {

    /**
     * sets up the JPanel; by default, creates a "mainPanel" in the "Center", and an
     * "buttonsPanel" in the "South"
     */
    protected void setupPanel() {
        this.setLayout(new BorderLayout());

        buttonsPanel = createEndButtonsPanel();
        mainPanel = createMainPanel();
        
        mainPanel.setPreferredSize(LunarLanderLauncher.preferredCanvasDimension());
        
        this.add(mainPanel, "Center");
        this.add(buttonsPanel, "South");
    }
    

    /**
     * creates and returns the "EndButtons" panel that contains buttons that might close the Options
     * panel. These include buttons like "OK", "Cancel", etc.
     * 
     * @return "EndButtons" JPanel
     */
    protected abstract JPanel createEndButtonsPanel();

    /**
     * prepares the panel for display
     */
    protected void prepareForDisplay() {
        super.prepareForDisplay();
        mainPanel.setPreferredSize(LunarLanderLauncher.preferredCanvasDimension());
        this.doLayout();
    }
    
    protected JPanel buttonsPanel;
}
