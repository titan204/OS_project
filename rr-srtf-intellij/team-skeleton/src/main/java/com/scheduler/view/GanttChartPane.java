package com.scheduler.view;

import java.util.List;

import com.scheduler.model.GanttEntry;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GanttChartPane extends Pane {

    private static final double BLOCK_H      = 38;
    private static final double TOP_PAD      = 8;
    private static final double TICK_H       = 14;
    private static final double MIN_W_UNIT   = 30;
    // Extra right-side padding so the last time label is never clipped
    private static final double RIGHT_PAD    = 30;

    private static final Color[] PALETTE = {
        Color.web("#4A90D9"), Color.web("#5CB85C"), Color.web("#E05C5C"),
        Color.web("#F0A030"), Color.web("#9B72CF"), Color.web("#3BBFBF"),
        Color.web("#D45F8A"), Color.web("#7BAF5E"), Color.web("#B07840"),
        Color.web("#507FA4")
    };

    private final Canvas canvas;

    public GanttChartPane() {
        canvas = new Canvas(600, BLOCK_H + TOP_PAD + TICK_H + 4);
        getChildren().add(canvas);
    }

    public void draw(List<GanttEntry> entries) {
        if (entries == null || entries.isEmpty()) return;

        int    totalTime = entries.get(entries.size() - 1).getEnd();
        double wpu       = Math.max(MIN_W_UNIT, 580.0 / Math.max(totalTime, 1));
        // Include RIGHT_PAD so the final tick label is never cut off
        double totalW    = wpu * totalTime + RIGHT_PAD;

        canvas.setWidth(totalW);
        canvas.setHeight(BLOCK_H + TOP_PAD + TICK_H + 6);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (GanttEntry e : entries) {
            double x = e.getStart() * wpu;
            double w = (e.getEnd() - e.getStart()) * wpu;
            Color  c = colorFor(e.getProcessId());

            // Block fill
            gc.setFill(c);
            gc.fillRoundRect(x + 1, TOP_PAD, w - 2, BLOCK_H, 6, 6);

            // Block border
            gc.setStroke(c.darker());
            gc.setLineWidth(1);
            gc.strokeRoundRect(x + 1, TOP_PAD, w - 2, BLOCK_H, 6, 6);

            // Label
            if (w > 16) {
                String lbl = "IDLE".equals(e.getProcessId()) ? "—" : e.getProcessId();
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                double tx = x + w / 2.0 - lbl.length() * 3.8;
                gc.fillText(lbl, tx, TOP_PAD + BLOCK_H / 2.0 + 4);
            }

            // Start tick
            gc.setFill(Color.web("#444"));
            gc.setFont(Font.font("Arial", 9));
            gc.fillText(String.valueOf(e.getStart()), x + 1, TOP_PAD + BLOCK_H + TICK_H);
        }

        // Final end-tick
        if (!entries.isEmpty()) {
            GanttEntry last = entries.get(entries.size() - 1);
            gc.setFill(Color.web("#444"));
            gc.setFont(Font.font("Arial", 9));
            gc.fillText(String.valueOf(last.getEnd()),
                    last.getEnd() * wpu + 1, TOP_PAD + BLOCK_H + TICK_H);
        }
    }

    private Color colorFor(String pid) {
        if ("IDLE".equals(pid)) return Color.web("#CCCCCC");
        return PALETTE[Math.abs(pid.hashCode()) % PALETTE.length];
    }

    public Canvas getCanvas() { return canvas; }
}
