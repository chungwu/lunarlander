package lunarlander.options;

import javax.swing.*;



import java.awt.event.*;
import java.awt.*;
import java.util.Vector;

import lunarlander.canvas.IconCanvas;
import lunarlander.gameobject.LunarLander;
import lunarlander.gameobject.PreviewLunarLander;
import lunarlander.network.NetworkManager;
import lunarlander.player.NetworkPlayer;
import lunarlander.util.Vect2D;


/**
 * @author Chung
 * Chat panel for the rendezvous page
 */
public class ChatPanel extends JPanel {

    /**
     * Constructor
     * @param players Vector of players
     * @param thisPlayer the local Player
     */
    public ChatPanel(Vector<NetworkPlayer> players, NetworkPlayer thisPlayer, NetworkManager networkManager) {
        this.players = players;
        this.thisPlayer = thisPlayer;
        this.networkManager = networkManager;

        createWidgets();

        this.setLayout(new BorderLayout());
        this.add(topPanel, "Center");
        this.add(bottomPanel, "South");
    }

    /**
     * also resizes chat panel size
     */
    public void setPreferredSize(Dimension size) {
        super.setPreferredSize(size);
        int topHeight = size.height - 40;
        int chatWidth = (int) (0.8 * size.width);
        spChat.setPreferredSize(new Dimension(chatWidth, topHeight));
        spPlayers.setPreferredSize(new Dimension(size.width - chatWidth - 20, topHeight));

        this.repaint();
    }

    /**
     * refreshes list data
     */
    public void updatePlayersList() {
        lsPlayers.setListData(players);
        lsPlayers.repaint();
    }

    /**
     * adds new message to chat
     * @param player player saying the message
     * @param message the saying
     */
    public void receiveMessage(NetworkPlayer player, String message) {
        chatMessages += "<br><strong>" + player.getName() + ":  </strong>" + message;
        tpChatMessages.setText(chatMessages);
        this.repaint();
    }
    
    public void receiveStatusMessage(String message) {
        chatMessages += "<br><em>*** " + message + " ***</em>";
        tpChatMessages.setText(networkManager.getClass().getSimpleName() + ": " + chatMessages);
        this.repaint();
    }
    
    public void receiveMessage(int playerId, String message) {
        for (NetworkPlayer player : players) {
            if (player.getId() == playerId) {
                receiveMessage(player, message);
                return;
            }
        }
    }

    /**
     * sends a message from thisPlayer
     * @param message message to send
     */
    public void sendMessage(String message) {
        try {
            networkManager.sendChatMessage(message);
        } catch (java.io.IOException e) {
            System.err.println("ERROR sending chat message: " + e);
        }
    }

    /**
     * creates all JWidgets
     */
    protected void createWidgets() {
        tpChatMessages = new JTextPane();
        tpChatMessages.setContentType("text/html");
        tpChatMessages.setEditable(false);
        tpChatMessages.setText(chatMessages);

        tfNewMessage = new JTextField(60);
        tfNewMessage.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyChar() == '\n') {
                    if (tfNewMessage.getText().length() > 0) {
                        sendMessage(tfNewMessage.getText());
                        tfNewMessage.setText("");
                    }
                }
            }
        });
        
        btSendMessage = new JButton("Send");
        btSendMessage.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                if (tfNewMessage.getText().length() > 0) {
                    sendMessage(tfNewMessage.getText());
                    tfNewMessage.setText("");
                }
            }
        });

        lsPlayers = new JList(players);
        lsPlayers.setBackground(Color.black);
        lsPlayers.setForeground(Color.white);
        lsPlayers.setFixedCellHeight(35);
        lsPlayers.setCellRenderer(new PlayerNameRenderer());

        spChat = new JScrollPane(tpChatMessages);
        spPlayers = new JScrollPane(lsPlayers);

        topPanel = new JPanel(new FlowLayout());
        topPanel.add(spChat);
        topPanel.add(spPlayers);

        bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(new JLabel("Say:  "));
        bottomPanel.add(tfNewMessage);
        bottomPanel.add(btSendMessage);
    }


    /**
     * @author Chung
     *
     * Renders the players list in a fancy way
     */
    class PlayerNameRenderer extends JLabel implements ListCellRenderer {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.white);
            g.drawString(player.getName(), 35, 25);

            PreviewLunarLander lander = new PreviewLunarLander(new Vect2D(LunarLander.LANDER_LENGTH / 2 + 3,
                    LunarLander.LANDER_LENGTH/ 2), new Vect2D(0, 0), 0.0, player);
            
            lander.setRole(player.getRole());
            lander.setDrawTeamColor(drawTeam);
            lander.setTeam(player.getTeam());
            
            IconCanvas canvas = new IconCanvas(lander);
            canvas.paintScreen(g);
        }

        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.

        public Component getListCellRendererComponent(JList list, Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // the list and the cell have the focus
        {
            player = (NetworkPlayer) value;
            if (isSelected) {
                setBackground(Color.darkGray);
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }


        private NetworkPlayer player;
        private static final long serialVersionUID = 1L;
    }
    
    protected boolean drawTeam;


    protected JTextPane tpChatMessages;
    protected JTextField tfNewMessage;
    protected JButton btSendMessage;
    protected JList lsPlayers;
    protected JScrollPane spChat;
    protected JScrollPane spPlayers;
    protected JPanel topPanel;
    protected JPanel bottomPanel;

    protected String chatMessages = "";

    protected Vector<NetworkPlayer> players;
    protected NetworkPlayer thisPlayer;
    private NetworkManager networkManager;
    
    private static final long serialVersionUID = 1L;
}