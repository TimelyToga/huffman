import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.PriorityQueue;
/**
 * SimpleHuffProcessor.java
 * Completed April 17, 2014
 * @author Emre Sonmez (ebs32)
 * @author Tim Blumberg (NETID)
 */

public class SimpleHuffProcessor implements IHuffProcessor {

	private HuffViewer myViewer;
	HashMap<Integer, String> paths = new HashMap<Integer, String>(); // stores String representation of path for an integer
	HashMap<Integer, Integer> freqMap; // stores frequencies of character
	int compressedSize; // size of compressed file
	int originalSize; // size of original file
	int difference; // actual file size - compressed file size 
	
	/**
	 * method: compress
	 * use: compresses file by Huffman encoding
	 * uses: preprocessCompress
	 * returns: compressedSize (integer)
	 */
	public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
		BitOutputStream outStream = new BitOutputStream(out);
		BitInputStream inStream = new BitInputStream(in);
		
		 // ensure compressed file size is smaller than original file size
		if(!force && difference < 0){
			outStream.close();
			inStream.close();
			throw new IOException("Oops. Compressed file size is greater than original file size.");
		}
		
		// write magic number 
		outStream.writeBits(BITS_PER_INT, MAGIC_NUMBER);

		// write header
		for(int a = 0; a < ALPH_SIZE; a++){
			if (freqMap.containsKey(a)){ // pull frequencies from HashMap & write to header
				outStream.writeBits(BITS_PER_INT,freqMap.get(a));
			}else{ // write 0 to header
				outStream.writeBits(BITS_PER_INT,0);
			}
		}
		
		// compress file
		int current; // initialize integer (will represent bits read from inStream)
		while((current = inStream.readBits(BITS_PER_WORD)) != -1){
			String code = paths.get(current); // get String representation from HashMap
			for(int i = 0; i < code.length(); i++){
				outStream.writeBits(1, Integer.parseInt(code.charAt(i)+"")); // write bits to file
			}
		}

		// write EOF to file (note, EOF added to freqMap in preprocessCompress)
		String myEOF = paths.get(ALPH_SIZE);
		for(int i = 0; i < myEOF.length(); i++){
			outStream.writeBits(1, Integer.parseInt(myEOF.charAt(i)+""));
		}

		// close streams & return compressedSize (global variable, see preprocessCompress)
		outStream.close();
		inStream.close();
		showString("Compressing completed.");
		return compressedSize;
	}
	
	/**
	 * method: preprocessCompress
	 * use: prepares file for processing
	 * fills paths, freqMap, nodeForest(priority queue)
	 * calculates file size, compressed file size, difference
	 * returns: difference (integer)
	 * used in: compress
	 */
	
	public int preprocessCompress(InputStream in) throws IOException { 
		showString("Started compress...");
		paths.clear(); // clear HashMap of paths
		freqMap = new HashMap<Integer,Integer>(); // initialize freqMap
		BitInputStream stream = new BitInputStream(in);
		
		int current; // represents bits read from stream
		while((current = stream.readBits(BITS_PER_WORD)) != -1){ // fill freqMap
			if (freqMap.containsKey(current)){
				freqMap.put(current, freqMap.get(current) + 1);
			}else{
				freqMap.put(current, 1);
			}
		}
		
		// add EOF to freqMap
		freqMap.put(PSEUDO_EOF, 1);

		// create priority queue
		PriorityQueue<TreeNode> nodeForest = new PriorityQueue<TreeNode>();
		for (int i = 0; i < ALPH_SIZE+1; i++){ // ALPH_SIZE+1 to account for EOF
			if(freqMap.containsKey(i)){
				nodeForest.add(new TreeNode(i,freqMap.get(i)));
			}
		}
		
		TreeNode root = createTree(nodeForest); // create tree root
		findAllPaths(root, ""); // fill HashMap paths
		
		// calculate size of original file
		originalSize = -8; //subtract 8 as freqMap accounts for EOF, original file does not include EOF
		for (int k:freqMap.keySet()){
			originalSize += BITS_PER_WORD*freqMap.get(k); // multiply number of bits per word (8) by frequency
		}
		
		// calculate compressed file size (global variable, returned in compress)
		compressedSize = 0;
		for(int k:paths.keySet()){
			int add = freqMap.get(k)*(paths.get(k).length()); //frequencies * the length of the path encoding
			compressedSize += add;
		}
		compressedSize += ALPH_SIZE*32; // account for header
		compressedSize += 32; // account for magic number

		stream.close(); //close stream
		
		difference = originalSize-compressedSize; // bits saved (if negative, use force compression)
		
		// print to view
		showString("Original file size: " + originalSize + " bytes");
		showString("Compressed file size: " + compressedSize + " bytes");
		showString("Difference in file size (original-compressed): " + difference + " bytes");
		
		return difference;
	}

	/**
	 * method: FindAllPaths
	 * use: fills HashMap paths by iterating over tree
	 */

	public void findAllPaths(TreeNode curNode, String s){ 
		if (curNode.myLeft == null && curNode.myRight==null){ // if leaf, add value of node to paths
			paths.put(curNode.myValue, s);
			return;
		}
		findAllPaths(curNode.myLeft, s + "0"); // recurse over left child (add 0 to string)
		findAllPaths(curNode.myRight, s + "1"); // recurse over right child (add 1 to string)
	}

	/**
	 * method: createTree
	 * use: recursively creates tree from priority queue
	 * returns: TreeNode
	 * used in: uncompress & preprocessCompress
	 */
	
	public TreeNode createTree(PriorityQueue<TreeNode> in){ 
		if (in.size() == 1){ // if size of priority queue is 1, return head of queue
			return in.poll();
		}
		// create left and right child nodes
		TreeNode lBranch = in.poll(); 
		TreeNode rBranch = in.poll(); 
		
		// create new value by combining weight of children & create new node (with combined weights)
		int newWeight = lBranch.myWeight + rBranch.myWeight; 
		TreeNode newNode = new TreeNode(newWeight, newWeight);
		newNode.myLeft = lBranch; // set left child of newNode
		newNode.myRight = rBranch; // set right child of newNode
		
		in.add(newNode); // add node to priority queue
		
		return createTree(in); // call recursively
	}
	
	/**
	 * method: setViewer
	 * use: sets viewer
	 */
	public void setViewer(HuffViewer viewer) {
		myViewer = viewer;
	}

	/**
	 * method: uncompress
	 * use: uncompresses file compressed by Huffman encoding
	 * checks: checks if magic number matches 
	 * creates: frequency map (freqMap), priority queue
	 * uses two TreeNodes (n (unmodified) & current (modified then reset))
	 * to write to stream
	 * returns: unCompressCount (bits "un-compressed")
	 * view will indicate if un-compressed file and original file are the same size
	 */
	public int uncompress(InputStream in, OutputStream out) throws IOException { // finished (ES)
		showString("");
		showString("Started uncompress...");
		BitInputStream inB = new BitInputStream(in);
		BitOutputStream outB = new BitOutputStream(out);
		
		// create two TreeNodes: one to store root, one to modify
		TreeNode n = null;
		TreeNode current = null;
		
		int unCompressCount = 0; // tracks uncompressed characters
		
		//check magic number
		if(inB.readBits(BITS_PER_INT)!= MAGIC_NUMBER){
			inB.close();
			outB.close();
			throw new IOException("Oops. Magic numbers are not equal.");
		}

		// create map of frequencies
		freqMap = new HashMap<Integer,Integer>();
		for (int i = 0; i < ALPH_SIZE; i++){
			freqMap.put(i,inB.readBits(BITS_PER_INT));
		}

		// add EOF to freqMap
		freqMap.put(PSEUDO_EOF,1);

		// create priority queue
		PriorityQueue<TreeNode> nodeForest = new PriorityQueue<TreeNode>();
		for(int i = 0; i < ALPH_SIZE+1; i++){ // account for EOF
			if(freqMap.get(i) != 0){
				nodeForest.add(new TreeNode(i,freqMap.get(i)));
			}
		}

		n = createTree(nodeForest);	// create new tree and set to n
		current = n; // set current to n

		// read file by iterating until leaf is found
		int myBit = 0; // current bit
		while(true){ 
			myBit = inB.readBits(1); // read bits from stream
			if (myBit == -1){ // error case: no PSEUDO_EOF
				inB.close();
				outB.close();
				throw new IOException("Oops. No PSEUDO_EOF found.");
			}
			else {
				if ((myBit & 1) == 0){ // if myBit is 0, set current to left child
					current = current.myLeft;
				}
				else{ // otherwise, set current to right child
					current = current.myRight;
				}
				if(current.myRight == null && current.myLeft == null){ // if both children null, leaf found
					if(current.myValue == PSEUDO_EOF) { // if leaf is PSEUDO_EOF, don't write to stream
						break;
					}
					else{ // write to stream (outB)
						outB.writeBits(BITS_PER_WORD,current.myValue);
						unCompressCount += BITS_PER_WORD; // increment bits uncompressed 
						current = n; // reset current to n
					}
				}
			}
		}
		outB.close(); // close bitstream
		inB.close(); // close stream
		showString("Uncompressed file size: " + unCompressCount + " bytes");

		// check if uncompressed file size and original file size the same (if not, throw error)
		if (unCompressCount == originalSize){
			showString("Uncompressed and original files are the same size.");
		}else{ // note: running uncompress on .hf file without compressing first will test error
			showString("Uncompressed and original files do not match.");
			throw new IOException("Oops. Uncompressed and original files are different sizes.");
		}
	
		showString("Uncompress completed.");
		
		return unCompressCount;
	}

	private void showString(String s){
		myViewer.update(s);
	}

}
