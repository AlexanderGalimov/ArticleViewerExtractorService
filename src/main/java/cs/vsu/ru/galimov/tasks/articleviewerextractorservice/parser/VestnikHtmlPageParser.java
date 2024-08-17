package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.parser;

import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.DateArchive;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.DepartmentMagazine;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.Magazine;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.PDFParams;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.parser.config.HtmlParseConfig;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@Component
public class VestnikHtmlPageParser {
    private static final String mainURL = "http://www.vestnik.vsu.ru";
    private final Magazine magazine;
    private final HtmlParseConfig config = new HtmlParseConfig(mainURL);

    public VestnikHtmlPageParser() {
        this.magazine = new Magazine("Vestnik", mainURL);
    }

    public List<String> parseGreetingPage() {
        return parseLinkFromPage(config.getMainUrl(), config.getOpts().get("mainURL").get("opt"));
    }

    public List<String> getAllMagazinesURLs(List<String> names) {
        if (!names.isEmpty()) {
            return makeURlJournal(names);
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> makeURlJournal(List<String> links) {
        List<String> result = new ArrayList<>();
        for (String content : links) {
            result.add(config.getMainUrl() + content);
        }
        return result;
    }

    private List<String> parseLinkFromPage(String url, String selectOption) {
        try {
            List<String> result = new ArrayList<>();

            Document document = Jsoup.connect(url).get();

            Elements elements = document.select(selectOption);

            for (Element link : elements) {
                String currURl = link.attr("href");
                result.add(currURl);
            }
            return result;
        } catch (Exception e) {
            System.out.println("Error in parse link from page listen" + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<String> parseInfoFromDateArchivePage(String url, String type) {
        List<String> dateArchivesInfo = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            if (Objects.equals(type, "type1")) {
                Elements summaryElements = document.select(config.getOpts().get("selectOptionsInfoDateArchives").get("type1doc"));

                for (Element summaryElement : summaryElements) {
                    String currString = "";
                    Element titleElement = summaryElement.select(config.getOpts().get("selectOptionsInfoDateArchives").get("type1title")).first();
                    Element seriesElement = summaryElement.select(config.getOpts().get("selectOptionsInfoDateArchives").get("type1series")).first();

                    String titleText = titleElement.text();
                    String seriesText = seriesElement.text();
                    currString = currString + titleText + " " + seriesText;

                    dateArchivesInfo.add(currString);
                }
            } else {
                Elements listItems = document.select(config.getOpts().get("selectOptionsInfoDateArchives").get("type2doc"));

                for (Element listItem : listItems) {
                    String currLink = "";
                    String listItemText = listItem.text();

                    if (listItemText.matches(config.getOpts().get("selectOptionsInfoDateArchives").get("type2title"))) {
                        listItemText = listItemText.replace("Содержание", "").trim();
                        currLink = currLink + listItemText;

                        dateArchivesInfo.add(currLink);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in parseInfoFromDateArchivePage" + e.getMessage());
        }
        return dateArchivesInfo;
    }


    public String parseMagazineArchives(DepartmentMagazine departmentMagazine) {
        String result = "";

        List<String> archiveType1 = parseLinkFromPage(departmentMagazine.getUrl(), config.getOpts().get("selectOptionsArchives").get("type1new"));

        List<String> archiveType2 = parseLinkFromPage(departmentMagazine.getUrl(), config.getOpts().get("selectOptionsArchives").get("type2"));

        if (!archiveType1.isEmpty()) {
            departmentMagazine.setType("type1");
            result = archiveType1.get(0);
        } else if (!archiveType2.isEmpty()) {
            departmentMagazine.setType("type2");
            result = config.getMainUrl() + archiveType2.get(0);
        }
        return result;
    }

    public List<PDFParams> parsePdfParamsFromDateArchive(String datedArchiveUrl, String type) {
        List<String> titles = new ArrayList<>();
        List<String> authors = new ArrayList<>();
        List<String> pdfLinks = new ArrayList<>();
        List<PDFParams> pdfParams = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(datedArchiveUrl).get();
            if (Objects.equals(type, "type1")) {
                Elements articleSummaries = doc.select(config.getOpts().get("selectOptionsPdfParams").get("type1doc"));

                for (Element articleSummary : articleSummaries) {
                    Element titleElement = articleSummary.select(config.getOpts().get("selectOptionsPdfParams").get("type1title")).first();
                    String title = titleElement.text();
                    titles.add(title);

                    Element authorsElement = articleSummary.select(config.getOpts().get("selectOptionsPdfParams").get("type1authors")).first();
                    String author = authorsElement.text();
                    authors.add(author);
                }
            } else {
                Elements listItems = doc.select(config.getOpts().get("selectOptionsPdfParams").get("type2doc"));
                for (Element listItem : listItems) {
                    String author = listItem.select(config.getOpts().get("selectOptionsPdfParams").get("type2authors")).text();
                    String title = listItem.select(config.getOpts().get("selectOptionsPdfParams").get("type2title")).text();
                    if (title != null && author != null) {
                        titles.add(title);
                        authors.add(author);
                    }
                }
            }

            List<String> links = parsePDFPageLink(datedArchiveUrl, type);

            if (Objects.equals(type, "type1")) {
                for (String link : links) {
                    pdfLinks.add(parsePDFDownloadLink(link, "type1"));
                }
            } else {
                pdfLinks = links;
            }

            for (int i = 0; i < pdfLinks.size(); i++) {
                String[] authorNamesArray = authors.get(i).split(", ");
                if(authorNamesArray.length == 0){
                    authorNamesArray = authors.get(i).split("; ");
                }

                pdfParams.add(new PDFParams(pdfLinks.get(i), Arrays.asList(authorNamesArray), titles.get(i)));
            }

        } catch (Exception e) {
            System.out.println("Error in parsePdfParamsFromDateArchive" + e.getMessage());
        }
        return pdfParams;
    }

    public List<DateArchive> parseDateArchives(String archive, String content, String typeOfDepartmentMagazine) {
        List<DateArchive> dateArchives = new ArrayList<>();
        List<String> dateArchivesLinks = parseLinkFromPage(archive, config.getOpts().get("selectOptionsLinksArchivesByDate").get(typeOfDepartmentMagazine));

        if (Objects.equals(typeOfDepartmentMagazine, "type2")) {
            dateArchivesLinks = makeURlArchives(content, dateArchivesLinks);
        }

        List<String> info = parseInfoFromDateArchivePage(archive, typeOfDepartmentMagazine);
        if (dateArchivesLinks.size() == info.size()) {
            for (int i = 0; i < dateArchivesLinks.size(); i++) {
                dateArchives.add(new DateArchive(info.get(i), dateArchivesLinks.get(i)));
            }
        }

        return dateArchives;
    }

    public List<String> makeURlArchives(String content, List<String> archivesByDate) {
        List<String> result = new ArrayList<>();
        for (String archive : archivesByDate) {
            result.add(content + archive);
        }
        return result;
    }

    public List<String> parsePDFPageLink(String datedArchiveURL, String type) {
        List<String> pdfLinksByDate = new ArrayList<>();

        if (Objects.equals(type, "type1")) {
            pdfLinksByDate = parseLinkFromPage(datedArchiveURL, config.getOpts().get("selectOptionsPDFLinks").get("type1"));
        } else {
            for (String page : parseLinkFromPage(datedArchiveURL, config.getOpts().get("selectOptionsPDFLinks").get("type2"))) {
                pdfLinksByDate.add(config.getMainUrl() + page);
            }
        }
        return pdfLinksByDate;
    }

    public String parsePDFDownloadLink(String url, String type) {
        if (Objects.equals(type, "type1")) {
            return parseLinkFromPage(url, config.getOpts().get("selectOptionsPDFDownload").get("type1")).get(0);
        } else {
            return null;
        }
    }
}
