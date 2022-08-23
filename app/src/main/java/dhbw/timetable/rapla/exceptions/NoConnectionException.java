package dhbw.timetable.rapla.exceptions;

/**
 * Created by Hendrik Ulbrich (C) 2017
 */
public class NoConnectionException extends Exception {

    private String url;

    public NoConnectionException(String url) {
        this.url = url;
    }

    @Override
    public String getMessage() {
        return "No internet connection or rapla server is down. Check URL: " + url;
    }

}
