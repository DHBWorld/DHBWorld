package com.main.dhbworld.Dualis.parser;

public enum DualisURL {

    DocumentsURL("CREATEDOCUMENT&"),
    OverallURL("STUDENT_RESULT&"),
    ClassURL("COURSERESULTS&"),
    SemesterURL("COURSERESULTS&");

    private static final String baseURL  = "https://dualis.dhbw.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=";
    private final String url;

    DualisURL(String url) {
        this.url = baseURL + url;
    }

    public String getUrl() {
        return url;
    }

    public static String refactorMainArguments(String arguments) {
        String mainArguments = arguments.replace("-N000000000000000", "");
        return mainArguments.replace("-N000019,", "-N000307");
    }
}
