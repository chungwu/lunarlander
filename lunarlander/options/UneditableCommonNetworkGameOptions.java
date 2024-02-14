/*
 * Created on Jan 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.options;

import java.awt.FlowLayout;

import javax.swing.JPanel;


/**
 * @author chungwu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UneditableCommonNetworkGameOptions extends CommonNetworkGameOptions {
    
    public UneditableCommonNetworkGameOptions(RendezvousOptions gameOptions) {
        super(gameOptions);
    }

    protected void createWidgets() {
        super.createWidgets();
        
        this.tfRocketBudget.setEnabled(false);
        this.tfSafeAngle.setEnabled(false);
        this.tfSafeVelocityX.setEnabled(false);
        this.tfSafeVelocityY.setEnabled(false);
        this.tfTurbo.setEnabled(false);
        this.coGameTypeChooser.setEnabled(false);
        this.cbEnableBigRockets.setEnabled(false);
        this.cbEnableDrones.setEnabled(false);
        this.cbEnableSmallRockets.setEnabled(false);
        this.cbRocketsDamage.setEnabled(false);
    }
    
    protected void createSubPanels() {
        super.createSubPanels();
        mapAndMisc = new JPanel(new FlowLayout());
        mapAndMisc.add(specificOptions);
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        specificOptions.setUneditable(true);
    }
    
    private static final long serialVersionUID = 1L;
}
