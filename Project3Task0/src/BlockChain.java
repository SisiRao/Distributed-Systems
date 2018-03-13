/**
 * This class represents a simple BlockChain.
 * This BlockChain has exactly two instance members - an ArrayList to hold Blocks
 * and a chain hash to hold a SHA256 hash of the most recently added Block.
 *
 * Created Date: 1 March 2018
 * @author sisi rao
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Timestamp;

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
	
    /**
    * This method acts as a test driver for the BlockChain.
    * It begins by creating a BlockChain object and then adding the Genesis 
    * block to the chain. The Genesis block will be created with an empty string
    * as the pervious hash and a difficulty of 2.
    * 
    * All blocks have a difficulty entered by the user at run time. All hashes
    * will have the proper number of zero hex digits representing the most 
    * significant nibbles in the hash. A nibble is 4 bits. If the difficulty is 
    * specified as three, then all hashes will begin with 3 or more zero hex 
    * digits (or 3 nibbles, or 12 zero bits).
    * 
    * Blocks with higher difficulty will take more time for proof of work. Blocks
    * with Difficulty level of 4 and 5 usually takes more time to create than 
    * blocks with difficulty of 3 and lower.
    * 
    * @param newBlock is added to the BlockChain as the most recent block.
    */
    public static void main(String[] args)
    {
        BlockChain blockchain = new BlockChain();
        int index = 1;
        long start, end;

        loop: while(true) {
                    System.out.println("1. Add a transaction to the blockchain.\n" + 
                                    "\n" + 
                                    "2. Verify the blockchain.\n" + 
                                    "\n" + 
                                    "3. View the blockchain.\n" + 
                                    "\n" + 
                                    "4. Corrupt the chain.\n" + 
                                    "\n" + 
                                    "5. Hide the corruption by repairing the chain.\n" + 
                                    "\n" + 
                                    "6. Exit.");
                    Scanner sc = new Scanner(System.in);
                    int option = Integer.parseInt(sc.nextLine());
                    switch(option) {
                        case 1:{
                            System.out.println("Enter difficulty");
                int difficulty = Integer.parseInt(sc.nextLine());
                System.out.println("Enter transaction");
                String data = sc.nextLine();
                //Add transaction to the blockchain
                start = System.currentTimeMillis();
                Block b = new Block(index++,new Timestamp(start),data,difficulty);
                blockchain.addBlock(b);
                end = System.currentTimeMillis();
                System.out.printf("Total execution time to add this block %d milliseconds \n",(end-start));

                break;
                        }

                        case 2:{
                            System.out.println("Verifying");
                            start = System.currentTimeMillis();
                            if(blockchain.isChainVaild()) {
                                System.out.println("Chain verification: true");
                                }
                            else {
                                System.out.println("Chain verification: false");
                            }
                            end = System.currentTimeMillis();
                System.out.printf("Total execution time to verify the chain is %d milliseconds \n",(end-start));
                break;
                        }

                        case 3:{
                            System.out.println("View the Blockchain");
                            System.out.println(blockchain.toString());
                break;
                        }

                        case 4:{
                            System.out.println("Corrupt the Blockchain\n" + 
                                            "Enter block to corrupt");
                            int block_i = Integer.parseInt(sc.nextLine());
                            System.out.println("Enter new data for block "+block_i);
                            String data = sc.nextLine();
                            blockchain.blocks.get(block_i).setData(data);
                            System.out.printf("Block %d now holds %s\n",block_i,data);
                            break;

                        }

                        case 5:{
                            System.out.println("Repair the Blockchain");
                            blockchain.repairChain();
                            System.out.println("Repair complete");
                            break;
                        }

                        case 6:{
                            break loop;
                        }

                    }

            }		

    }
}
