package com.sirap.extractor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Album;

public class FishCache {
	private static Cache<String, Album> albums;
	
    static {
        albums = CacheBuilder.newBuilder().maximumSize(10000)
                .expireAfterAccess(240, TimeUnit.HOURS)
                .initialCapacity(10)
                .build();
    }
    
    public static long size(){
    	return albums.size();
    }
    
    public  static Album get(String key){
    	XXXUtil.shouldBeNotnull(key);
    	return albums.getIfPresent(key);
    }
    
    public static void put(String key, Album value){
    	XXXUtil.shouldBeNotnull(key);
    	albums.put(key, value);
    }
    
    public static void remove(String key){
    	XXXUtil.shouldBeNotnull(key);
    	albums.invalidate(key);
    }
    
    public static void remove(List<String> keys){
    	XXXUtil.shouldBeNotnull(keys);
        if(keys !=null && keys.size() >0){
            albums.invalidateAll(keys);
        }
    }
}
