package org.example.diplom_ishi_new.template;

public class PlanTemplateView {

    private final String key;
    private final String fileName;
    private final String contentHtml;

    public PlanTemplateView(String key, String fileName, String contentHtml) {
        this.key = key;
        this.fileName = fileName;
        this.contentHtml = contentHtml;
    }

    public String getKey() {
        return key;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public boolean isHasCustom() {
        return fileName != null && !fileName.isBlank();
    }

    public boolean isRenderable() {
        return contentHtml != null && !contentHtml.isBlank();
    }
}
