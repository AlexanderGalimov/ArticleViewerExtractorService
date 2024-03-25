package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Magazine {
    private String name;
    private String link;

    public Magazine(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public Magazine() {
    }
}
