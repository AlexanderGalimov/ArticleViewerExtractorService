package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.TextIndexed;

@Getter
@Setter
@ToString
@JsonDeserialize
@JsonSerialize
public class Article {

    private String id;

    private Magazine magazine;

    private DepartmentMagazine departmentMagazine;

    private DateArchive dateArchive;

    private PDFParams pdfParams;

    @TextIndexed
    private String fullText;

    private String uniqUIIDS3;

    public Article(Magazine magazine, DepartmentMagazine departmentMagazine, DateArchive dateArchive, PDFParams pdfParams) {
        this.magazine = magazine;
        this.departmentMagazine = departmentMagazine;
        this.dateArchive = dateArchive;
        this.pdfParams = pdfParams;
    }

    public Article() {
    }
}

