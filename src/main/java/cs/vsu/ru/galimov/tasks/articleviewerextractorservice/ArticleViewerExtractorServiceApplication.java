package cs.vsu.ru.galimov.tasks.articleviewerextractorservice;

import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.component.Extractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class ArticleViewerExtractorServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(ArticleViewerExtractorServiceApplication.class);

    public static void main(String[] args) throws IOException {
        ApplicationContext context = SpringApplication.run(ArticleViewerExtractorServiceApplication.class, args);

        Extractor extractor = context.getBean(Extractor.class);

        try {
            extractor.extractArticles();
        } catch (Exception e) {
            logger.error("Exception in main: " + e.getMessage());
        }
    }
}
