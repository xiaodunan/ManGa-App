package com.mobiletrain.manga.util;

import com.google.gson.Gson;
import com.mobiletrain.manga.model.CategoryBean;
import com.mobiletrain.manga.model.MangaBean;

/**
 * Created by qf on 2016/10/20.
 */
public class JsonUtil {
    public static CategoryBean getCategoryBean(String json){
        CategoryBean categoryBean=null;
        try {
            categoryBean=  new Gson().fromJson(json,CategoryBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryBean;
    }

    public static MangaBean getMangabean(String json){
        MangaBean mangaBean=null;
        try {
            mangaBean=new Gson().fromJson(json,MangaBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mangaBean;
    }
}
