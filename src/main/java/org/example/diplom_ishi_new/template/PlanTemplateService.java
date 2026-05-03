package org.example.diplom_ishi_new.template;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service
public class PlanTemplateService {

    private static final Set<String> KEYS = Set.of("teaching", "methodical", "research", "mentorship");
    private final Path storageDirectory = Path.of("plan-templates");

    public boolean isValidKey(String key) {
        return KEYS.contains(key);
    }

    public PlanTemplateView getTemplate(String key) {
        Path file = findTemplateFile(key);
        if (file == null) {
            return new PlanTemplateView(key, null, null);
        }
        return new PlanTemplateView(key, originalFileName(file), renderFile(file));
    }

    public void saveTemplate(String key, MultipartFile file) {
        if (!isValidKey(key) || file == null || file.isEmpty()) {
            return;
        }

        try {
            Files.createDirectories(storageDirectory);
            deleteCurrentTemplate(key);
            String original = cleanFileName(file.getOriginalFilename());
            Path target = storageDirectory.resolve(key + "__" + original);
            file.transferTo(target);
        } catch (IOException ex) {
            throw new IllegalStateException("Shablon faylini saqlab bo'lmadi", ex);
        }
    }

    public void resetTemplate(String key) {
        if (!isValidKey(key)) {
            return;
        }
        try {
            deleteCurrentTemplate(key);
        } catch (IOException ex) {
            throw new IllegalStateException("Shablonni default holatga qaytarib bo'lmadi", ex);
        }
    }

    public Path getTemplatePath(String key) {
        return findTemplateFile(key);
    }

    private Path findTemplateFile(String key) {
        if (!isValidKey(key) || !Files.isDirectory(storageDirectory)) {
            return null;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storageDirectory, key + "__*")) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    return path;
                }
            }
        } catch (IOException ignored) {
            return null;
        }
        return null;
    }

    private void deleteCurrentTemplate(String key) throws IOException {
        Path current = findTemplateFile(key);
        if (current != null) {
            Files.deleteIfExists(current);
        }
    }

    private String renderFile(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        try {
            if (name.endsWith(".html") || name.endsWith(".htm")) {
                return Files.readString(file, StandardCharsets.UTF_8);
            }
            if (name.endsWith(".csv")) {
                return csvToTable(Files.readString(file, StandardCharsets.UTF_8));
            }
            if (name.endsWith(".txt")) {
                return "<pre class=\"template-pre\">" + escapeHtml(Files.readString(file, StandardCharsets.UTF_8)) + "</pre>";
            }
            if (name.endsWith(".docx")) {
                return docxTablesToHtml(file);
            }
        } catch (IOException ignored) {
            return null;
        }
        return null;
    }

    private String docxTablesToHtml(Path file) {
        try (ZipFile zipFile = new ZipFile(file.toFile())) {
            ZipEntry documentXml = zipFile.getEntry("word/document.xml");
            if (documentXml == null) {
                return null;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().parse(zipFile.getInputStream(documentXml));
            NodeList tables = document.getElementsByTagNameNS("*", "tbl");
            if (tables.getLength() == 0) {
                return null;
            }

            StringBuilder html = new StringBuilder("<div class=\"table-wrap\"><table class=\"report-view-table template-custom-table\"><tbody>");
            for (int tableIndex = 0; tableIndex < tables.getLength(); tableIndex++) {
                Element table = (Element) tables.item(tableIndex);
                appendDocxTableRows(html, table);
                if (tableIndex < tables.getLength() - 1) {
                    html.append("<tr class=\"template-table-gap\"><td colspan=\"20\"></td></tr>");
                }
            }
            html.append("</tbody></table></div>");
            return html.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    private void appendDocxTableRows(StringBuilder html, Element table) {
        for (Element row : directChildren(table, "tr")) {
            html.append("<tr>");
            for (Element cell : directChildren(row, "tc")) {
                html.append("<td>").append(escapeHtml(cellText(cell))).append("</td>");
            }
            html.append("</tr>");
        }
    }

    private java.util.List<Element> directChildren(Element parent, String localName) {
        java.util.List<Element> children = new java.util.ArrayList<>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element element && localName.equals(element.getLocalName())) {
                children.add(element);
            }
        }
        return children;
    }

    private String cellText(Element cell) {
        StringBuilder text = new StringBuilder();
        NodeList texts = cell.getElementsByTagNameNS("*", "t");
        for (int i = 0; i < texts.getLength(); i++) {
            if (!text.isEmpty()) {
                text.append(' ');
            }
            text.append(texts.item(i).getTextContent());
        }
        return text.toString().trim();
    }

    private String csvToTable(String content) {
        StringBuilder html = new StringBuilder("<div class=\"table-wrap\"><table class=\"report-view-table template-custom-table\"><tbody>");
        String[] rows = content.split("\\R", -1);
        for (String row : rows) {
            if (row.isBlank()) {
                continue;
            }
            html.append("<tr>");
            String[] cells = row.split(",", -1);
            for (String cell : cells) {
                html.append("<td>").append(escapeHtml(cell.trim())).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</tbody></table></div>");
        return html.toString();
    }

    private String originalFileName(Path file) {
        String savedName = file.getFileName().toString();
        int marker = savedName.indexOf("__");
        if (marker < 0 || marker + 2 >= savedName.length()) {
            return savedName;
        }
        return savedName.substring(marker + 2);
    }

    private String cleanFileName(String fileName) {
        String name = fileName == null || fileName.isBlank() ? "shablon.html" : fileName.trim();
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
