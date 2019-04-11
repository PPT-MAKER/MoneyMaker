package com.example.hwt.testapp.Behavior;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hwt.testapp.GApplication;

import java.util.HashSet;
import java.util.Set;

public class CollectionHelper {
    private static final String COLLECTION_SP = "collectionSp";

    private static Set<String> collectItems = new HashSet<>();

    private static SharedPreferences sp;

    private static final String COLLECTION_KEY = "collectionKey";

    static {
        sp = GApplication.getContext().getSharedPreferences(COLLECTION_SP, Context.MODE_PRIVATE);
        collectItems.addAll(sp.getStringSet(COLLECTION_KEY, new HashSet<String>()));
    }

    public static boolean isCollected(String imgUrl) {
        return collectItems.contains(imgUrl);
    }

    public static void collect(String imgUrl, boolean collect) {
        SharedPreferences.Editor editor = sp.edit();
        if (collect) {
            collectItems.add(imgUrl);
        } else {
            collectItems.remove(imgUrl);
        }
        editor.putStringSet(COLLECTION_KEY, collectItems);
        editor.apply();
    }

    public static Set<String> getCollectItems() {
        return collectItems;
    }
}
