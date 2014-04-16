import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

public class SimpleHuffProcessor implements IHuffProcessor {
	
    private HuffViewer myViewer;
    HashMap<Integer, String> paths = new HashMap<Integer, String>();
    int[] weights;
    TreeNode root;
    
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
    	BitOutputStream outStream = new BitOutputStream(out);
    	System.out.println("Started compress...");
    	
    	int writeCount = 0;
    	
    	//Set up Magic Number
    	outStream.writeBits(BITS_PER_INT, MAGIC_NUMBER);
    	writeCount += BITS_PER_INT;
    	
    	// Write header
    	for(int a = 0; a < ALPH_SIZE; a++){
    		out.write(weights[a]);
    		writeCount += BITS_PER_INT;
    	}
    	
    	// Read current character
		int current = in.read();
		while(current != -1){
			// Get the tree path in String form
			String curPath = paths.get(current);
			for(int a = 1; a <= curPath.length(); a++){
				String curByte = curPath.substring(a-1, a);
				if(curByte.equals("0")){
					outStream.writeBits(1, 0);
				} else if(curByte.equals("1")){
					outStream.writeBits(1, 1);
				} else {
					System.out.println("You are fucked.");
				}
				writeCount += 1;
			}
			current = in.read();
		}
		
		out.write(PSEUDO_EOF);
		System.out.println("File wrote: " + writeCount);
		return writeCount;
    }

    public int preprocessCompress(InputStream in) throws IOException {
    	BitInputStream stream = new BitInputStream(in);
    	PriorityQueue<TreeNode> nodeForest = new PriorityQueue<TreeNode>();
    	
    	int readBits = 0;
    	weights = new int[IHuffConstants.ALPH_SIZE];
    	int current = stream.readBits(IHuffConstants.BITS_PER_WORD);
    	readBits += IHuffConstants.BITS_PER_WORD;
    	// As long as there are bits, read them
    	while(current != -1){
    		weights[current] += 1;
    		current = stream.readBits(IHuffConstants.BITS_PER_WORD);
    		readBits += 8;
    	}
    	
    	// Create the tree
    	nodeForest = createTree(nodeForest);
    	
//    	printWeights(weights);
    	
    	// Create path map
    	findAllPaths(root, "");
    	for(Integer i : paths.keySet()){
    		System.out.println(paths.get(i));
    	}
    	
    	// BAD IMPLEMENTATION Update count
		for(Integer i : paths.keySet()){
			String s = paths.get(i);
			readBits -= s.length();
		}
    	
    	return readBits;
    }
    
    public void findAllPaths(TreeNode curNode, String s){
    	// Base case
    	if(curNode.myLeft == null && curNode.myRight == null){
    		paths.put(curNode.myValue, s);
    	}
    	
    	// Recursive Step
    	if(curNode.myLeft != null){
        	findAllPaths(curNode.myLeft, s + "0");
    	}
    	if(curNode.myRight != null){
        	findAllPaths(curNode.myRight, s + "1");
    	}
    	
    	if(s.length() != 0){
        	s = s.substring(0, s.length()-1);
    	}
    	return;
    }
    
    public PriorityQueue<TreeNode> createTree(PriorityQueue<TreeNode> in){
    	TreeNode curNode;
    	for(int a = 0; a < weights.length; a++){
    		int i = weights[a];
    		if(i != 0){
    			curNode = new TreeNode(a, i);
    			in.add(curNode);
    		}
    	}
    	
    	// Create the tree by combining two items in the nodeForest until
    	// There is only one item remaining.
    	while(in.size() != 1){
    		TreeNode lBranch = in.poll();
    		TreeNode rBranch = in.poll();
    		int newWeight = lBranch.myWeight + rBranch.myWeight;
    		TreeNode newNode = new TreeNode(newWeight, lBranch, rBranch);
    		in.add(newNode);
    	}
    	
    	/*
    	 * Initialize pointer to the root node. 
    	 * NOTE: This should remove the last item in the PriorityQueue
    	 */
    	root = in.poll();
    	
    	return in;
    }

    public void setViewer(HuffViewer viewer) {
        myViewer = viewer;
    }

    public int uncompress(InputStream in, OutputStream out) throws IOException {
    	BitInputStream inB = new BitInputStream(in);
    	PriorityQueue<TreeNode> nodeForest = new PriorityQueue<TreeNode>();
    	TreeNode uncompressRoot;
    	int unCompressCount = 0;
    	
    	clearWeights();
    	
    	// Read header
    	for(int a = 0; a < ALPH_SIZE; a++){
    		int curHeaderWeight = inB.readBits(BITS_PER_WORD);
    		weights[curHeaderWeight] = curHeaderWeight;
    	}
    	
    	// Recreate the tree
    	nodeForest = createTree(nodeForest);
    	uncompressRoot = nodeForest.poll();
    	
    	findAllPaths(root, "");
    	
    	
    	//return 0;
    }
    
    /**
     * Reset the global weights parameter for passing between Huff and UnHuff
     */
    public void clearWeights(){
    	for(int a = 0; a < weights.length; a++){
    		weights[a] = 0;
    	}
    	paths.clear();
    }
    
    private void showString(String s){
        myViewer.update(s);
    }
    
    public void printWeights(int[] array){
    	System.out.print("[ ");
    	for(Integer i : array){
    		if(i != 0)
    		System.out.print(i + ", ");
    	}
    	System.out.print("]\n");
    }

}
