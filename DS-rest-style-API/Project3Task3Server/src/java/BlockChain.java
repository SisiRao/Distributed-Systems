import java.util.ArrayList;
import java.sql.Timestamp;

/**
 * This class represents a simple BlockChain.
 * This BlockChain has exactly two instance members - an ArrayList to hold Blocks
 * and a chain hash to hold a SHA256 hash of the most recently added Block.
 *
 * Created Date: 1 March 2018
 * @author sisi rao
 */
public class BlockChain {
    public static ArrayList<Block> blocks;
    public static String chainHash;
    
    /**
    * This method initiate the BlockChain by adding a Genesis block.
    */
    static {
        //initiate
        blocks = new ArrayList<>();
        chainHash = ""; //hold a SHA256 hash of the most recently added Block
        Block genesis = new Block(0,new Timestamp(System.currentTimeMillis()),"Genesis",2);
        addBlock(genesis);
    }
    
    public BlockChain() {
    }
  
    /**
    * This method add a new Block into BlockChain.
    * 
    * @param newBlock is added to the BlockChain as the most recent block.
    */
    public static void addBlock(Block newBlock) {
        newBlock.setPreviousHash(chainHash);
        newBlock.proofOfWork(newBlock.getDifficulty());
        blocks.add(newBlock);
        chainHash = newBlock.calculateHash();
        
    }
    
    public Block getLastestBlock() {
        return blocks.get(blocks.size()-1);
    }
    
    
    /**
    * This method checks if the BlockChain is valid. If the chain only contains
    * one block, the genesis block at position 0, this routine computes the hash
    * of the block and checks that the hash has the requisite number of leftmost
    * 0's (proof of work) as specified in the difficulty field. 
    * 
    * @return true if and only if the chain is valid
    */
    public boolean isChainVaild() {
        //only contains one block
        if(blocks.size()==1) {
            Block b = blocks.get(0);
            String hash = b.calculateHash();
            if (!hash.equals(this.chainHash))
            {
                System.out.printf("Improper hash on node 0 Does not begin with 00\n");
                return false;
            }
                
            int cnt_block = getNumofLeftmostZero(hash);
            if(cnt_block != b.getDifficulty()) {
                System.out.printf("Improper hash on node 0 Does not begin with 00\n");
                return false;
            }
            return true;
        }
        
        //multiple blocks
        int i = 0;
        while(i<blocks.size())
        {
            Block currBlock = blocks.get(i);
            String currHash = currBlock.calculateHash();
            if(getNumofLeftmostZero(currHash) != currBlock.getDifficulty())
            {
                System.out.println("Improper hash on node "+i+" Does not begin with "
                                   +String.format("%0" + currBlock.getDifficulty() + "d", 0));
                return false;
            }
               
            if(i+1<blocks.size())
            {
                if(!currHash.equals(blocks.get(i+1).getPreviousHash()))
                {
                    System.out.println("Improper hash on node "+(i+1)+" Does not begin with "
                                    +String.format("%0" + blocks.get(i+1).getDifficulty() + "d", 0));
                    return false;
                }
                    
            }
            i++;
        }
        
        return true;
    }

       
    private int getNumofLeftmostZero(String hash) {
        int count = 0;
        while(hash.charAt(count)=='0'&& count<hash.length()-1) {
            count++;
        }
        return count;
    }
        
    /**
    * This method repairs the chain. It checks the hashes of each block and 
    * ensures that any illegal hashes are recomputed
    */
    public void repairChain() {
        int i=0;
        while(i<blocks.size())
        {
            
            Block currBlock = blocks.get(i);
            String currHash = currBlock.calculateHash();
            if(getNumofLeftmostZero(currHash) != currBlock.getDifficulty()) 
            {
                String newHash = currBlock.proofOfWork(currBlock.getDifficulty());
                if(i+1<blocks.size())
                {
                    blocks.get(i+1).setPreviousHash(newHash);
                }
            }
            i++;
            
        }

    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"chain\":[");
        String prefix = "";
        for(Block b : blocks) {
            sb.append(prefix);
            prefix = ",";
            sb.append(b.toString());
        }
        sb.append("],\"chainHash\":\""+chainHash+"\"}");
       
        return sb.toString();
    }
}
