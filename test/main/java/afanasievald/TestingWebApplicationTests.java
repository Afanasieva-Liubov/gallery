package afanasievald;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import afanasievald.controller.GalleryController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestingWebApplicationTests {

    @Autowired
    private GalleryController controller;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }
}
