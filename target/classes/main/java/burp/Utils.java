package burp;
import javax.swing.*;
import java.awt.*;

public class Utils {

    public static void blinkTab(final ITab iTab){
        JTabbedPane tp = (JTabbedPane) iTab.getUiComponent().getParent();
        int tabidx = getTabIndex(iTab);
        tp.setBackgroundAt(tabidx, BurpExtender.BURP_ORANGE);

        // unblink tab in 4 seconds
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                try {
                    Thread.sleep(4000);

                    unblinkTab(iTab);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }});
        t1.start();
    }

    public static void unblinkTab(ITab iTab){
        JTabbedPane tp = (JTabbedPane) iTab.getUiComponent().getParent();
        int tabidx = getTabIndex(iTab);
        tp.setBackgroundAt(tabidx, Color.BLACK);
    }

    private static int getTabIndex(ITab iTab) {
        JTabbedPane mom = (JTabbedPane) iTab.getUiComponent().getParent();;
        for(int i = 0; i < mom.getTabCount(); ++i) {
            if(iTab.getTabCaption().equals(mom.getTitleAt(i))) {
                return i;
            }
        }
        return -1;
    }

}
