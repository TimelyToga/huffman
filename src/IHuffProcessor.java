/**
 * The interface for the model that can be attached
 * to a HuffViewer. Most of the work done in huffing
 * (and unhuffing) will be via a class that implements
 * this interface. The interface may need to be extended
 * depending on the design of the huffman program.
 * <P>
 * @author Owen Astrachan
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

public interface IHuffProcessor extends IHuffConstants {  
    
    /**
     * Make sure this model communicates with some view.
     * @param viewer is the view for communicating.
     */
    public void setViewer(HuffViewer viewer);
    
    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * @param in is the stream which could be subsequently compressed
     * @return number of bits saved by compression or some other measure
     */
 
    
    public int preprocessCompress(InputStream in) throws IOException;
    

    /**
      * Compresses input to output, where the same InputStream has
      * previously been pre-processed via <code>preprocessCompress</code>
      * storing state used by this call.
      * @param in is the stream being compressed (not a BitInputStream)
      * @param out is bound to a file/stream to which bits are written
      * for the compressed file (not a BitOutputStream)
      * @return the number of bits written
      */

    public int compress(InputStream in, OutputStream out, boolean force) throws IOException;
    

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream) 
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException;
    
}
