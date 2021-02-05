package Download.Manager.Controller;

import java.util.HashMap;
import java.util.Map;

public class array {
    public static void main(String[] args) {
        byte[] bytes={0,0,0,3,4,5,0,0,0,9,0,0,0,0,14,15,16,0,0,0,20};
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
        System.out.println(ranges.entrySet());
    }
}
