import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class BetterHuffProcessor implements IHuffProcessor {

    private HuffViewer myViewer;
	
	@Override
	public void setViewer(HuffViewer viewer) {
		// TODO Auto-generated method stub

	}

	@Override
	public int preprocessCompress(InputStream in) throws IOException {
		// TODO Auto-generated method stub
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
