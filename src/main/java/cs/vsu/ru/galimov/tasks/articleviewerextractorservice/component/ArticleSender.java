package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.component;

import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.kafka.producer.InputProducer;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.kafka.topic.InputTopic;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Configuration
public class ArticleSender {

    private final InputProducer producer;

    private final InputTopic topic;

    @Autowired
    public ArticleSender(InputProducer producer, InputTopic topic) {
        this.producer = producer;
        this.topic = topic;
    }

    public void sendMessagesToKafka(List<Article> articles) {
        for (Article article : articles) {
            producer.send(topic.getTopicName(), article);
        }
    }
}
