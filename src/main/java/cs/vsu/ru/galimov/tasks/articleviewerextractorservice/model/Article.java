package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.data.mongodb.core.index.TextIndexed;

@Data
@JsonDeserialize
@JsonSerialize
public class Article {

    private String id;

    private Magazine magazine;

    private DepartmentMagazine departmentMagazine;

    private Archive archive;

    private DateArchive dateArchive;

    private PDFParams pdfParams;

    public Article(Magazine magazine, DepartmentMagazine departmentMagazine, Archive archive, DateArchive dateArchive, PDFParams pdfParams) {
        this.magazine = magazine;
        this.departmentMagazine = departmentMagazine;
        this.archive = archive;
        this.dateArchive = dateArchive;
        this.pdfParams = pdfParams;
    }

    public Article() {
    }
}

