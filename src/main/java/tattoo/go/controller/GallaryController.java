package tattoo.go.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class GallaryController {

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @ResponseBody
    @RequestMapping("/head/images")
    public String[] getHeadImages() throws IOException {
    Resource[] tattoos = resourcePatternResolver.getResources("classpath*:/static/img-tattoos/*.jpg");
    List<Resource> tattoosList =  Arrays.asList(tattoos);
    Collections.shuffle(tattoosList);
    String[] images = new String[12];
            for (int i = 0; i < 12; i++) {
            Resource resource = tattoosList.get(i);
            images[i] = "/img-tattoos/" + resource.getFilename();
        }
    return images;
    }

}

