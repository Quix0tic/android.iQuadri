package com.bortolan.iquadriv2.Interfaces.Libri;

public class AnnouncementResponse {
    private boolean error;
    private String message;
    private Announcement announcement;

    public AnnouncementResponse(boolean error, String message, Announcement announcement) {
        this.error = error;
        this.message = message;
        this.announcement = announcement;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }
}
