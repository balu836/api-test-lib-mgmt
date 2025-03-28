package uk.gov.dwp.utils.casehistory;

public class PlaceHolder {
    private String source;
    private String key;
    private String value;

    public PlaceHolder(String source, String key, String value) {
        this.source = source;
        this.key = key;
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + String.join(", ", this.source, this.key, this.value) + ")";
    }
}
