import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

public class SimpleHuffProcessor implements IHuffProcessor {
    
    private HuffViewer myViewer;
    HashMap<Integer, String> paths = new HashMap<Integer, String>();
    TreeNode root;
    
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        throw new IOException("compress is not implemented");
        //return 0;
    }

    public int preprocessCompress(InputStream in) throws IOException {
    	BitInputStream stream = new BitInputStream(in);
    	PriorityQueue<TreeNode> nodeForest = new PriorityQueue<TreeNode>();
    	int readBits = 0;
    	int[] weights = new int[IHuffConstants.ALPH_SIZE];
    	int current = stream.readBits(IHuffConstants.BITS_PER_WORD);
    	readBits += IHuffConstants.BITS_PER_WORD;
    	// As long as there are bits, read them
    	while(current != -1){
    		weights[current] += 1;
    		current = stream.readBits(8);
    		readBits += 8;
    	}
    	TreeNode curNode;
    	for(int a = 0; a < weights.length; a++){
    		int i = weights[a];
    		if(i != 0){
    			curNode = new TreeNode(a, i);
    			nodeForest.add(curNode);
    		}
    	}
    	System.out.println(nodeForest.peek().myWeight);
    	System.out.println(nodeForest.peek().myValue);
    	
    	// Create the tree by combining two items in the nodeForest until
    	// There is only one item remaining.
    	while(nodeForest.size() != 1){
    		TreeNode lBranch = nodeForest.poll();
    		TreeNode rBranch = nodeForest.poll();
    		int newVal = lBranch.myValue + rBranch.myValue;
    		TreeNode newNode = new TreeNode(newVal, lBranch, rBranch);
    		nodeForest.add(newNode);
    	}
    	/*
    	 * Initialize pointer to the root node. 
    	 * NOTE: This should remove the last item in the PriorityQueue
    	 */
    	root = nodeForest.poll();
    	
    	printWeights(weights);
    	return readBits;
    }
    
    public boolean buildTree(PriorityQueue nodeForest){
    	return true;
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
    
    public void printWeights(int[] array){
    	System.out.print("[ ");
    	for(Integer i : array){
    		if(i != 0)
    		System.out.print(i + ", ");
    	}
    	System.out.print("]\n");
    }

}
