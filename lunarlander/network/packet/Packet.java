package lunarlander.network.packet;
import java.awt.Color;
import java.io.*;
import java.nio.charset.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * Base class for all network packet types (UDP and TCP).
 * 
 * @author mike
 */
public abstract class Packet {
    
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream objectByteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(objectByteStream);
        
        try {
            objectOutputStream.writeObject(this);
        } finally {
            objectOutputStream.close();
        }
        
        byte[] objectBytes = objectByteStream.toByteArray();
        int size = objectBytes.length;
        
        byte[] outputBytes = new byte[objectBytes.length + 4];
        intToBytes(outputBytes, 0, size);
        
        for(int i=0; i<objectBytes.length; i++) {
            outputBytes[i+4] = objectBytes[i];
        }
        
        // System.out.println("CREATED: packet of size " + size + ";(" + outputBytes[0] + "," + outputBytes[1] + "," + outputBytes[2] + "," + outputBytes[3] + ")");
        
        return outputBytes;
    }
    
    public static void intToBytes(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte) ((value >> 24) & 0xFF);
        buffer[offset+1] = (byte) ((value >> 16) & 0xFF);
        buffer[offset+2] = (byte) ((value >> 8) & 0xFF);
        buffer[offset+3] = (byte) (value & 0xFF);
    }
    
    public static int bytesToInt(byte[] buffer, int offset) {
        int answer = (((int) (buffer[offset])) & 0xFF) << 24;
        answer += (((int) (buffer[offset+1])) & 0xFF) << 16;
        answer += (((int) (buffer[offset+2])) & 0xFF) << 8;
        answer += (((int) (buffer[offset+3])) & 0xFF);
        
        return answer;
    }
    
    public static void main(String [] args) {       
        
        System.out.println("233: " + Integer.toBinaryString(233));
        byte[] buffer = new byte[4];
        intToBytes(buffer, 0, 233);
        
        for(byte b : buffer) {
            System.out.println(Integer.toBinaryString(b));
        }
        
        System.out.println(((int)(buffer[3])) & 0xFF);
        System.out.println("in int: " + bytesToInt(buffer, 0));
        
         // JoinGamePacket packet = new JoinGamePacket(new lunarlander.player.NetworkPlayer(1, null, "Hello", Color.BLUE, null, 1, 1));
        /*
        try {
            
            byte[] outputBytes = packet.toByteArray();
            PacketReader reader = new PacketReader();
            
            ByteArrayInputStream bais = new ByteArrayInputStream(outputBytes, 0, 5);
            System.out.println(reader.readPacket(1, bais));
            
            bais = new ByteArrayInputStream(outputBytes, 5, 5);
            System.out.println(reader.readPacket(1, bais));
            
            bais = new ByteArrayInputStream(outputBytes, 10, 5);
            System.out.println(reader.readPacket(1, bais));
            
            bais = new ByteArrayInputStream(outputBytes, 15, 5);
            System.out.println(reader.readPacket(1, bais));
            
            bais = new ByteArrayInputStream(outputBytes, 20, 2000);
            System.out.println(reader.readPacket(1, bais));
            
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
        */
        /*
        System.out.println("+========");
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeInt(30);
            objectOutputStream.writeObject(packet);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            System.out.println(ois.readInt());
            System.out.println(ois.readObject());
            
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
        
        System.out.println("+========");
        
        try {
            byte[] bytes = packet.toByteArray();
            
            for(byte b : bytes) {
                System.out.print(b);
            }           
            
            System.out.println();
            System.out.println("byte array length: " + bytes.length);
            
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            
            try {
            System.out.println("SIZE: " + inputStream.readInt());
            
            System.out.println("PACKET: " + inputStream.readObject());
            } finally {
                inputStream.close();
            }
        } catch (OptionalDataException e) {
            System.err.println(e);
            System.err.println("e.eof: " + e.eof);
            System.err.println("e.length: " + e.length);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
        */
        
    }
    
    public static Charset charset = Charset.forName("ISO-8859-1");
    public static CharsetDecoder decoder = charset.newDecoder();
}
