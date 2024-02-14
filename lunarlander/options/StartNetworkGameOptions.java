package lunarlander.options;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lunarlander.Settings;
import lunarlander.network.Client;
import lunarlander.network.Host;
import lunarlander.player.*;
import lunarlander.thread.*;

/**
 * @author Chung
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class StartNetworkGameOptions extends MessagedOptions {
    
    protected String getInstructions() {
        return "Enter remote host's IP address if you want to join a game, or start your own network game.";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see Options#createWidgets()
     */
    protected void createWidgets() {
        super.createWidgets();        
        
        tfJoinHost = new JTextField(20);
        tfServerHost = new JTextField(20);
        
        btJoin = new JButton("Join Game");
        btJoin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    NetworkSimulationThread thread = NetworkSimulationThread.getInstance(joinOptions.getClient());
                    thread.setNetworkManager(joinOptions.getClient());
                    joinOptions.getClient().connectToHost(tfJoinHost.getText());
                    thread.startRendezvous();
                } catch (Exception e) {
                    System.err.println("Error connecting to " + tfJoinHost.getText() + ": " + e);
                    e.printStackTrace();
                }
                joinOptions.displayOptions();
            }
        });
        
        btStart = new JButton("Start Game");
        btStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                NetworkSimulationThread thread = NetworkSimulationThread.getInstance(serverOptions.getHost());
                thread.setNetworkManager(serverOptions.getHost());
                try {
                    serverOptions.getHost().startServer();
                } catch (Exception e) {
                    System.err.println("SERVER: error starting server: " + e);
                    e.printStackTrace();
                }
                thread.startRendezvous();
                serverOptions.displayOptions();
            }
        });
    }
    
    protected void createSubOptions() {
        super.createSubOptions();
        
        NetworkPlayer player = 
            new NetworkPlayer(0, null, 
                              Settings.getString(Settings.PLAYER_ONE+Settings.NAME), 
                              Settings.getColor(Settings.PLAYER_ONE+Settings.COLOR), 
                              null, 
                              Team.values()[Settings.getInt(Settings.PLAYER_ONE+Settings.TEAM)], 
                              PlayerRole.values()[Settings.getInt(Settings.PLAYER_ONE+Settings.ROLE)]);
        Client client = new Client(player);
        joinOptions = new JoinGameOptions(player, client);
        client.setGameManager(joinOptions);
        subOptions.add(joinOptions);

        Host host = new Host(player);
        serverOptions = new StartServerGameOptions(player, host);
        host.setGameManager(serverOptions);
        subOptions.add(serverOptions);
    }
    
    protected void fillDefaults() {
        super.fillDefaults();
        tfJoinHost.setText(Settings.getString(Settings.REMOTE_IP));
        try {
            tfServerHost.setText((InetAddress.getLocalHost()).getHostAddress());
        } catch (UnknownHostException e) {
            tfServerHost.setText("localhost");
        }
    }
    
    protected void fillSettings() {
        super.fillSettings();
        tfServerHost.setText(Settings.getString(Settings.HOST_IP));
        tfJoinHost.setText(Settings.getString(Settings.REMOTE_IP));
    }
    
    public void saveSettings() {
        super.saveSettings();
        Settings.setString(Settings.HOST_IP, tfServerHost.getText());
        Settings.setString(Settings.REMOTE_IP, tfJoinHost.getText());
    }

    /*
     * (non-Javadoc)
     * 
     * @see Options#createMainPanel()
     */
    protected JComponent createMainPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(createJoinPanel());
        panel.add(createServerPanel());
        return panel;
    }
    
    protected JPanel createJoinPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Join Existing Game"));
        
        JPanel settings = new JPanel();
        settings.add(new JLabel("Remote Host:  "));
        settings.add(tfJoinHost);
        
        panel.add(settings, "Center");
        panel.add(btJoin, "South");
        return panel;
    }
    
    protected JPanel createServerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Start New Network Game"));
        
        JPanel settings = new JPanel();
        settings.add(new JLabel("Server Host:  "));
        settings.add(tfServerHost);
        
        panel.add(settings, "Center");
        panel.add(btStart, "South");
        
        return panel;
    }

    /**
     * create a JPanel for the terminal buttons (OK, ResetToDefault, Cancel)
     * 
     * @return the JPanel
     */
    protected JPanel createEndButtonsPanel() {
        JPanel panel = new JPanel();
        btDefaults = new JButton("Reset Default Settings");
        btDefaults.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fillDefaults();
            }
        });
        btCancel = new JButton("Cancel");
        btCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                exitOptions();
            }
        });
        
        panel.add(btDefaults);
        panel.add(btCancel);
        return panel;
    }

    protected JButton btJoin;
    protected JButton btStart;
    protected JButton btDefaults;
    protected JButton btCancel;
    protected JTextField tfJoinHost;
    protected JTextField tfServerHost;
    
    protected StartServerGameOptions serverOptions;
    protected JoinGameOptions joinOptions;
    
    public static int CLIENT = 0;
    public static int SERVER = 1;
    
    private static final long serialVersionUID = 1L;
}