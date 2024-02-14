package lunarlander.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

import lunarlander.LunarLanderLauncher;
import lunarlander.player.NetworkPlayer;
import lunarlander.thread.NetworkSimulationThread;
import lunarlander.canvas.NetworkGameCanvas;
import lunarlander.game.LunarLanderDeathmatch;
import lunarlander.gameobject.NetworkLunarLander;
import lunarlander.map.GameMap;
import lunarlander.network.packet.*;

/**
 * This class contains the run() behavior for a host player
 * 
 * @author mike
 */
public class Host extends NetworkManager {
    
    public static final int SERVER_PLAYER_ID = 1;
    
    public Host(NetworkPlayer thisPlayer) {
        super(thisPlayer, NetworkRole.SERVER);
        playerIdToPlayer = new HashMap<Integer, NetworkPlayer>();
        channelToPlayerId = new HashMap<SocketChannel, Integer>();       
    }
    
    public void startServer() throws IOException {
                
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket serverSocket = serverChannel.socket();
        serverSocket.bind(new InetSocketAddress(
                NetworkSimulationThread.TCP_PORT_NUMBER));
    
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        state = State.RENDEZVOUS;
    }

    public void processPackets() {
        
        if (state == State.NOTHING) {
            return;
        }
        
        int numKeys;
        
        try {
            numKeys = selector.selectNow();
        } catch (IOException e) {
            System.err.println("ERROR selecting key: " + e);
            return;
        }
        
        if (numKeys == 0) {
            return;
        }
        
        for(Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {        

            SelectionKey key = it.next();
            it.remove();
            
            debugPrint("Stepping Rendezvous with key " + key);
            
            if (key.isAcceptable()) {
                registerNewChannel(key);
            } else if (key.isReadable()) {
                SocketChannel channel = (SocketChannel) key.channel();
                List<Packet> packets = null;
                try {
                    packets = packetManager.readPackets(channel);
                } catch (IOException e) {
                    System.err.println("ERROR reading from channel: " + 
                            channel + ": "+ e);
                    continue;
                }
                
                for (Packet packet : packets) {
                    try {
                        debugPrint("SERVER: read packet " + packet);
                        handlePacket(channel, packet);
                    } catch (UnexpectedPacketException e) {
                        System.err.println(e);
                    }
                }
            }
        }
    }
    @Override
    public void sendChatMessage(int toPlayerId, String message) throws IOException {
        if (state == State.NOTHING) {
            return;
        }
        ChatMessagePacket packet = new ChatMessagePacket(thisPlayer.getId(), toPlayerId, message);
        gameManager.receiveChatMessage(thisPlayer.getId(), message);
        broadcastPacket(packet);
    }

    @Override
    public void sendUpdates() {
        if (state == State.NOTHING) {
            return;
        }
        if (framesSinceLastPlayerUpdate == FRAMES_TO_PLAYER_UPDATE) {
            PlayerPacket packet = gameManager.getSyncPlayerPacketToSend();
            if (packet != null) {
                broadcastPacket(packet);
            }
            
            framesSinceLastPlayerUpdate = 0;
        } else {
            framesSinceLastPlayerUpdate++;
        }
        
        if (framesSinceLastGameOptionsUpdate == FRAMES_TO_GAME_OPTIONS_UPDATE) {
            GameOptionsPacket packet = gameManager.getSyncGameOptionsPacketToSend();
            if (packet != null) {
                broadcastPacket(packet);
            }
            framesSinceLastGameOptionsUpdate = 0;
        } else {
            framesSinceLastGameOptionsUpdate++;
        }
        
        if (state == State.IN_GAME) {
            
            if (thisPlayer.getLander().isDead()) {
                ((LunarLanderDeathmatch)LunarLanderLauncher.game).createLanderForPlayer(thisPlayer);
            }
            
            for (NetworkPlayer player : playerIdToPlayer.values()) {
                
                if (player.getLander().isDead()) {
                    ((LunarLanderDeathmatch)LunarLanderLauncher.game).createLanderForPlayer(player);
                    PlayerGameStatePacket packet = new PlayerGameStatePacket(player);
                    broadcastPacket(packet);
                } else {                
                    PlayerGameStatePacket packet = new PlayerGameStatePacket(player);
                    broadcastPacket(packet, player.getId());
                }
            }
            
            broadcastPacket(new PlayerGameStatePacket(thisPlayer));
        }
    }
    
    public void startGame(GameMap map) throws IOException {
        
        broadcastPacket(new CommandPacket(CommandPacket.Command.START_GAME));
        
        LunarLanderLauncher.launchNetwork(this, gameManager.getPlayers());
        NetworkSimulationThread.getInstance(this).startGame();
        gameManager = (LunarLanderDeathmatch) LunarLanderLauncher.game;
        ((NetworkGameCanvas) LunarLanderLauncher.game.canvas).setMode(NetworkGameCanvas.PaintMode.LOADING);
        ((NetworkGameCanvas) LunarLanderLauncher.game.canvas).setLoadingMessage("Sending Map Data...");
        broadcastPacket(new MapPacket(map));
        ((NetworkGameCanvas) LunarLanderLauncher.game.canvas).setLoadingMessage("Sent Map Data!");

        LunarLanderLauncher.game.map = map;
        LunarLanderLauncher.game.moon = map.getMoon();
        LunarLanderLauncher.game.canvas.reset();
        
        ((NetworkGameCanvas) LunarLanderLauncher.game.canvas).setMode(NetworkGameCanvas.PaintMode.PREGAME);
        
        state = State.WAIT_FOR_GAME_START;
        
        gameManager.receiveStatusMessage(thisPlayer.getName() + " has joined the game");
    }
    
    public void startPlaying() {
        NetworkSimulationThread.getInstance(this).startPlaying();  
        LunarLanderLauncher.game.reset();
        ((NetworkGameCanvas)LunarLanderLauncher.canvas).setMode(NetworkGameCanvas.PaintMode.GAME);
        LunarLanderLauncher.game.canvas.setDisplayMessage("");
        state = State.IN_GAME;
        broadcastPacket(new CommandPacket(CommandPacket.Command.START_PLAYING));
        for (NetworkPlayer player : playerIdToPlayer.values()) {
            broadcastPacket(new PlayerGameStatePacket(player));
        }
    }
    
    public void disconnect() throws IOException {
        broadcastPacket(new GoodbyePacket(thisPlayer.getId()));
    }
    
    private void registerNewChannel(SelectionKey key) {
        try {
            debugPrint("Got connection request!  key: " + key);            
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverChannel.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            debugPrint("Channel registered: " + channel);
        }
        catch (IOException e) {
            // Do nothing (an error occured while establishing the connection with a remote client,
            // but only the client needs to know about this error
        }  
    }
    
    private void handlePacket(SocketChannel channel, Packet packet) 
            throws UnexpectedPacketException {
        if (packet instanceof ChatMessagePacket) {
            receiveAndBroadcastMessage((ChatMessagePacket) packet);
        } else if (packet instanceof JoinGamePacket) {
            addNewPlayer(channel, (JoinGamePacket) packet);
        } else if (packet instanceof PlayerPacket) {
            receivePlayerUpdate((PlayerPacket) packet);
        } else if (packet instanceof ReceivedMapPacket) {
            NetworkPlayer player = (NetworkPlayer) gameManager.getPlayer(((ReceivedMapPacket) packet).playerId);
            gameManager.receiveStatusMessage(player.getName() + " has joined the game");
            player.setState(NetworkPlayer.State.RECEIVED_MAP);
            broadcastPacket(packet, player.getId());
            
            for (NetworkPlayer p : playerIdToPlayer.values()) {
                if (p.getState() == NetworkPlayer.State.RECEIVED_MAP ||
                    p.getState() == NetworkPlayer.State.IN_GAME) {
                    try {
                        packetManager.sendPacket(new ReceivedMapPacket(p.getId()), channel);
                    } catch (IOException e) {
                        System.err.println("Error sending received map packet: " + e);
                        e.printStackTrace();
                    }
                }
            }
            
            try {
                packetManager.sendPacket(new ReceivedMapPacket(thisPlayer.getId()), channel);
            } catch (IOException e) {
                System.err.println("Error sending received map packet: " + e);
                e.printStackTrace();                
            }
        } else if (packet instanceof GoodbyePacket) {
            int playerId = ((GoodbyePacket) packet).playerId;
            NetworkPlayer player = playerIdToPlayer.get(playerId);
            gameManager.removePlayer(playerId);
            gameManager.receiveStatusMessage(player.getName() + " has left the game");
            playerIdToPlayer.remove(playerId);
            channelToPlayerId.remove(channel);
            broadcastPacket(new RemovePlayerPacket(playerId));
        } else if (packet instanceof PlayerGameStatePacket) {
            PlayerGameStatePacket gamePacket = (PlayerGameStatePacket) packet;
            NetworkPlayer player = playerIdToPlayer.get(gamePacket.playerId);
            ((NetworkLunarLander)player.getLander()).update(gamePacket.landerState);
        } else {
            throw new UnexpectedPacketException(packet, "SERVER: not expecting packet");
        }
    }
    
    private void receiveAndBroadcastMessage(ChatMessagePacket packet) {
        if (packet.receiverId == ChatMessagePacket.EVERYONE) {
            broadcastPacket(packet, packet.speakerId);
            gameManager.receiveChatMessage(packet.speakerId, packet.message);
        } else if (packet.receiverId == thisPlayer.getId()) {
            gameManager.receiveChatMessage(packet.speakerId, packet.message);
        } else {
            debugPrint("UNSUPPORTED: Private message: " + packet);
        }
    }
    
    private void addNewPlayer(SocketChannel channel, JoinGamePacket packet) {
        // TODO: check for game version stored in packet
        NetworkPlayer newPlayer = new NetworkPlayer(nextPlayerId, channel, packet);
        nextPlayerId++;
        try {
            packetManager.sendPacket(new AcceptJoinGamePacket(newPlayer.getId()), channel);
            packetManager.sendPacket(gameManager.getPlayerPacketToSend(), channel);
            packetManager.sendPacket(gameManager.getGameOptionsPacketToSend(), channel);
        } catch (IOException e) {
            System.err.println("SERVER: can't reply with accept join for player " + packet);
        }
        
        for (NetworkPlayer player : playerIdToPlayer.values()) {
            try {
                packetManager.sendPacket(new PlayerPacket(player), newPlayer.getChannel());
                packetManager.sendPacket(new PlayerPacket(newPlayer), player.getChannel());
            } catch (IOException e) {
                System.err.println("Can't send new player to : " + player);
            }
        }
        
        channelToPlayerId.put(channel, newPlayer.getId());
        playerIdToPlayer.put(newPlayer.getId(), newPlayer);
        
        gameManager.addPlayer(newPlayer);
    }
    
    private void receivePlayerUpdate(PlayerPacket packet) {
        
        NetworkPlayer player = new NetworkPlayer(packet);
        broadcastPacket(packet, packet.id);
        gameManager.updatePlayer(player);
    }
    
    private void broadcastPacket(Packet packet) {
        broadcastPacket(packet, Integer.MAX_VALUE);
    }
    
    private void broadcastPacket(Packet packet, int idToSkip) {
        for (NetworkPlayer player : playerIdToPlayer.values()) {
            try {
                if (player.getId() != idToSkip) {
                    packetManager.sendPacket(packet, player.getChannel());
                }
            } catch (IOException e) {
                System.err.println("Can't send packet to : " + player);
                e.printStackTrace();
            }
        }
    }
    
    private void handleCommand(SocketChannel channel, CommandPacket.Command command, Object[] arguments) {
        Integer senderId = channelToPlayerId.get(channel);
        if (senderId == null) {
            System.err.println("CANNOT map channel to player id: " + channel);
            return;
        }
        
        NetworkPlayer sender = playerIdToPlayer.get(senderId);
        
        if (sender == null) {
            System.err.println("CANNOT map player id to player: " + senderId);
            return;
        }
    }
    
    private static enum State {
        NOTHING, RENDEZVOUS, WAIT_FOR_GAME_START, IN_GAME
    }
    
    private State state;
    private Selector selector;
    private int nextPlayerId = SERVER_PLAYER_ID + 1;
    private Map<Integer, NetworkPlayer> playerIdToPlayer;
    private Map<SocketChannel, Integer> channelToPlayerId;
}