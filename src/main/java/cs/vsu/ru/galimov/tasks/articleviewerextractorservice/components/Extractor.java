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

    private final ArticleSender articleSender;

    @Autowired
    public Extractor(VestnikHtmlPageParser pageParser, ArticleSender articleSender) {
        this.pageParser = pageParser;
        this.articleSender = articleSender;
    }

    public void extractArticles() {
        List<String> magazinesURlPieces = pageParser.parseGreetingPage();

        List<String> names = makeNames(magazinesURlPieces);

        List<String> magazinesURLs = pageParser.getAllMagazinesURLs(magazinesURlPieces);

        List<DepartmentMagazine> departmentMagazines = createDepartmentMagazines(names, magazinesURLs);

        for (DepartmentMagazine magazine : departmentMagazines) {
            List<Archive> currentArchives = pageParser.parseMagazineArchives(magazine.getUrl()).stream().toList();
            magazine.setArchives(currentArchives);
        }
        List<Article> articles;

        for (int i = 0; i < 2; i++) {
            for (Archive archive : departmentMagazines.get(i).getArchives()) {
                List<DateArchive> currDateArchives = findDateArchives(archive, magazinesURLs.get(i));
                articles = makeArticles(departmentMagazines.get(i), currDateArchives, archive);
                articleSender.sendMessagesToKafka(articles);
            }
        }
    }

    private List<Article> makeArticles(DepartmentMagazine departmentMagazine, List<DateArchive> currDateArchivesLinks, Archive archive) {
        List<Article> currArticles = new ArrayList<>();
        for (DateArchive currDateArchive : currDateArchivesLinks) {
            List<PDFParams> pdfParams = findPdfParams(currDateArchive.getLink(), archive.getType());
            for (PDFParams param : pdfParams) {
                currArticles.add(new Article(pageParser.getMagazine(), departmentMagazine, archive, currDateArchive, param));
            }
        }
        return currArticles;
    }

    private List<DateArchive> findDateArchives(Archive archive, String urlPiece) {
        return pageParser.parseDateArchives(archive, urlPiece);
    }

    private List<PDFParams> findPdfParams(String dateArchiveLink, ArchiveType type) {
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


