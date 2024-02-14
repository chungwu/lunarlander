package lunarlander.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.Queue;

import lunarlander.Settings;
import lunarlander.game.LunarLanderGame;
import lunarlander.player.NetworkPlayer;

public class NetworkGameCanvas extends SingleGameCanvas {

    public NetworkGameCanvas(LunarLanderGame llg) {
        super(llg);
        // TODO Auto-generated constructor stub
        mode = PaintMode.LOADING;
        chatMessages = new LinkedList<ChatMessage>();
        loadingMessage = "";
    }
    
    /**
     * Paint the canvas, either the title screen or the game screen, depending on the "mode" flag
     * 
     * @param g is the graphics context
     */
    public void paintScreen(Graphics g) {
        if (Settings.getBoolean(Settings.ANTIALIAS)) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        setBackground(Color.black);
        
        if (mode == PaintMode.LOADING) {
            paintLoading(g);
        } else {
            paintInGame(g);
        }
        
        paintChatMessages(g);
    }
    
    public void setMode(PaintMode mode) {
        this.mode = mode;
        if (mode == PaintMode.PREGAME) {
            this.setDisplayMessage("waiting for game to start...");
        }
    }
    
    /**
     * Paint the title screen
     * 
     * @param g
     *            is the graphics context
     */
    public void paintLoading(Graphics g) {
        g.setColor(Color.white);
        Font f = new Font("Courier", Font.BOLD, Math.min(36, getHeight()));
        g.setFont(f);

        int strWidth = g.getFontMetrics().stringWidth("LOADING...");
        g.drawString("LOADING...", (getWidth() - strWidth) / 2,
                (getHeight() + f.getSize()) / 2);

        Font f2 = new Font("Courier", Font.BOLD, 20);
        g.setFont(f2);
        g.setColor(Color.yellow);
        int strWidth2 = g.getFontMetrics().stringWidth(loadingMessage);
        g.drawString(loadingMessage, (getWidth() - strWidth2) / 2,
                (getHeight() + f2.getSize()) / 2 + f.getSize());
    }
    
    public void updateViewingWindow() {
        if (mode == PaintMode.PREGAME) {
            urx += 5;
            llx += 5;
            
            if (urx > lunarLanderGame.moon.getWorldWidth()) {
                urx -= lunarLanderGame.moon.getWorldWidth();
                llx -= lunarLanderGame.moon.getWorldWidth();
            }
        } else {
            super.updateViewingWindow();
        }
    }
    
    private void paintChatMessages(Graphics g) {
        int row = chatMessages.size() - 1;
        for (ChatMessage message : chatMessages) {
            paintChatMessage(g, row, message);
            row--;
        }
    }
    
    private void paintChatMessage(Graphics g, int row, ChatMessage message) {
        int height = ((int)(getHeight() * 0.75)) + (10 + row * ChatMessage.PLAIN.getSize());
        Color textColor = (row == 0) ? Color.WHITE : Color.LIGHT_GRAY;
        
        if (message.type == ChatMessage.Type.STATUS) {
            g.setFont(ChatMessage.EMPH);
            g.setColor(textColor);
            g.drawString("*** " + message.message + " ***", 10, height);
        } else {
            g.setFont(ChatMessage.BOLD);
            g.setColor(message.player.getColor());
            int nameWidth = g.getFontMetrics().stringWidth(message.player.getName());
            g.drawString(message.player.getName(), 10, height);
            g.setFont(ChatMessage.PLAIN);
            g.setColor(textColor);
            g.drawString(message.message, 10+nameWidth, height);
        }
    }

    public static enum PaintMode {
        LOADING, PREGAME, GAME
    }
    
    public void setLoadingMessage(String message) {
        loadingMessage = message;
    }
    
    public void addStatusMessage(String message) {
        chatMessages.add(new ChatMessage(message));
    }
    
    public void addChatMessage(NetworkPlayer player, String message) {
        chatMessages.add(new ChatMessage(player, message));
    }
    
    private String loadingMessage;
    private Queue<ChatMessage> chatMessages;
    
    private PaintMode mode;
    
    private static class ChatMessage {
        
        public ChatMessage(String statusMessage) {
            this.player = null;
            this.message = statusMessage;
            this.type = Type.STATUS;
        }
        
        public ChatMessage(NetworkPlayer player, String message) {
            this.player = player;
            this.message = message;
            this.type = Type.CHAT;
        }
        
        public NetworkPlayer player;
        public String message;
        public Type type;
        
        public static Font PLAIN = new Font("Courier", Font.PLAIN, 14);
        public static Font BOLD = new Font("Courier", Font.BOLD, 14);
        public static Font EMPH = new Font("Courier", Font.ITALIC, 14);
        
        public static enum Type {
            STATUS, CHAT
        }
    }
}
