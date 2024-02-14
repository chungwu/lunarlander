package lunarlander.options;

import java.util.Vector;

import javax.swing.*;
import java.awt.*;

import lunarlander.LunarLanderLauncher;

/**
 * @author Chung
 * 
 * MessagedOptions is just an Options class with an extra "Instructions" panel on top and an extra
 * "Errors" panel on the bottom. The former displays a helpful message explaining the purpose of the
 * panel, and the latter displays error messages if any setting is invalid.
 */
public abstract class MessagedOptions extends ButtonedOptions {

    /**
     * implements Options.createWidgets(); creates labels for instructions and error
     */
    protected void createWidgets() {
        String instruction = getInstructions();
        if (instruction.length() > 0) {
            lbInstructions = new JLabel("<html><table align='center' width='"
                    + (this.getPreferredSize().width - 2 * MESSAGE_BORDER_WIDTH) + "'><tr><td>"
                    + instruction + "</td></tr></table></html>");
            lbInstructions.setBorder(BorderFactory.createLoweredBevelBorder());
        }
        lbError = new JLabel("");
    }

    /**
     * the implementing class needs to supply the instructions string
     * 
     * @return instructions String; must be already formatted to fit the panel
     */
    protected abstract String getInstructions();

    /**
     * overrides Options.setupPanel(); also creates panels for instructions and errors
     */
    protected void setupPanel() {
        this.setLayout(new BorderLayout());

        JPanel instPanel = new JPanel();

        if (lbInstructions != null) {
            instPanel.add(lbInstructions);
        }

        JPanel errorPanel = new JPanel();
        errorPanel.add(lbError);

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(LunarLanderLauncher.preferredCanvasDimension());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(instPanel);
        mainPanel.add(createMainPanel());
        mainPanel.add(errorPanel);

        buttonsPanel = createEndButtonsPanel();

        this.add(mainPanel, "Center");
        this.add(buttonsPanel, "South");
    }

    protected void prepareForDisplay() {
        super.prepareForDisplay();
        if (lbInstructions != null) {
            lbInstructions.setText("<html><table align='center' width='"
                    + (this.getPreferredSize().width - 2 * MESSAGE_BORDER_WIDTH) + "'><tr><td>"
                    + getInstructions() + "</td></tr></table></html>");
        }
        this.doLayout();
    }


    protected JLabel lbInstructions;
    protected JLabel lbError;
    protected Vector errorMessages;

    public static final int MESSAGE_BORDER_WIDTH = 20;
}