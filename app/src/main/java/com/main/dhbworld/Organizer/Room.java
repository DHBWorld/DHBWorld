package com.main.dhbworld.Organizer;

public class Room {
    String name;
    String roomType;
    String roomNo;
    String url;

    public Room() { }
    public String filterString(){return name + roomType + roomNo + url;}

    public void setName(String name) {
        this.name = name;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getUrl() {
        return url;
    }
}
