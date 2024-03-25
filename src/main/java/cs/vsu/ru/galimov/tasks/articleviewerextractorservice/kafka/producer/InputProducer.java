package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.kafka.producer;

import com.google.gson.Gson;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InputProducer {

    @Qualifier("kafkaTemplate")
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Gson gson;

    @Autowired
    public InputProducer(KafkaTemplate<String, String> kafkaTemplate, Gson gson) {
        this.kafkaTemplate = kafkaTemplate;
        this.gson = gson;
    }

    public void send(String topic, Article article) {
        String articleJson = convertArticleToJson(article);
        System.out.println("sent");
        kafkaTemplate.send(topic, articleJson);
    }

    private String convertArticleToJson(Article article) {
        return gson.toJson(article);
    }
}

