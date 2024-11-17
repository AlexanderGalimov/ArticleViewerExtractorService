package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.kafka.producer;

import com.google.gson.Gson;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InputProducer {

    @Qualifier("kafkaTemplate")
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Gson gson;

    private final Logger logger = LoggerFactory.getLogger(InputProducer.class);

    @Autowired
    public InputProducer(KafkaTemplate<String, String> kafkaTemplate, Gson gson) {
        this.kafkaTemplate = kafkaTemplate;
        this.gson = gson;
    }

    public void send(String topic, Article article) {
        String articleJson = convertArticleToJson(article);
        logger.info("Article successfully sent");
        kafkaTemplate.send(topic, articleJson);
    }

    private String convertArticleToJson(Article article) {
        return gson.toJson(article);
    }
}

