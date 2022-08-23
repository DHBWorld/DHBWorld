package dhbw.timetable.rapla.network;

/**
 * Created by Hendrik Ulbrich (C) 2017
 */
public enum BaseURL {

    STUTTGART("stuttgart"), KARLSRUHE("karlsruhe"), MANNHEIM("mannheim");

    private final String text;

    /**
     * @param text stuttgart, karlsruhe or mannheim
     */
    private BaseURL(final String text) {
        this.text = text;
    }

    public static String getRegex() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (BaseURL burl : BaseURL.values()) {
            sb.append(burl).append("|");
        }
        sb.append(")");
        return sb.toString();
    }

    public String complete() {
        return "https://rapla.dhbw-" + text + ".de/rapla";
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
