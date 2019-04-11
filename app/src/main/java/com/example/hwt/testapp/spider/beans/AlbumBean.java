package com.example.hwt.testapp.spider.beans;

import java.io.Serializable;
import java.util.List;

/**
 * 相册
 */
public class AlbumBean implements Serializable {

    private String name;
    private String coverUrl;

    // 相册里面的子相册链接
    private List<AlbumSecondBean> secondBeans;

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

    public List<AlbumSecondBean> getSecondBeans() {
        return secondBeans;
    }

    public void setSecondBeans(List<AlbumSecondBean> secondBeans) {
        this.secondBeans = secondBeans;
    }

    @Override
    public String toString() {
        return "AlbumBean{" +
                "name='" + name + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", secondBeans=" + secondBeans +
                '}';
    }

    public static class AlbumSecondBean {
        private String name;
        private String coverUrl;
        private String albumHref;

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

        public String getAlbumHref() {
            return albumHref;
        }

        public void setAlbumHref(String albumHref) {
            this.albumHref = albumHref;
        }

        @Override
        public String toString() {
            return "AlbumSecondBean{" +
                    "name='" + name + '\'' +
                    ", coverUrl='" + coverUrl + '\'' +
                    ", albumHref='" + albumHref + '\'' +
                    '}';
        }
    }
}
