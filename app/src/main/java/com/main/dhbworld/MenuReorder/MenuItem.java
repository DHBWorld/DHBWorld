package com.main.dhbworld.MenuReorder;

public class MenuItem {
    private String title;
    private final String resourceId;
    private boolean hidden = false;

    public MenuItem(String title, String resourceId) {
        this.title = title;
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getResourceId() {
        return resourceId;
    }
}
