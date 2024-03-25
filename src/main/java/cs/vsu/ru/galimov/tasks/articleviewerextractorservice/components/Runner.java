package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.components;

import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.kafka.producer.InputProducer;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.kafka.topic.InputTopic;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Configuration
public class Runner {

    private final InputProducer producer;

    private final Extractor extractor;

    private final InputTopic topic;

    @Autowired
    public Runner(InputProducer producer, Extractor extractor, InputTopic topic) {
        this.producer = producer;
        this.extractor = extractor;
        this.topic = topic;
    }

    public void run() {
        List<Article> articles = extractor.extractArticles();
        sendMessagesToKafka(articles);
    }

    public void sendMessagesToKafka(List<Article> articles) {
        for (Article article : articles) {
            producer.send(topic.getTopicName(), article);
        }
    }
}
