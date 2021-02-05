package Download.Manager.Controller;



import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.MapExtra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class OKHttp {
    public static OkHttpClient client;
    String saveDir="E://";
    int length;
    static String savedir;
    static byte[] bytes;
    FileOutputStream fileOutputStream = null;
    static String filename;
    static DB db= DBMaker.fileDB("lists.db")
            .checksumHeaderBypass()
            .transactionEnable()
            .closeOnJvmShutdown()
            .make();
    static Map<String, byte[]> list;
    static String url;
    public int request(String downloadUrl) throws Exception {

        client = new OkHttpClient();
        client.setConnectTimeout(15, TimeUnit.HOURS); // connect timeout
        client.setReadTimeout(15, TimeUnit.HOURS);    // socket timeout
        client.setWriteTimeout(15, TimeUnit.HOURS);    // socket timeout
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        length= Math.toIntExact(response.body().contentLength());
        bytes=new byte[length];
        url=downloadUrl;
        filename=getFileName(downloadUrl);
        savedir=saveDir+ File.separator+filename;
        response.body().close();
        return length;
    }
    public void range(String downloadUrl,int start,int end,int n) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(15, TimeUnit.HOURS); // connect timeout
        okHttpClient.setReadTimeout(15, TimeUnit.HOURS);    // socket timeout
        okHttpClient.setWriteTimeout(15, TimeUnit.HOURS);    // socket timeout
        Request request = new Request.Builder()
                .header("Range","bytes="+start+"-"+end)
                .url(downloadUrl)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        byte[] b=response.body().bytes();
        int j=0;
        savedir=saveDir+getFileName(downloadUrl);
        for (int i=start;i<end;i++) {
            if(i%25==0)
            {
                System.out.print(".");
            }
            bytes[i]=b[j++];
        }
        response.body().close();
    }

    public void write() throws IOException {
        System.out.println(savedir);
        fileOutputStream = new FileOutputStream(savedir);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }
    public String getFileName(String downloadUrl)
    {
        String fileName;
        fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
        return fileName;
    }
    public static void save()
    {
        openDB();
        list.put(url,bytes);
        closeDB();
    }
    public static void closeDB()
    {
        db.commit();
//        db.close();
    }
    public static void openDB()
    {
        list = (Map<String, byte[]>) db.hashMap("list")
                .createOrOpen();
    }
    public Set<String> list()
    {
        openDB();
        Set<String> filenames=list.keySet();
        closeDB();
        return filenames;
    }
    public static Map<Integer, Integer> downloadrange(String url)
    {
        byte[] bytes=list.get(url);
        int start=0,end=0;
        int counter=0;
        Map<Integer, Integer> ranges=new HashMap<>();
        for(int i=0;i<bytes.length;i++)
        {
            if(bytes[i]==0)
            {
                counter++;
                if(i==bytes.length-1)
                {
                    start++;
                    end=(start+counter)-1;
                    if(counter!=0)
                    {
                        ranges.put(start,end);
                        counter=0;
                    }
                }
            }
            else if(bytes[i]!=0)
            {
                if(start!=0)start++;
                end=(start+counter)-1;
                if(counter!=0)
                {
                    ranges.put(start,end);
                    counter=0;
                }
                start=i;
            }
        }
        return ranges;
    }
}
