package com.telegroup.nezavisnetvapp.recommendations;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.telegroup.nezavisnetvapp.Article;
import com.telegroup.nezavisnetvapp.R;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class UserLogs {

    private static final String LOG_FILE_NAME = "nezavisneUserLogs.dat";
    private Context context;
    private File logFile = null;
    private HashMap<String, Integer> categoryLog;
    private HashMap<Date, Long> newsLog;

    public UserLogs(Context context){
        this.context = context;
        logFile = new File(this.context.getFilesDir(), LOG_FILE_NAME);
        if(!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e("LOG_ERROR", "Could not create log file");
                logFile = null;
            }
        }else{
            //TODO: Load values from file
        }
    }

    public void userOpenedNews(Article article){
        Integer numOfOpens = categoryLog.get(article.getCategoryId());
        if(numOfOpens != null){
            numOfOpens++;
            categoryLog.put(article.getCategoryId(), numOfOpens);
        }else{
            categoryLog.put(article.getCategoryId(), 1);
        }
        newsLog.put(new Date(), Long.valueOf(article.getId()));
        wipeOlderLogs(context.getResources().getInteger(R.integer.number_of_days_to_keep_log));
        updateLogFile();
    }

    private void wipeOlderLogs(int numOfDaysToWipe){
        Set<Date> dates = newsLog.keySet();
        for(Date date : dates){
            if(date.getTime() + numOfDaysToWipe * 24 * 60 * 60 * 1000 < new Date().getTime()){
                newsLog.remove(date);
            }
        }
    }

    private JsonObject writeToJsonObject(){
        //TODO: Do this
        return null;
    }

    private void updateLogFile(){
        //TODO: Do this
    }

}
