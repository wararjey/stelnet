package stelnet.ui;

import java.util.List;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

/**
 * Renders elements in a vertical line.
 *
 * Has fixed size, will display a scroll if elements exceed it.
 */
public class ScrollableStack extends Group {

    private Size size;

    public ScrollableStack(Size size, List<Renderable> elements) {
        super(elements);
        this.size = size;
    }

    @Override
    public void render(CustomPanelAPI panel, float x, float y) {
        TooltipMakerAPI inner = panel.createUIElement(size.getWidth(), size.getHeigth(), true);
        render(inner);
        panel.addUIElement(inner).inTL(x, y);
    }
}
