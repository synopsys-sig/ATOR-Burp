package burp;

/**
 * Created by fruh on 10/7/16.
 */
public class ExtStringCreator {
    private static final int STEP = 8;
    public static String[] getStartStopString(String selectedText, String wholeText, int[] bounds) {
    	String[] ret = new String[2];
        ret[0] = ret[1] = null;

        if (selectedText.equals("") || selectedText == null) {
            return null;
        }
        int startIndex = bounds[0];
        int stopIndex = bounds[1];
        int sI = moveIndex(startIndex, true, wholeText.length());
        int eI = moveIndex(stopIndex, false, wholeText.length());

        int tmpsI;
        int tmpeI;
        boolean changed = true;
        int i = 0;
        while (changed) {
        	ret[0] = wholeText.substring(sI, startIndex);
            ret[1] = wholeText.substring(stopIndex, eI);

            // we found what is selected
            if (selectedText.equals(Extraction.extractData(wholeText, ret[0], ret[1]))) {
                break;
            }
            else {
                ret[0] = ret[1] = null;
            }
            tmpsI = sI;
            sI = moveIndex(startIndex, true, wholeText.length());

            tmpeI = eI;
            eI = moveIndex(stopIndex, false, wholeText.length());

            // if something changing or not
            if (tmpeI == eI && tmpsI == sI) {
                changed = false;
            }
        }
        if (ret[0] == null) {
            return null;
        }
        
        return ret;
    }

    public static int moveIndex(int index, boolean isStart, int textLen) {
        int tmp = STEP + 1;

        if (isStart) {
            while (--tmp > 0) {
                if (index - tmp >= 0) {
                    index -= tmp;
                    break;
                }
            }
        }
        else {
            while (--tmp > 0) {
                if (index + tmp < textLen) {
                    index += tmp;
                    break;
                }
            }
        }
        return index;
    }
}
