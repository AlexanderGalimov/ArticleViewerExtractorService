package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DateArchive {
    private String info;

    private String link;

    public DateArchive(String info, String link) {
        this.info = info;
        this.link = link;
    }

    public DateArchive() {
    }
}
