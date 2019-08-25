package cn.pan.controller;

import cn.pan.prediction.tfModel;
import cn.pan.service.EsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping
public class IndexController {

    public static Map<String, Integer> word2int = null;
    public static Map<Integer, String> int2word = null;

    @Autowired
    EsRestService restService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/search")
    public String search(Model model,
                         @RequestParam("keyword") String keyword) {
        String[] searchFields = {"title", "filecontent"};
        ArrayList<Map<String, Object>> fileList = restService.searchDocs("userdoc",
                keyword, searchFields, 1, 10);
        model.addAttribute("flist", fileList);
        model.addAttribute("keyword", keyword);

        return "result";
    }

    @RequestMapping("/suggest")
    public String suggest(Model model,
                         @RequestParam("keyword") String keyword) {

        int[] maxWords = tfModel.getMaxWords(word2int.get(keyword));
        keyword = int2word.get(maxWords[1]);
        System.out.println(keyword);
        String[] searchFields = {"title", "filecontent"};
        ArrayList<Map<String, Object>> fileList = restService.searchDocs("userdoc",
                keyword, searchFields, 1, 10);
        model.addAttribute("flist", fileList);
        model.addAttribute("keyword", keyword);

        return "result";
    }

}
