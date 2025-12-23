package eu.ajg.fairdistribution;

import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class OutputStreamRedirector extends OutputStream {

    private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private final StringBuilder textBuffer = new StringBuilder();
    
    protected OutputStreamHandler osh;

    @Override
    public void write(int b) throws IOException {
        byteBuffer.put((byte) b);

        byteBuffer.flip();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        decoder.decode(byteBuffer, charBuffer, false);
        byteBuffer.compact();

        charBuffer.flip();
        textBuffer.append(charBuffer);

        int newline;
        while ((newline = textBuffer.indexOf("\n")) >= 0) {
            String line = textBuffer.substring(0, newline);
            textBuffer.delete(0, newline + 1);
            if(osh != null) osh.handle(line);
        }
    }
    
    public void setOutputStreamHandler(OutputStreamHandler osh) {
    	this.osh = osh;
    }
    
    public interface OutputStreamHandler {
    	void handle(String s);
    }
    
}
