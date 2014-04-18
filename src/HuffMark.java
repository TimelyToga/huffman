import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HuffMark {
	protected static JFileChooser	ourOpenChooser	= new JFileChooser(System.getProperties().getProperty("user.dir"));
	static {
		ourOpenChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	private double[]				myTotalCompressTime;
	private long[]					myTotalUncompressedBytes;
	private long[]					myTotalCompressedBytes;

	private IHuffProcessor			myHuffer;
	private static String			SUFFIX			= ".hf";
	private static boolean			FAST_READER		= true;
	private String[]				suffixes;

	boolean							preHuffed;
	boolean							preHuffed2;

	int								numComp			= 0;

	public void compress(File f) throws IOException {
		int curCompMagnitude = 0;
		
		// Get curCompMagnitude
		for (int a = suffixes.length - 1; a >= 0; a--) {
			if (f.getName().endsWith(suffixes[a])) {
				curCompMagnitude = a + 1;
				break;
			}
		}
		
		if (f.getName().endsWith(SUFFIX)) return; // don't read .hf files!
		if (f.isDirectory()) return; // don't read directories

		double start = System.currentTimeMillis();
		myHuffer.preprocessCompress(getFastByteReader(f));
		File outFile = new File(getCompressedName(f));
		FileOutputStream out = new FileOutputStream(outFile);
		System.out.println("compressing to: " + outFile.getCanonicalPath());
		myHuffer.compress(getFastByteReader(f), out, true);
		double end = System.currentTimeMillis();
		double time = (end - start) / 1000.0;

		myTotalUncompressedBytes[curCompMagnitude] += f.length();
		myTotalCompressedBytes[curCompMagnitude] += outFile.length();
		myTotalCompressTime[curCompMagnitude] += time;

		System.out.printf("%s from\t %d to\t %d in\t %.3f\n", f.getName(), f.length(), outFile.length(), time);
		System.out.println(f.getName() + " compression percentage: "
				+ (100.0 * (1.0 - 1.0 * (double) outFile.length() / (double) f.length())));

	}

	public void doMark() throws IOException {
		if (myHuffer == null) {
			myHuffer = new SimpleHuffProcessor();
		}
		int action = ourOpenChooser.showOpenDialog(null);
		if (action == JFileChooser.APPROVE_OPTION) {
			File dir = ourOpenChooser.getSelectedFile();
			File[] list = dir.listFiles();
			for (File f : list) {
				compress(f);
			}
			for(int a = 0; a < numComp; a++){
				System.out.println("---Huff Count "  + a + "-----");
				System.out.printf("total bytes read: %d\n", myTotalUncompressedBytes[a]);
				System.out.printf("total compressed bytes %d\n", myTotalCompressedBytes[a]);
				System.out.printf("total percent compression %.3f\n", 100.0 * (1.0 - 1.0 * myTotalCompressedBytes[a]
						/ myTotalUncompressedBytes[a]));
				System.out.printf("compression time: %.3f\n", myTotalCompressTime[a]);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		HuffMark hf = new HuffMark();
		// Compute SUFFIX
		hf.suffixes = new String[hf.numComp];
		for (int a = 0; a < hf.numComp; a++) {
			hf.SUFFIX += ".hf";
			hf.suffixes[a] = hf.SUFFIX.toLowerCase();
		}
		
		// Initialize arrays
		hf.myTotalCompressTime = new double[hf.numComp];
		hf.myTotalUncompressedBytes = new long[hf.numComp];
		hf.myTotalCompressedBytes = new long[hf.numComp];
		
		hf.doMark();
	}

	private String getCompressedName(File f) {
		String name = f.getName();
		String path = null;
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			System.err.println("trouble with file canonicalizing " + f);
			return null;
		}
		int pos = path.lastIndexOf(name);
		String newName = path.substring(0, pos) + name + SUFFIX;
		return newName;
	}

	private InputStream getFastByteReader(File f) throws FileNotFoundException {

		if (!FAST_READER) {
			return new FileInputStream(f);
		}

		ByteBuffer buffer = null;
		try {
			FileChannel channel = new FileInputStream(f).getChannel();
			buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			byte[] barray = new byte[buffer.limit()];

			if (barray.length != channel.size()) {
				System.err.println(String.format("Reading %s error: lengths differ %d %ld\n", f.getName(),
						barray.length, channel.size()));
			}
			buffer.get(barray);
			return new ByteArrayInputStream(barray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
