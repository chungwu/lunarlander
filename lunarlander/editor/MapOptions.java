/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lunarlander.editor;

import javax.swing.*;
import java.awt.event.*;


/**
 * @author Chung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MapOptions extends JPanel {
    public MapOptions(DraftMap dm) {
        this.map = dm;
        tfName = new JTextField(map.getName(), 15);
        tfWidth = new JTextField("" + map.getWorldWidth(), 5);        
        tfHeight = new JTextField("" + map.getWorldHeight(), 5);
        tfGravity = new JTextField("" + map.getGravity(), 5);
        cbSingle = new JCheckBox("Single");
        cbDuo = new JCheckBox("Duo");
        cbDeathmatch = new JCheckBox("Deathmatch");
        cbTeamDeathmatch = new JCheckBox("Team Deathmatch");
        cbCTF = new JCheckBox("CTF");
        
        tfWidth.setEnabled(false);
        
        btApply = new JButton("Apply");
        btApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                map.setWorldHeight(Double.parseDouble(tfHeight.getText()));
                map.setName(tfName.getText());
                map.setGravity(Double.parseDouble(tfGravity.getText()));
                map.setSingle(cbSingle.isSelected());
                map.setDuo(cbDuo.isSelected());
                map.setDeathmatch(cbDeathmatch.isSelected());
                map.setTeamDeathmatch(cbTeamDeathmatch.isSelected());
                map.setCTF(cbCTF.isSelected());
                LunarLanderMapEditor.setupFrame();
            }
        });
        
        btRestore = new JButton("Cancel");
        btRestore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillFields();
            }
        });
        
        this.add(new JLabel("Name:"));
        this.add(tfName);
        this.add(new JLabel("Width:"));
        this.add(tfWidth);
        this.add(new JLabel("Height:"));
        this.add(tfHeight);
        this.add(new JLabel("Gravity:"));
        this.add(tfGravity);
        this.add(cbSingle);
        this.add(cbDuo);
//        this.add(cbDeathmatch);
//        this.add(cbTeamDeathmatch);
//        this.add(cbCTF);
        this.add(btApply);
        this.add(btRestore);
        
        fillFields();
    }
    
    public void fillFields() {
        tfWidth.setText("" + map.getWorldWidth());
        tfHeight.setText("" + map.getWorldHeight());
        tfName.setText(map.getName());
        tfGravity.setText("" + map.getGravity());
        cbSingle.setSelected(map.isSingle());
        cbDuo.setSelected(map.isDuo());
        cbDeathmatch.setSelected(map.isDeathmatch());
        cbTeamDeathmatch.setSelected(map.isTeamDeathmatch());
        cbCTF.setSelected(map.isCTF());
    }
    
    private DraftMap map;
    private JTextField tfWidth;
    private JTextField tfHeight;
    private JTextField tfName;
    private JTextField tfGravity;
    private JButton btApply;
    private JButton btRestore;
    private JCheckBox cbSingle;
    private JCheckBox cbDuo;
    private JCheckBox cbDeathmatch;
    private JCheckBox cbTeamDeathmatch;
    private JCheckBox cbCTF;
    
    private static final long serialVersionUID = 1L;
}
