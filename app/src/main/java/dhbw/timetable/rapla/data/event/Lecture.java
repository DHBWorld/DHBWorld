package dhbw.timetable.rapla.data.event;

public abstract class Lecture {

    protected String title, persons, resources;

    public Lecture(String title, String persons, String resources) {
        this.title = title;
        this.persons = persons;
        this.resources = resources;
    }

    public String getTitle() {
        return title;
    }

    public String getPersons() {
        return persons;
    }

    public String getResources() {
        return resources;
    }

    public String getInfo() {
        return persons + " " + resources;
    }
}
