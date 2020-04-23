package burp;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by fruh on 9/7/16.
 */
public class Extraction {
    private String startString;
    private String stopString;
    private String msgId;
    private String id;
    private Set<String> repRefSet;

    public Extraction(String startString, String stopString) {
        this.startString = startString;
        this.stopString = stopString;
        repRefSet = new HashSet<>();
    }

    public String extractData(String response) {
        return Extraction.extractData(response, startString, stopString);
    }

    public static String extractData(String response, String startString, String stopString) {
        String ret = "EXTRACTION_ERROR";
        int index_of_start = response.indexOf(startString);

        if (index_of_start >= 0) {
            String tmp_part = response.substring(index_of_start + startString.length());

            int index_of_stop = tmp_part.indexOf(stopString);

            if (index_of_stop >= 0) {
                ret = tmp_part.substring(0, index_of_stop);
            }
        }
        return ret;
    }

    public String getStartString() {
        return startString;
    }

    public String getStopString() {
        return stopString;
    }

    public Set<String> getRepRefSet() {
        return repRefSet;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "'" + id + "', '" + startString + "', '" + stopString + "', '" + msgId + "'";
    }
}
