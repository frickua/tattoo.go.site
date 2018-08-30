import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import tattoo.go.Application;
import tattoo.go.controller.GallaryController;

import java.util.Collections;

@RunWith(SpringRunner.class)

@SpringBootTest(classes = Application.class)

@WebAppConfiguration
public class HeadImagesTest {

    private MockMvc mvc;

//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    @Before
//    public void setup() throws Exception {
//        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//    }

    @Autowired
    GallaryController gallaryController;

    @Test
    public void headImagesTest() throws IOException {
        String[] images = gallaryController.getHeadImages();
        System.out.println(Arrays.toString(images));
    }
}

