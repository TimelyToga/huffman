import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SimpleHuffProcessor implements IHuffProcessor {
    
    private HuffViewer myViewer;
    
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        throw new IOException("compress is not implemented");
        //return 0;
    }

    public int preprocessCompress(InputStream in) throws IOException {
        throw new IOException("preprocess not implemented");
        //return 0;
    }

    public void setViewer(HuffViewer viewer) {
        myViewer = viewer;
    }

    public int uncompress(InputStream in, OutputStream out) throws IOException {
        throw new IOException("uncompress not implemented");
        //return 0;
    }
    
    private void showString(String s){
        myViewer.update(s);
    }

}
