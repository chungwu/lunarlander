package lunarlander.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lunarlander.network.packet.Packet;

public class ChannelReader {
    private SocketChannel channel;
    private ByteArrayOutputStream leftOverBytes;
    private int bufferBytesLeft;
    private int bufferOffset;
    private int objectBytesLeft;
    private ReadState state;
    private List<Packet> leftOverPackets;
    private byte[] buffer;
    private ByteBuffer byteBuffer;
    private static final boolean DEBUG = false;
    public ChannelReader(SocketChannel ch) {
        channel = ch;
        leftOverBytes = new ByteArrayOutputStream();
        bufferBytesLeft = 0;
        bufferOffset = 0;
        objectBytesLeft = 0;
        state = ReadState.NONE_READ;
        leftOverPackets = new ArrayList<Packet>();
        buffer = new byte[PacketManager.BUFFER_SIZE];
        byteBuffer = ByteBuffer.allocateDirect(PacketManager.BUFFER_SIZE);
    }
    
    private void debugPrint(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }
    
    private int readObjectSize() {
        int objectSize = Packet.bytesToInt(buffer, bufferOffset);
        bufferBytesLeft -= 4;
        bufferOffset += 4;
        return objectSize;
    }
    
    public Packet readPacket() throws IOException {
        readPacketsFromChannel();
        return leftOverPackets.size() == 0 ? null : leftOverPackets.remove(0);
    }
    
    public List<Packet> readPackets() throws IOException {
        readPacketsFromChannel();
        
        if (leftOverPackets.size() == 0) {
            return Collections.<Packet>emptyList();
        }
        
        List<Packet> allPackets = new ArrayList<Packet>(leftOverPackets.size());
        for (Packet packet : leftOverPackets) {
            allPackets.add(packet);
        }
        
        leftOverPackets.clear();
        return allPackets;
    }
    
    private void readPacketsFromChannel() throws IOException {
        byteBuffer.clear();
        int numRead = channel.read(byteBuffer);
        
        if (numRead == 0) {
            return;
        }
        
        byteBuffer.flip();
        byteBuffer.get(buffer, 0, numRead);
        
        bufferOffset = 0;
        bufferBytesLeft = numRead;
        
        // first we finish what we have have started for this channel
        finishPreviousRead();
        
        // while there are still packets in the buffer, keep reading them
        while (bufferBytesLeft > 0) {
            readNewPacket();
        }
    }
    
    private void finishPreviousRead() throws IOException {
        // there wasn't any pending reads, so skip this part
        if (state == ReadState.NONE_READ) {
            return;
        }
        
        if (state == ReadState.READING_SIZE) {
            // if we were in the middle of reading the size...
            if (bufferBytesLeft - objectBytesLeft < 4) {
                // if we STILL don't have enough to read 4 bytes, then push 
                // all bytes in the buffer into leftOverBytes, update 
                // objectBytesLeft, and quit
                leftOverBytes.write(buffer, 0, bufferBytesLeft);
                objectBytesLeft -= bufferBytesLeft;
                debugPrint("Can't finish reading size!");
                return;
            } else {
                // else, we do have enough.  First, we increment the
                // buffer offset by how much we'll need to make up the 4
                // bytes, and we decrement the buffer bytes left
                
                bufferOffset = objectBytesLeft;                
                bufferBytesLeft -= objectBytesLeft;
                
                // we read those bytes into the leftOverBytes buffer, convert
                // it into an int, and store it as the new objectBytesLeft
                leftOverBytes.write(buffer, 0, objectBytesLeft);
                
                objectBytesLeft = Packet.bytesToInt(leftOverBytes.toByteArray(), 0);
                
                // leftOverBytes no longer has useful bytes, so reset it
                leftOverBytes.reset();
                
                // we're done reading size
                state = ReadState.READING_PAYLOAD;
                
                debugPrint("Finished reading size!  Read: " + objectBytesLeft);
            }
        }
                    
        // if we get to this point, then we already know the object size
        // of the object we're reading.
        
        if (objectBytesLeft > bufferBytesLeft) {
            // our read this time is still
            // not enough to fulfill our pending read.  Read everything into
            // the scratch stream, update the bytes left, and leave
            leftOverBytes.write(buffer, bufferOffset, bufferBytesLeft);
            objectBytesLeft -= bufferBytesLeft;
            bufferBytesLeft = 0;
            
            debugPrint("Couldn't finish reading object!  left: " + objectBytesLeft);
            return;
        }
        
        // if we get to this point, then we have enough to read off one 
        // packet, so let's read it!
        leftOverBytes.write(buffer, bufferOffset, objectBytesLeft);
        
        try {
            // convert what we read into a Packet
            ObjectInputStream ois = 
                new ObjectInputStream(
                        new ByteArrayInputStream(leftOverBytes.toByteArray()));
            Packet packet = (Packet) ois.readObject();
            debugPrint("READ: " + packet);
            leftOverPackets.add(packet);
            leftOverBytes.reset();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error parsing class: " + e);
        }
        
        // update buffer pointers accordingly
        bufferBytesLeft -= objectBytesLeft;
        bufferOffset += objectBytesLeft;
        objectBytesLeft = 0;
        state = ReadState.NONE_READ;
    }
    
    private void readNewPacket() throws IOException {
        
        debugPrint("Reading a new packet...  offset: " + bufferOffset + ", left: " + bufferBytesLeft);
        
        if (bufferBytesLeft < 4) {
            // we don't have enough bytes to even read the size!  Save it
            leftOverBytes.write(buffer, bufferOffset, bufferBytesLeft);
            objectBytesLeft = 4 - bufferBytesLeft;
            bufferBytesLeft = 0;
            state = ReadState.READING_SIZE;
            debugPrint("Couldn't finish reading size!  Left: " + objectBytesLeft);
            return;
        }
        
        // first read the object size
        objectBytesLeft = readObjectSize();
        
        debugPrint("OBJECT SIZE: " + objectBytesLeft);
        
        if (bufferBytesLeft >= objectBytesLeft) {
            // we have more bytes in our buffer than we need for this object!
            // read a single packet off the front
            try {
                ObjectInputStream ois =
                    new ObjectInputStream(
                            new ByteArrayInputStream(buffer, bufferOffset, objectBytesLeft));
                Packet packet = (Packet) ois.readObject();
                debugPrint("READ: " + packet);
                leftOverPackets.add(packet);
                bufferOffset += objectBytesLeft;
                bufferBytesLeft -= objectBytesLeft;
                leftOverBytes.reset();
                debugPrint("Still got left: " + bufferBytesLeft);
            } catch (ClassNotFoundException e) {
                throw new IOException("Error parsing class: " + e);
            }
        } else {
            // we don't have enough bytes in the buffer for this object,
            // so read all we can, and save it in the maps
            leftOverBytes.write(buffer, bufferOffset, bufferBytesLeft);
            objectBytesLeft -= bufferBytesLeft;
            bufferBytesLeft = 0;
            state = ReadState.READING_PAYLOAD;
            debugPrint("Couldn't finish reading object!  Left: " + objectBytesLeft);
        }
    }
    private static enum ReadState {
        NONE_READ, READING_SIZE, READING_PAYLOAD
    }
}

