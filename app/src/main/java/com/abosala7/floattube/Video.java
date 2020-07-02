package com.abosala7.floattube;

public class Video {

    private String title;
    private String id;
    private String imageLink;
    private String channelTitle;

    public Video(String title, String id, String imageLink, String channelTitle) {
        this.title = title;
        this.id = id;
        this.imageLink = imageLink;
        this.channelTitle = channelTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    @Override
    public String toString() {
        return "Video{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", channelTitle='" + channelTitle + '\'' +
                '}';
    }
    /**/
}
