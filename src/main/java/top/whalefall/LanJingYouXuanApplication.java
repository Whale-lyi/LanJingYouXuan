package top.whalefall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("top.whalefall.mapper")
@SpringBootApplication
public class LanJingYouXuanApplication {

    public static void main(String[] args) {
        SpringApplication.run(LanJingYouXuanApplication.class, args);
    }

}
