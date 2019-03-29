package com.binus.ind.cloud_computing_binus.domain;

public class Paragraf {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"text\":");
        sb.append("\""+getText()+"\"");
        sb.append("}");

        return sb.toString();
    }
}
