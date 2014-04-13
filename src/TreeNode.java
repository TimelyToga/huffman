/**
 * Utility binary-tree (Huffman tree) node for Huffman coding.
 * This is a simple, standard binary-tree node implementing
 * the comparable interface based on weight.
 * 
 * @author Owen Astrachan
 * @version 1.0 July 2000
 * @version 2.0 Jan 2006
 */
public class TreeNode implements Comparable<TreeNode> {
    
    public int myValue;
    public int myWeight;
    public TreeNode myLeft;
    public TreeNode myRight;

    /**
     * construct leaf node (null children)
     * 
     * @param value
     *            is the value stored in the node (e.g., character)
     * @param weight
     *            is used for comparison (e.g., count of # occurrences)
     */

    public TreeNode(int value, int weight) {
        myValue = value;
        myWeight = weight;
    }

    /**
     * construct internal node (with children)
     * 
     * @param value
     *            is stored as value of node
     * @param weight
     *            is weight of node
     * @param ltree
     *            is left subtree
     * @param rtree
     *            is right subtree
     */

    public TreeNode(int value, int weight, TreeNode ltree, TreeNode rtree) {
        this(value, weight);
        myLeft = ltree;
        myRight = rtree;
    }

    /**
     * Return value  based on comparing this TreeNode to another.
     * @return -1 if this < o, +1 if this > o, and 0 if this == 0
     */

    public int compareTo(TreeNode rhs) {

        return myWeight - rhs.myWeight;
    }
}
