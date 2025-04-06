package cs.vsu.ru.galimov.tasks.articleviewerextractorservice.parser;

import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.model.*;
import cs.vsu.ru.galimov.tasks.articleviewerextractorservice.parser.config.HtmlParseConfig;
import lombok.Getter;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Getter
@Component
public class VestnikHtmlPageParser {
    private static final String mainURL = "http://www.vestnik.vsu.ru";

    private final Magazine magazine;

    private final HtmlParseConfig config = new HtmlParseConfig(mainURL);

    private final Logger logger = LoggerFactory.getLogger(VestnikHtmlPageParser.class);

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
        List<String> result = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com/")
                    .timeout(10_000)
                    .ignoreHttpErrors(true)
                    .execute()
                    .parse();

            Elements elements = document.select(selectOption);
            for (Element link : elements) {
                result.add(link.attr("href"));
            }

        } catch (HttpStatusException e) {
            logger.error("HTTP error: " + e.getStatusCode() + " for URL: " + url);
        } catch (IOException e) {
            logger.error("I/O error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage());
        }

        return result;
    }


//    private List<String> parseLinkFromPage(String url, String selectOption) {
//        try {
//            List<String> result = new ArrayList<>();
//
//            Document document = Jsoup.connect(url).get();
//
//            Elements elements = document.select(selectOption);
//
//            for (Element link : elements) {
//                String currURl = link.attr("href");
//                result.add(currURl);
//            }
//            return result;
//        } catch (Exception e) {
//            logger.error("Error in parse link from page listen" + e.getMessage());
//        }
//        return new ArrayList<>();
//    }

    public List<String> parseInfoFromDateArchivePage(String url, ArchiveType type) {
        List<String> dateArchivesInfo = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            if (type == ArchiveType.NEW) {
                Elements summaryElements = document.select(config.getOpts().get("selectOptionsInfoDateArchives").get("newDoc"));

                for (Element summaryElement : summaryElements) {
                    String currString = "";
                    Element titleElement = summaryElement.select(config.getOpts().get("selectOptionsInfoDateArchives").get("newTitle")).first();
                    Element seriesElement = summaryElement.select(config.getOpts().get("selectOptionsInfoDateArchives").get("newSeries")).first();

                    String titleText = titleElement.text();
                    String seriesText = seriesElement.text();
                    currString = currString + titleText + " " + seriesText;

                    dateArchivesInfo.add(currString);
                }
            } else {
                Elements listItems = document.select(config.getOpts().get("selectOptionsInfoDateArchives").get("oldDoc"));

                for (Element listItem : listItems) {
                    String currLink = "";
                    String listItemText = listItem.text();

                    if (listItemText.matches(config.getOpts().get("selectOptionsInfoDateArchives").get("oldTitle"))) {
                        listItemText = listItemText.replace("Содержание", "").trim();
                        currLink = currLink + listItemText;

                        dateArchivesInfo.add(currLink);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in parseInfoFromDateArchivePage" + e.getMessage());
        }
        return dateArchivesInfo;
    }


    public Set<Archive> parseMagazineArchives(String departmentMagazineLink) {
        Set<Archive> archives = new HashSet<>();

        List<String> archiveMixed = parseLinkFromPage(departmentMagazineLink, config.getOpts().get("selectOptionsArchives").get("mix"));

        List<String> archiveOld = parseLinkFromPage(departmentMagazineLink, config.getOpts().get("selectOptionsArchives").get("old"));

        if (!archiveOld.isEmpty()) {
            archives.add(new Archive(config.getMainUrl() + archiveOld.get(0), ArchiveType.OLD));
        } else {
            for (String archiveLink : archiveMixed) {
                Archive archive = new Archive(archiveLink, null);
                if (archiveLink.contains("journals.vsu.ru")) {
                    archive.setType(ArchiveType.NEW);
                } else if (archiveLink.contains("vestnik.vsu.ru")) {
                    archive.setType(ArchiveType.OLD);
                }
                archives.add(archive);
            }
        }
        return archives;
    }

    public List<PDFParams> parsePdfParamsFromDateArchive(String datedArchiveUrl, ArchiveType type) {
        List<String> titles = new ArrayList<>();
        List<String> authors = new ArrayList<>();
        List<String> pdfLinks = new ArrayList<>();
        List<PDFParams> pdfParams = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(datedArchiveUrl).get();
            if (type == ArchiveType.NEW) {
                Elements articleSummaries = doc.select(config.getOpts().get("selectOptionsPdfParams").get("newDoc"));

                for (Element articleSummary : articleSummaries) {
                    Element titleElement = articleSummary.select(config.getOpts().get("selectOptionsPdfParams").get("newTitle")).first();
                    String title = titleElement.text();
                    titles.add(title);

                    Element authorsElement = articleSummary.select(config.getOpts().get("selectOptionsPdfParams").get("newAuthors")).first();
                    String author = authorsElement.text();
                    authors.add(author);
                }
            } else {
                Elements listItems = doc.select(config.getOpts().get("selectOptionsPdfParams").get("oldDoc"));
                for (Element listItem : listItems) {
                    String author = listItem.select(config.getOpts().get("selectOptionsPdfParams").get("oldAuthors")).text();
                    String title = listItem.select(config.getOpts().get("selectOptionsPdfParams").get("oldTitle")).text();
                    if (title != null && author != null) {
                        titles.add(title);
                        authors.add(author);
                    }
                }
            }

            List<String> links = parsePDFPageLink(datedArchiveUrl, type);

            if (type == ArchiveType.NEW) {
                for (String link : links) {
                    pdfLinks.add(parsePDFDownloadLink(link, type));
                }
            } else {
                pdfLinks = links;
            }

            for (int i = 0; i < pdfLinks.size(); i++) {
                String[] authorNamesArray = authors.get(i).split(", ");
                if (authorNamesArray.length == 0) {
                    authorNamesArray = authors.get(i).split("; ");
                }

                pdfParams.add(new PDFParams(pdfLinks.get(i), Arrays.asList(authorNamesArray), titles.get(i)));
            }

        } catch (Exception e) {
            logger.error("Error in parsePdfParamsFromDateArchive" + e.getMessage());
        }
        return pdfParams;
    }

    public List<DateArchive> parseDateArchives(Archive archive, String content) {
        List<DateArchive> dateArchives = new ArrayList<>();
        List<String> dateArchivesLinks = new ArrayList<>();
        List<String> info = new ArrayList<>();

        if (archive.getType() == ArchiveType.NEW) {
            List<String> currentDateArchivesLinks;
            List<String> currentInfo;
            int index = 1;
            do {
                currentDateArchivesLinks = parseLinkFromPage(archive.getLink() + "/" + index, config.getOpts().get("selectOptionsLinksArchivesByDate").get("new"));
                dateArchivesLinks.addAll(currentDateArchivesLinks);
                currentInfo = parseInfoFromDateArchivePage(archive.getLink() + "/" + index, archive.getType());
                info.addAll(currentInfo);
                index++;
            } while (!currentDateArchivesLinks.isEmpty());
        } else {
            dateArchivesLinks = parseLinkFromPage(archive.getLink(), config.getOpts().get("selectOptionsLinksArchivesByDate").get("old"));
            dateArchivesLinks = makeURlArchives(content, dateArchivesLinks);
            info = parseInfoFromDateArchivePage(archive.getLink(), archive.getType());
        }

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

    public List<String> parsePDFPageLink(String datedArchiveURL, ArchiveType type) {
        List<String> pdfLinksByDate = new ArrayList<>();

        if (type == ArchiveType.NEW) {
            pdfLinksByDate = parseLinkFromPage(datedArchiveURL, config.getOpts().get("selectOptionsPDFLinks").get("new"));
        } else {
            for (String page : parseLinkFromPage(datedArchiveURL, config.getOpts().get("selectOptionsPDFLinks").get("old"))) {
                pdfLinksByDate.add(config.getMainUrl() + page);
            }
        }
        return pdfLinksByDate;
    }

    public String parsePDFDownloadLink(String url, ArchiveType type) {
        if (type == ArchiveType.NEW) {
            return parseLinkFromPage(url, config.getOpts().get("selectOptionsPDFDownload").get("new")).get(0);
        } else {
            return null;
        }
    }
}
