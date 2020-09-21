package com.iyx.codeless.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathRecorder {

    private static PathRecorder instance;

    public static final String KEY_NAME = "name";
    public static final String KEY_POSITION = "position";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CONTAINER_NAME = "container_name";

    private List<Map<String,Object>> pathRecord;

    private PathRecorder(){
        pathRecord = new ArrayList<>();

    }

    public static PathRecorder getInstance(){
        if (instance == null){
            synchronized (PathRecorder.class){
                if (instance == null){
                    instance = new PathRecorder();
                }
            }
        }
        return instance;
    }


    public void collect(Map<String,Object> map){
        if (pathRecord != null){
            if (!pathRecord.contains(map)){
                pathRecord.add(map);
            }
        }
    }

    public List<Map<String,Object>> getPathRecord(){
        return pathRecord;
    }

    public String getPath(){
        StringBuilder sb = new StringBuilder();
        for (Map<String,Object> map:pathRecord){
            sb.append(map.get(KEY_CONTAINER_NAME)).append("_").append(map.get(KEY_POSITION)).append("_");
        }
        if (pathRecord.size()>=1){
            sb.append(pathRecord.get(pathRecord.size()-1).get(KEY_NAME));
        }
        return sb.toString();
    }

    public void clear(){
        pathRecord.clear();
//        instance = null;
    }

}
