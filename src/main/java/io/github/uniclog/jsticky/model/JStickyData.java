package io.github.uniclog.jsticky.model;

import lombok.Data;

@Data
public class JStickyData {
    private JStickySettings settings = new JStickySettings();
    private JStickyWindowSettings windowSettings = new JStickyWindowSettings();
    private String content;
    private transient String contentMirror;
}