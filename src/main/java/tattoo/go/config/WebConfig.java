package tattoo.go.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Disable caching for a specific file or pattern
        registry.addResourceHandler("/picker.html")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }
}

