package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.components;

import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.*;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.parser.VestnikHtmlPageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Configuration
@Component
public class Extractor {

    private final VestnikHtmlPageParser pageParser;

    @Autowired
    public Extractor(VestnikHtmlPageParser pageParser) {
        this.pageParser = pageParser;
    }

    public List<Article> extractArticles() {
        List<String> magazinesURlPieces = pageParser.parseGreetingPage();

        List<String> names = makeNames(magazinesURlPieces);

        List<String> magazinesURLs = pageParser.getAllMagazinesURLs(magazinesURlPieces);

        List<DepartmentMagazine> departmentMagazines = createDepartmentMagazines(names, magazinesURLs);

        List<String> archives = new ArrayList<>();

        for (DepartmentMagazine magazine : departmentMagazines) {
            archives.add(pageParser.parseMagazineArchives(magazine));
        }

        List<Article> articles = makeArticles(departmentMagazines.get(0), archives.get(0), magazinesURLs.get(0));

        return articles;
    }


    private List<Article> makeArticles(DepartmentMagazine departmentMagazine, String archive, String urlPiece) {
        List<DateArchive> currDateArchivesLinks = findDateArchives(archive, urlPiece, departmentMagazine.getType());

        List<Article> currArticles = new ArrayList<>();
        for (DateArchive currDateArchive : currDateArchivesLinks) {
            List<PDFParams> pdfParams = findPdfParams(currDateArchive.getLink(), departmentMagazine.getType());
            for (PDFParams param : pdfParams) {
                currArticles.add(new Article(pageParser.getMagazine(), departmentMagazine, currDateArchive, param));
            }
        }
        return currArticles;
    }

    private List<DateArchive> findDateArchives(String archive, String urlPiece, String type) {
        return pageParser.parseDateArchives(archive,
                urlPiece, type);
    }

    private List<PDFParams> findPdfParams(String dateArchiveLink, String type) {
        return pageParser.parsePdfParamsFromDateArchive(dateArchiveLink, type);
    }

    private List<String> makeNames(List<String> urlPieces) {
        List<String> names = new ArrayList<>();

        for (String journal : urlPieces) {
            names.add(journal.split("/")[2]);
        }
        return names;
    }

    private List<DepartmentMagazine> createDepartmentMagazines(List<String> names, List<String> journals) {
        List<DepartmentMagazine> departmentMagazines = new ArrayList<>();

        for (int i = 0; i < journals.size(); i++) {
            departmentMagazines.add(new DepartmentMagazine(names.get(i), journals.get(i), null));
        }
        return departmentMagazines;
    }
}


