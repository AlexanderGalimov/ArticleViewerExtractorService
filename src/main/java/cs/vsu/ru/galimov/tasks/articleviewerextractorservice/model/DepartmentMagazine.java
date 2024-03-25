package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DepartmentMagazine {
    private String name;

    private String url;

    private String type;

    public DepartmentMagazine(String name, String url, String type) {
        this.name = name;
        this.url = url;
        this.type = type;
    }

    public DepartmentMagazine() {
    }
}
