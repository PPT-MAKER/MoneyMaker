package com.example.hwt.testapp.spider.service;

import com.example.hwt.testapp.ListUtil;
import com.example.hwt.testapp.SPUtil;
import com.example.hwt.testapp.spider.beans.AlbumBean;
import com.example.hwt.testapp.spider.beans.PhotoBean;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SpiderService {
    private static final String BASE_URL = "http://sj.zol.com.cn";
    private static final String ALBUM_URL = BASE_URL + "/bizhi";

    /**
     * @param onAlbumGet 回调
     */
    public static void getAlbum(final OnAlbumGet onAlbumGet) {
        List<AlbumBean> caches = (List<AlbumBean>) SPUtil.getInstance().getObject(ALBUM_URL, new TypeToken<List<AlbumBean>>() {
        }.getType());
        if (!ListUtil.isEmpty(caches) && onAlbumGet != null) {
            onAlbumGet.onAlbumGet(caches);
            return;
        }

        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                final List<AlbumBean> ret = new ArrayList<>();
                Document document = request(ALBUM_URL);
                if (document != null) {
                    Elements selects = document.select("dd.brand-sel-box").get(1).select("a");
                    if (selects != null) {
                        Iterator<Element> iterator = selects.iterator();
                        while (iterator.hasNext()) {
                            Element next = iterator.next();
                            AlbumBean albumBean = new AlbumBean();
                            albumBean.setName(next.text());
                            Document albumDetail = request(BASE_URL + next.attr("href"));
                            if (albumDetail != null) {
                                albumBean.setCoverUrl(getCover(albumDetail));
                                albumBean.setAlbumDetailHref(getAlbumDetailHref(albumDetail));
                            }
                            ret.add(albumBean);
                        }
                    }
                }
                if (onAlbumGet != null) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            onAlbumGet.onAlbumGet(ret);
                        }
                    });
                }

                SPUtil.getInstance().cacheObject(ALBUM_URL, ret);
            }
        });
    }

    private static List<String> getAlbumDetailHref(Document albumDetail) {
        List<String> ret = new ArrayList<>();
        List<String> href = albumDetail.select("body > div.wrapper.top-main.clearfix > div.main > ul a").eachAttr("href");
        if (href != null) {
            for (String s : href) {
                ret.add(BASE_URL + s);
            }
        }
        return ret;
    }

    private static String getCover(Document albumDetail) {
        return albumDetail.select("body > div.wrapper.top-main.clearfix > div.main > ul:nth-child(3) > li:nth-child(1) > a > img").attr("src");
    }

    /**
     * @param href       子相册的url AlbumBean.albumDetailHref
     * @param onPhotoGet 回调
     */
    public static void getPhoto(final String href, final OnPhotoGet onPhotoGet) {
        List<PhotoBean> caches = (List<PhotoBean>) SPUtil.getInstance().getObject(href, new TypeToken<List<PhotoBean>>() {
        }.getType());
        if (!ListUtil.isEmpty(caches) && onPhotoGet != null) {
            onPhotoGet.onPhotoGet(caches);
            return;
        }

        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                final List<PhotoBean> ret = new ArrayList<>();
                Document request = request(href);
                String attr = request.select("body > div:nth-child(4) > dl > dd > a:nth-child(1)").attr("href");
                List<String> eachAttr = request.select("#showImg > li > a").eachAttr("href");

                for (String s : eachAttr) {
                    String substring = s.substring(s.lastIndexOf("_") + 1, s.lastIndexOf("."));
                    String photoDetail = BASE_URL + attr.replaceAll("_[0-9]*_", "_" + substring + "_");
                    Document photoDetailDoc = request(photoDetail);
                    if (photoDetailDoc != null) {
                        String src = photoDetailDoc.select("body > img:nth-child(1)").attr("src");
                        PhotoBean photoBean = new PhotoBean();
                        photoBean.setUrl(src);
                        ret.add(photoBean);
                    }
                }

                if (onPhotoGet != null) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            onPhotoGet.onPhotoGet(ret);
                        }
                    });
                }
                SPUtil.getInstance().cacheObject(href, ret);
            }
        });
    }

    private static Document request(String url) {
        try {
            return Jsoup.connect(url).data("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                    .data("Accept-Encoding", "gzip, deflate").data("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .data("Connection", "keep-alive")
                    .data("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnAlbumGet {
        void onAlbumGet(List<AlbumBean> data);
    }

    public interface OnPhotoGet {
        void onPhotoGet(List<PhotoBean> data);
    }

}
