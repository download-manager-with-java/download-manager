package Download.Manager.Controller;

import java.io.IOException;
import java.util.*;

import static Download.Manager.Controller.OKHttp.save;

public class ThreadPool {
    String url;
    int threadnum;
    Thread[] thread;
    ThreadGroup threadGroup;
    static boolean pause=false;
    static boolean stop=false;
    static Map<String,String> downloaddetail=new HashMap<>();
    public ThreadPool(String url) {
        this.url = url;
        this.threadnum = threadnum;
    }
    public void MultiThreadOK() throws Exception {
        OKHttp okHttp=new OKHttp();
        int length = okHttp.request(url);
        downloaddetail.put("size", String.valueOf(length));
        System.out.println("the lenght of the file is : " + length);
        threadnum=getThreadnum(length);
        downloaddetail.put("threadnum", String.valueOf(threadnum));
        downloaddetail.put("Name", OKHttp.filename);
        threadGroup = new ThreadGroup("new Group");
        thread = new Thread[threadnum];
        int ThraedTaskNumber = length / threadnum;
        for (int i = 0; i < threadnum; i++) {
                int finalI = i;
                int finalI1 = i;
                thread[i] = new Thread(threadGroup,new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OKHttp ok=new OKHttp();
                            int start, end;
                            start = finalI * ThraedTaskNumber;
                            end = ThraedTaskNumber * (finalI + 1);
                            downloaddetail.put("Range", String.valueOf(end-start));
                            System.out.println("ranges for thread" + finalI + " : from " + start + " to " + end);
                            ok.range(url,start, end,finalI);
                            System.out.println("ok");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread[i].setName("Thread-"+i);
                thread[i].start();
            }
           System.out.println(threadGroup.activeCount() + " threads in thread group...");
            for (int i = 0; i < threadnum; i++) {
                thread[i].join();
                System.out.println("join");
            }
            if (length % threadnum != 0) {
                Thread thread=new Thread(threadGroup,new Runnable(){
                OKHttp ok=new OKHttp();
                    @Override
                    public void run() {
                        try {
                            System.out.println(threadGroup.activeCount() + " threads in thread group...");
                            ok.range(url,threadnum * ThraedTaskNumber, length,-1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("ok");
                    }
                });
                thread.setName("Thread-"+threadnum);
                thread.start();
                thread.join();
            }
            int count=0;
            for(int i=0;i<threadnum;i++)
            {
                if(thread[i].getState()== Thread.State.TERMINATED)
                {
                    count++;
                }
            }
            if(count==threadnum && !stop)
            {
                OKHttp ok = new OKHttp();
                ok.write();
            }
            stop=false;
        System.out.println(threadGroup.activeCount() + " threads in thread group...");

    }
    public  void pause() throws InterruptedException {
        // thread group interrupted
        System.out.println("pause "+pause);
        synchronized (threadGroup) {
            while (pause) {
                System.out.println(threadGroup.activeCount() + "pause");
//                threadGroup.wait();
                threadGroup.suspend();
                System.out.println(threadGroup.activeCount() + "paused");
            }
        }
    }
    public  void stop() throws InterruptedException {
        // thread group interrupted
        System.out.println("stop " + pause);
        save();
        synchronized (threadGroup) {
                System.out.println(threadGroup.activeCount() + "stop");
                threadGroup.stop();
                System.out.println(threadGroup.activeCount() + "stoped");
        }
    }
    public void resume() throws InterruptedException {
        synchronized (threadGroup) {
        System.out.println(threadGroup.activeCount()+"resume");
//            threadGroup.notifyAll();
            threadGroup.resume();
        }
    }
    public int getThreadnum(int length)
    {
        long Megabyte=1048576;//bytes
        long Gigabyte=1073741824;//bytes
        int threadnum=0;
        if(length<Megabyte)
            threadnum =1;
        else if(length<100*Megabyte)
            threadnum=2;
        else if(length<500*Megabyte)
            threadnum=3;
        else if(length<Gigabyte)
            threadnum=4;
        else if(length<2*Gigabyte)
            threadnum=5;
        else if(length<3*Gigabyte)
            threadnum=6;
        else
            threadnum=7;
        return threadnum;
    }
    public void continuedownload() throws Exception {
        Map<Integer, Integer> ranges=OKHttp.downloadrange(url);
        threadnum=ranges.size();
        OKHttp okHttp=new OKHttp();
        int length = okHttp.request(url);
        downloaddetail.put("threadnum ", String.valueOf(threadnum));
        downloaddetail.put("Name ", OKHttp.filename);
        threadGroup = new ThreadGroup("new Group");
        thread = new Thread[threadnum];
        Set<Integer> srange= ranges.keySet();
        Integer[] startrange=srange.toArray(new Integer[srange.size()]);
        Collection<Integer> erange= ranges.values();
        Integer[] endrange=erange.toArray(new Integer[erange.size()]);
        for (int i = 0; i < threadnum; i++) {
            int finalI = i;
            thread[i] = new Thread(threadGroup,new Runnable() {
                @Override
                public void run() {
                    try {
                        OKHttp ok=new OKHttp();
                        int start, end;
                        start = startrange[finalI];
                        end = endrange[finalI];
                        downloaddetail.put("Range", String.valueOf(end-start));
                        System.out.println("ranges for thread" + finalI + " : from " + start + " to " + end);
                        ok.range(url,start, end,finalI);
                        System.out.println("ok");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread[i].setName("Thread-"+i);
            thread[i].start();
            System.out.println(threadGroup.activeCount() + " threads in thread group...");
        }
        System.out.println(threadGroup.activeCount() + " threads in thread group...");
        for (int i = 0; i < threadnum; i++) {
            thread[i].join();
            System.out.println("join");
        }
        int count=0;
        for(int i=0;i<threadnum;i++)
        {
            if(thread[i].getState()== Thread.State.TERMINATED)
            {
                count++;
            }
        }
        if(count==threadnum && !stop)
        {
            OKHttp ok = new OKHttp();
            ok.write();
            OKHttp.list.remove(url);
        }
        stop=false;
        System.out.println(threadGroup.activeCount() + " threads in thread group...");

    }

}
