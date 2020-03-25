package dls.icesight.blocklynukkit;

import java.util.ArrayList;

public class ProgressBarThread implements Runnable {
    private ArrayList<Integer> proList = new ArrayList<Integer>();
    private int progress;
    private int totalSize;
    private boolean run = true;
    public ProgressBarThread(int totalSize){
        this.totalSize = totalSize;
    }
    public void updateProgress(int progress){
        synchronized (this.proList) {
            if(this.run){
                this.proList.add(progress);
                this.proList.notify();
            }
        }
    }
    public void finish(){
        this.run = false;
    }
    @Override
    public void run() {
        synchronized (this.proList) {
            try {
                while (this.run) {
                    if(this.proList.size()==0){
                        this.proList.wait();
                    }
                    synchronized (proList) {
                        this.progress += this.proList.remove(0);
                        System.err.println("当前进度："+(this.progress/this.totalSize*100)+"%");
                    }
                }
                System.err.println("下载完成");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
