package tattoo.go.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Map;

import tattoo.go.service.TattooImgService;

@Controller
public class MainController {

    @Autowired
    private TattooImgService tattooImgService;

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) throws IOException {
        model.put("portfolio", tattooImgService.getImagesFromGPhotos(null));
        return "index";
    }
}
