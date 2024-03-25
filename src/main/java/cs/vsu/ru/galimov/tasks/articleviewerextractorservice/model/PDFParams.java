package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public class PDFParams {
    private String link;

    private List<String> authors;

    private String title;

    public PDFParams(String link, List<String> authors, String title) {
        this.link = link;
        this.authors = authors;
        this.title = title;
    }

    public PDFParams() {
    }
}
