package com.example.hwt.testapp.spider.beans;

import java.util.List;

/**
 * 相册
 */
public class AlbumBean {

    private String name;
    private String coverUrl;

    // 相册里面的子相册链接
    private List<String> albumDetailHref;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<String> getAlbumDetailHref() {
        return albumDetailHref;
    }

    public void setAlbumDetailHref(List<String> albumDetailHref) {
        this.albumDetailHref = albumDetailHref;
    }

    @Override
    public String toString() {
        return "AlbumBean{" +
                "name='" + name + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", albumDetailHref=" + albumDetailHref +
                '}';
    }
}
