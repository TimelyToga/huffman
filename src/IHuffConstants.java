
/**
 * Global constants used in Huff/Unhuff. Clients needing these
 * values should implement this interface or access the
 * values directly, e.g., as <code>IHuffConstants.BITS_PER_WORD</code>.
 * <P>
 * @author Owen Astrachan
 * @date November 2009
 */
public interface IHuffConstants {
    /**
     * The standard number of bits per chunk/word when huffing.
     */
    public static final int BITS_PER_WORD = 8;
    
    /**
     * The size of the alphabet given the number of bits per chunk, this
     * should be 2^BITS_PER_WORD.
     */
    public static final int ALPH_SIZE = (1 << BITS_PER_WORD);
    
    /**
     * The standard number of bits needed to represent/store
     * an int, this is 32 in Java and nearly all other languages.
     */
    public static final int BITS_PER_INT = 32;
    
    /**
     * The value of the PSEUDO_EOF character. This is one-more
     * than the maximum value of a legal BITS_PER_WORD-bit character.
     */
    
    public static final int PSEUDO_EOF = ALPH_SIZE;
    
    /**
     * Isolate the magic numbers in one place.
     */
    public static final int MAGIC_NUMBER = 0xface8200;
    public static final int STORE_COUNTS = MAGIC_NUMBER | 1;
    public static final int STORE_TREE = MAGIC_NUMBER | 2;
    public static final int STORE_CUSTOM = MAGIC_NUMBER | 4;
}
