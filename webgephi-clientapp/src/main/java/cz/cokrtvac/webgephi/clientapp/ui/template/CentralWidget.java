package cz.cokrtvac.webgephi.clientapp.ui.template;

import com.vaadin.server.Page;
import com.vaadin.ui.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 14:56
 */
public class CentralWidget extends CustomComponent implements Page.BrowserWindowResizeListener {
    private Component left;
    private Component center;
    private Component right;

    HorizontalSplitPanel split1;
    HorizontalSplitPanel split2;

    public CentralWidget() {
        left = new HorizontalLayout(new Label("LEFT"));
        left.setStyleName("left_panel");

        center = new HorizontalLayout(new Label("CENTER"));
        center.setStyleName("center_panel");

        right = new HorizontalLayout(new Label("RIGHT"));
        right.setStyleName("right_panel");

        split2 = new HorizontalSplitPanel();
        split1 = new HorizontalSplitPanel();

        setLeft(left);
        setCenter(center);
        setRight(right);

        split1.setSplitPosition(345, Unit.PIXELS);
        split1.setMinSplitPosition(50, Unit.PIXELS);

        split2.setMinSplitPosition(50, Unit.PIXELS);
        split2.setSplitPosition(230, Unit.PIXELS, true);

        split1.setSecondComponent(split2);

        setCompositionRoot(split1);
    }

    public Component getLeft() {
        return left;
    }

    public void setLeft(Component left) {
        this.left = left;
        split1.setFirstComponent(this.left);
        updateSize(Page.getCurrent().getBrowserWindowHeight());
    }

    public Component getCenter() {
        return center;
    }

    public void setCenter(Component center) {
        this.center = center;
        this.split2.setFirstComponent(this.center);
        updateSize(Page.getCurrent().getBrowserWindowHeight());
    }

    public Component getRight() {
        return right;
    }

    public void setRight(Component right) {
        this.right = right;
        this.split2.setSecondComponent(this.right);
        updateSize(Page.getCurrent().getBrowserWindowHeight());
    }

    @Override
    public void browserWindowResized(Page.BrowserWindowResizeEvent browserWindowResizeEvent) {
           updateSize(browserWindowResizeEvent.getHeight());
    }

    private void updateSize(int height){
        int h = height - 70;
        split1.setHeight(h, Unit.PIXELS);
        split2.setHeight(h, Unit.PIXELS);
        left.setHeight(h, Unit.PIXELS);
        //left.setWidth(100, Unit.PERCENTAGE);
        center.setHeight(h, Unit.PIXELS);
        //center.setWidth(100, Unit.PERCENTAGE);
        right.setHeight(h, Unit.PIXELS);
        //right.setWidth(100, Unit.PERCENTAGE);
    }
}
