package cs.vsu.ru.galimov.tasks.articleviewerextractorservice;

import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.components.Extractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ArticleViewerExtractorServiceApplication {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = SpringApplication.run(ArticleViewerExtractorServiceApplication.class, args);

        Extractor extractor = context.getBean(Extractor.class);

        extractor.extractArticles();
    }

}
