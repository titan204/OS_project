package com.scheduler.view;

import com.scheduler.model.GanttEntry;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class GanttChartPane extends Pane {

    private static final double BLOCK_H      = 38;
    private static final double TOP_PAD      = 8;
    private static final double TICK_H       = 14;
    private static final double MIN_W_UNIT   = 30;

    private static final Color[] PALETTE = {
        Color.web("#4A90D9"), Color.web("#5CB85C"), Color.web("#E05C5C"),
        Color.web("#F0A030"), Color.web("#9B72CF"), Color.web("#3BBFBF"),
        Color.web("#D45F8A"), Color.web("#7BAF5E"), Color.web("#B07840"),
        Color.web("#507FA4")
    };

    private final Canvas canvas;

    public GanttChartPane() {
        // TODO: Implementation will be completed by team member 🧩
    }

    public void draw(List<GanttEntry> entries) {
        // TODO: Implementation will be completed by team member 🧩
    }

    private Color colorFor(String pid) {
        // TODO: Implementation will be completed by team member 🧩
        return null;
    }

    public Canvas getCanvas() { return canvas; }
}
