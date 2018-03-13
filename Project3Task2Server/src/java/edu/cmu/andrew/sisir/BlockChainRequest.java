
package edu.cmu.andrew.sisir;

/**
 * BlockChainRequest.java Class is a simple request that contains the information
 * the client passes in.
 * 
 * @author sisi
 */
class BlockChainRequest {
    int option;
    String data;
    int difficulty;
    String signature;

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    public BlockChainRequest(int op,String d,int dif,String sig){
        this.option = op;
        this.data = d;
        this.difficulty = dif;
        this.signature = sig;
    }
    
    
}
