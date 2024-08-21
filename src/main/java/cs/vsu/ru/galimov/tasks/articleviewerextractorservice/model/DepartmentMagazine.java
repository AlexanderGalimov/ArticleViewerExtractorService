package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model;

import lombok.Data;

import java.util.List;

@Data
public class DepartmentMagazine {
    private String name;

    private String url;

    private List<Archive> archives;

    public DepartmentMagazine(String name, String url, List<Archive> archives) {
        this.name = name;
        this.url = url;
        this.archives = archives;
    }

    public DepartmentMagazine() {
    }
}
