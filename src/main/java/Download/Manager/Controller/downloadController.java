package Download.Manager.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.List;


@Controller
public class downloadController {
    /**
     * sponch bob
     * https://hajifirouz1.cdn.asset.aparat.com/aparat-video/0334124f62568f37fa11f1106332dd1129075200-720p.mp4
     * */
    ThreadPool threadPool;
    @GetMapping("/download")
    public String index() {
        return "page";
    }

    @RequestMapping(path = "/download",method = RequestMethod.POST)
    public String download(@RequestParam("URL") String URL,Model model) throws Exception {
        System.out.println("url : "+URL);
        threadPool=new ThreadPool(URL);
        threadPool.MultiThreadOK();
        model.addAttribute("Name", ThreadPool.downloaddetail.get("Name"));
        model.addAttribute("size", ThreadPool.downloaddetail.get("size"));
        model.addAttribute("threadnum", ThreadPool.downloaddetail.get("threadnum"));
        model.addAttribute("Range", ThreadPool.downloaddetail.get("Range"));
        return  "page";
    }
    @GetMapping(path = "/pause")
    public String pause() throws Exception {
        ThreadPool.pause=true;
        threadPool.pause();
        return  "page";
    }
    @GetMapping(path = "/resume")
    public String resume() throws Exception {
        ThreadPool.pause=false;
        threadPool.resume();
        return  "page";
    }
    @GetMapping(path = "/stop")
    public String stop() throws Exception {
        ThreadPool.stop=true;
        threadPool.stop();
        return  "page";
    }
}
