package cs.vsu.ru.galimov.tasks.articleviewerextractorservice;

import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.components.Runner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ArticleViewerExtractorServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ArticleViewerExtractorServiceApplication.class, args);

        Runner runner = context.getBean(Runner.class);

        runner.run();
    }

}
