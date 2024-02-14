package lunarlander.network;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.nio.channels.*;
import java.nio.*;
import java.util.List;
import java.util.ArrayList;

import lunarlander.network.packet.Packet;


public class PacketManager {

    public static final int BUFFER_SIZE = 4096;
    
    public PacketManager() {
        channels = new HashMap<SocketChannel, ChannelReader>();
        outBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }
    
    public void sendPacket(Packet packet, SocketChannel channel) throws IOException {        
        byte[] packetBytes = packet.toByteArray();
        int bytesLeftToWrite = packetBytes.length;
        int bufferOffset = 0;
        while (bytesLeftToWrite > 0) {
            outBuffer.clear();
            
            int numBytesToWrite = Math.min(outBuffer.capacity(), bytesLeftToWrite);
            
            outBuffer.put(packetBytes, bufferOffset, numBytesToWrite);
            outBuffer.flip();
        
            while (outBuffer.hasRemaining()) {
                channel.write(outBuffer);
            }
            bufferOffset += numBytesToWrite;
            bytesLeftToWrite -= numBytesToWrite;
        }
        
        // System.out.println("SENT: " + packet);
    }
    
    public Packet readOnePacket(SocketChannel channel) throws IOException {
        ChannelReader reader = lookupChannelReader(channel);
        return reader.readPacket();
    }
    
    public List<Packet> readPackets(SocketChannel channel) throws IOException {
        ChannelReader reader = lookupChannelReader(channel);
        return reader.readPackets();
    }
    
    private ChannelReader lookupChannelReader(SocketChannel channel) {
        ChannelReader reader = channels.get(channel);
        
        if (reader == null) {
            reader = new ChannelReader(channel);
            channels.put(channel, reader);
        }
        
        return reader;
    }
    
    
    private ByteBuffer outBuffer;
    private Map<SocketChannel, ChannelReader> channels;        
}
