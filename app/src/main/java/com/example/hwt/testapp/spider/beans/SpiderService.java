package com.example.hwt.testapp.spider.beans;
import java.util.ArrayList;
import java.util.List;

public class SpiderService {

    private static final String BASE_URL = "http://sj.zol.com.cn";

    public static void getAlbum(OnAlbumGet onAlbumGet){
        List<AlbumBean> ret = new ArrayList<>();
        AlbumBean albumBean1 = new AlbumBean();
        albumBean1.setName("风景");
        albumBean1.setCoverUrl("https://sjbz-fd.zol-img.com.cn/t_s208x312c5/g5/M00/01/06/ChMkJ1w3FnmIE9dUAADdYQl3C5IAAuTxAKv7x8AAN15869.jpg");
        albumBean1.setTotalCount(10);
        albumBean1.setHerf(BASE_URL+"/bizhi/detail_8715.html");

        AlbumBean albumBean2 = new AlbumBean();
        albumBean2.setName("动漫");
        albumBean2.setCoverUrl("https://sjbz-fd.zol-img.com.cn/t_s208x312c5/g2/M00/0C/03/ChMlWlyB7r6IcJB9AALJbRFkrbsAAIq3QGm_5wAAsmF192.jpg");
        albumBean2.setTotalCount(10);
        albumBean2.setHerf(BASE_URL+"/bizhi/detail_8732_97162.html");

        ret.add(albumBean1);
        ret.add(albumBean2);
        if(onAlbumGet != null){
            onAlbumGet.onAlbumGet(ret);
        }
    }

    public static void getAlbum(String href,OnPhotoGet onPhotoGet){
        List<PhotoBean> ret = new ArrayList<>();
        PhotoBean photoBean1 = new PhotoBean();
        photoBean1.setUrl("https://sjbz-fd.zol-img.com.cn/t_s320x510c/g5/M00/01/06/ChMkJ1w3FnmIE9dUAADdYQl3C5IAAuTxAKv7x8AAN15869.jpg");

        PhotoBean photoBean2 = new PhotoBean();
        photoBean2.setUrl("https://sjbz-fd.zol-img.com.cn/t_s320x510c/g5/M00/01/06/ChMkJ1w3FniIAR5_AAGySQqUEwoAAuTxAKsWroAAbJh319.jpg");
        ret.add(photoBean1);
        ret.add(photoBean2);
        if(onPhotoGet != null){
            onPhotoGet.OnPhotoGet(ret);
        }
    }

    interface OnAlbumGet{
         void onAlbumGet(List<AlbumBean> data);
    }

    interface OnPhotoGet{
        void OnPhotoGet(List<PhotoBean> data);
    }

}
