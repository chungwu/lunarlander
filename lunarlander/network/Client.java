package lunarlander.network;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.net.*;
import java.util.List;

import javax.swing.JOptionPane;

import lunarlander.LunarLanderLauncher;
import lunarlander.Settings;
import lunarlander.player.NetworkPlayer;
import lunarlander.thread.NetworkSimulationThread;
import lunarlander.canvas.NetworkGameCanvas;
import lunarlander.game.LunarLanderDeathmatch;
import lunarlander.gameobject.NetworkLunarLander;
import lunarlander.map.GameMap;
import lunarlander.map.NetworkMap;
import lunarlander.network.packet.*;

/**
 * This class contains the run() behavior for a client player
 * 
 * @author mike
 */
public class Client extends NetworkManager {
    public Client(NetworkPlayer thisPlayer) {
        super(thisPlayer, NetworkRole.CLIENT);
        channel = null;
        state = State.NOTHING;
    }     
    
    // Interface to NetworkSimulationThread
    public void processPackets() {
        
        if (state == State.NOTHING) {
            return;
        }
        
        List<Packet> packets = null;
        
        try {
            packets = packetManager.readPackets(channel);
        } catch (IOException e) {
            System.err.println("ERROR reading from channel: " + e);
            return;
        }
                
        for (Packet packet : packets) {
            debugPrint("CLIENT: has packet " + packet);
            try {
                handlePacket(packet);
            } catch (UnexpectedPacketException e) {
                System.err.println(e);
            }
        }        
    }
    
    public void sendUpdates() throws IOException {
        if (state == State.NOTHING) {
            return;
        }
        
        if (framesSinceLastPlayerUpdate == FRAMES_TO_PLAYER_UPDATE) {
            PlayerPacket packet = gameManager.getSyncPlayerPacketToSend();
            if (packet != null) {
                packetManager.sendPacket(packet, channel);
            }
            
            framesSinceLastPlayerUpdate = 0;
        } else {
            framesSinceLastPlayerUpdate++;
        }
        
        if (state == State.IN_GAME) {
            if (!thisPlayer.getLander().isDead()) {
                packetManager.sendPacket(new PlayerGameStatePacket(thisPlayer), channel);
            }
        }
    }
    
    public void sendChatMessage(int toPlayerId, String message) throws IOException {
        if (state == State.NOTHING) {
            return;
        }
        packetManager.sendPacket(
                new ChatMessagePacket(thisPlayer.getId(), toPlayerId, message), channel);
        gameManager.receiveChatMessage(thisPlayer.getId(), message);
    }
    
    public void connectToHost(String host) 
        throws UnknownHostException, IOException, CannotJoinGameException {
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        
        InetSocketAddress hostAddress = 
            new InetSocketAddress(host, NetworkSimulationThread.TCP_PORT_NUMBER);
        
        channel.connect(hostAddress);
        
        while (!channel.finishConnect()) {
            blockFor(100);
        }
        
        debugPrint("CLIENT: created channel " + channel);
        
        packetManager.sendPacket(new JoinGamePacket(thisPlayer), channel);
        
        Packet reply = null;
        while (reply == null) {
            reply = packetManager.readOnePacket(channel);
            
            if (reply instanceof AcceptJoinGamePacket) {
                break;
            } else if (reply instanceof DenyJoinGamePacket) {
                debugPrint("CLIENT: denied to join game with reason " + reply);
                throw new CannotJoinGameException(((DenyJoinGamePacket) reply).reason);
            } else if (reply != null) {
                System.err.println("CLIENT: unexpected packet while joining game: " + reply);
            }
            
            reply = null;
            blockFor(100);
        }
        thisPlayer.setId(((AcceptJoinGamePacket) reply).id);                
        debugPrint("CLIENT: accepted into game with id " + thisPlayer.getId());
        
        state = State.RENDEZVOUS;
    }
    
    public void startGame(GameMap map) {
        ((NetworkGameCanvas) LunarLanderLauncher.game.canvas).setLoadingMessage("GOT MAP!!");
        LunarLanderLauncher.game.map = map;
        LunarLanderLauncher.game.moon = map.getMoon();
        LunarLanderLauncher.game.canvas.reset();
        ((NetworkGameCanvas) LunarLanderLauncher.game.canvas).setMode(NetworkGameCanvas.PaintMode.PREGAME);
        
        try {
            packetManager.sendPacket(new ReceivedMapPacket(thisPlayer.getId()), channel);
        } catch (IOException e) {
            System.err.println("ERROR sending received_map notice: " + e);
            e.printStackTrace();
        }
        state = State.WAIT_FOR_GAME_START;
    }
    
    public void disconnect() throws IOException {
        packetManager.sendPacket(new GoodbyePacket(thisPlayer.getId()), channel);
    }
    
    private void handlePacket(Packet packet) throws UnexpectedPacketException {
        if (packet instanceof ChatMessagePacket) {
            ChatMessagePacket chatPacket = (ChatMessagePacket) packet;
            gameManager.receiveChatMessage(chatPacket.speakerId, chatPacket.message);
        } else if (packet instanceof PlayerPacket) {
            PlayerPacket playerPacket = (PlayerPacket) packet;
            gameManager.updatePlayer(new NetworkPlayer(playerPacket));
        } else if (packet instanceof GameOptionsPacket) {
            gameManager.updateGameOptions((GameOptionsPacket) packet);
        } else if (packet instanceof CommandPacket) {
            handleCommand(((CommandPacket)packet).command, ((CommandPacket) packet).arguments);
        } else if (packet instanceof MapPacket) {
            GameMap map = new NetworkMap((MapPacket)packet);
            debugPrint("GOT MAP: " + map);
            startGame(map);
        } else if (packet instanceof ReceivedMapPacket) {
            NetworkPlayer player = (NetworkPlayer) gameManager.getPlayer(((ReceivedMapPacket) packet).playerId);
            gameManager.receiveStatusMessage(player.getName() + " has joined the game");
            player.setState(NetworkPlayer.State.RECEIVED_MAP);
        } else if (packet instanceof GoodbyePacket) {
            JOptionPane.showMessageDialog(LunarLanderLauncher.frame, "Server has disconnected");
            LunarLanderLauncher.displayTitleFrame();
        } else if (packet instanceof RemovePlayerPacket) {
            int playerId = ((GoodbyePacket) packet).playerId;
            NetworkPlayer player = (NetworkPlayer) gameManager.getPlayer(playerId);
            gameManager.removePlayer(playerId);
            gameManager.receiveStatusMessage(player.getName() + " has left the game");
        } else if (packet instanceof PlayerGameStatePacket) {
            PlayerGameStatePacket gamePacket = (PlayerGameStatePacket) packet;
            NetworkPlayer player = (NetworkPlayer) gameManager.getPlayer(gamePacket.playerId);
            ((NetworkLunarLander)player.getLander()).update(gamePacket.landerState);
        } else {
            throw new UnexpectedPacketException(packet, "Already joined game!");
        }
    }
    
    private void handleCommand(CommandPacket.Command command, Object[] arguments) {
        if (command == CommandPacket.Command.START_GAME) {
            LunarLanderLauncher.launchNetwork(this, gameManager.getPlayers());
            NetworkSimulationThread.getInstance(this).startGame();
            gameManager = (LunarLanderDeathmatch) LunarLanderLauncher.game;
            ((NetworkGameCanvas) LunarLanderLauncher.game.canvas).setMode(NetworkGameCanvas.PaintMode.LOADING);
            ((NetworkGameCanvas) LunarLanderLauncher.game.canvas).setLoadingMessage("Receiving Map Data...");
            state = State.WAIT_FOR_MAP;
        } else if (command == CommandPacket.Command.START_PLAYING) {
            NetworkSimulationThread.getInstance(this).startPlaying();  
            LunarLanderLauncher.game.reset();
            ((NetworkGameCanvas)LunarLanderLauncher.canvas).setMode(NetworkGameCanvas.PaintMode.GAME);
            LunarLanderLauncher.game.canvas.setDisplayMessage("");
            state = State.IN_GAME;
        }
    }
    
    private void blockFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            
        }
    }
    
    private static enum State {
        NOTHING, RENDEZVOUS, WAIT_FOR_MAP, WAIT_FOR_GAME_START, IN_GAME
    }
    private State state;
    private SocketChannel channel;
}