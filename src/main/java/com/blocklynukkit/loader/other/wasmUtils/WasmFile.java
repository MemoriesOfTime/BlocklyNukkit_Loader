package com.blocklynukkit.loader.other.wasmUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WasmFile {
    private RandomAccessFile raf = null;
    private boolean append = false;
    public WasmFile(String path, String controlFlag){
        if(controlFlag.contains("a")){append = true;controlFlag = controlFlag.replaceAll("a","");}
        if(controlFlag.equals("r+"))controlFlag = "rw";
        if(controlFlag.equals("w"))controlFlag = "rw";
        if(controlFlag.equals("w+"))controlFlag = "rw";
        if(controlFlag.equals("wr"))controlFlag = "rw";
        try {
            raf = new RandomAccessFile(path,controlFlag);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void seek(long pos){
        try {
            raf.seek(pos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
