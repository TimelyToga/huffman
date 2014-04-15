import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


public class BetterHuffProcessor implements IHuffProcessor {

    private HuffViewer myViewer;
	
	@Override
	public void setViewer(HuffViewer viewer) {
		myViewer = viewer;
	}

	@Override
	public int preprocessCompress(InputStream in) throws IOException {
		BitInputStream stream = new BitInputStream(in);
		int[] weights = new int[256];
		int current = stream.readBits(howManyBits);
		
		while(successfully read bits){
			// update weights for current
			// read enxt character
		}
		return 0;
	}
	
    private void showString(String s){
        myViewer.update(s);
    }

	@Override
	public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int uncompress(InputStream in, OutputStream out) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
