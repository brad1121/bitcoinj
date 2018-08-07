package org.bitcoinj.utils;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BlockFileLoaderTest {

    private String blocksDir = "/media/brad/DankMemes/btc cash/blockChain/blocks/";
    private List loadBlockList(){
        return loadBlockList(0);
    }
    private List loadBlockList(int startBlockFile){
        List<File> list = new LinkedList<File>();
        File testFile = new File(blocksDir + String.format(Locale.US, "blk%05d.dat", 0));
        assertEquals("Could not find a block file, check blocksDir is valid",true,testFile.exists());
        for (int i = 0; true; i++) {
            if(i >= startBlockFile){
                File file = new File(blocksDir + String.format(Locale.US, "blk%05d.dat", i));
                if (!file.exists()){
                    break;
                }

                list.add(file);
            }
            if(i >= ( startBlockFile + 1 ) ){
                // don't need to test more than 100
                break;
            }
        }
        if(list.isEmpty()){
            fail("Failed to load and blocks into list");
        }
        return list;
    }

    @Test
    public void LoadBlocksPreAug2017() throws BlockStoreException {
        NetworkParameters params = MainNetParams.get();
        BlockFileLoader loader = new BlockFileLoader(params,loadBlockList(0), params.getPrevPacketMagic());
        BlockStore store = new MemoryBlockStore(params);
        BlockChain blockChain = new BlockChain(params,store);
        int count = 0;
        for (Block block : loader) {
            try {
                System.out.println(block.getHashAsString()+": "+count);

                assertEquals(true,blockChain.add(block));
                 block.verifyHeader();

            } catch (Exception e) {
                System.out.println(e.toString());
                fail(e.toString());
            }finally {
                count++;
            }
        }
    }
}
