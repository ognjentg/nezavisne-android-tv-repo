package com.telegroup.nezavisnetvapp.recommendations;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.telegroup.nezavisnetvapp.R;
import com.telegroup.nezavisnetvapp.model.NewsCard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class UserLogs {

    private static final String LOG_FILE_NAME = "nezavisneUserLogs.dat";
    private Context context;
    private File logFile = null;

    @SerializedName("caregoryLog")
    @Expose
    private HashMap<String, String> categoryLog;

    @SerializedName("newsLog")
    @Expose
    private HashMap<String, String> newsLog;

    public UserLogs(Context context){
        this.context = context;
        categoryLog = new HashMap<>();
        newsLog = new HashMap<>();
        logFile = new File(this.context.getFilesDir(), LOG_FILE_NAME);
        if(!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e("LOG_ERROR", "Could not create log file");
                logFile = null;
            }
        }else{
            try{
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                StringBuilder stringBuilder = new StringBuilder();
                String readLine;
                while((readLine = reader.readLine()) != null){
                    stringBuilder.append(readLine);
                }
                reader.close();
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                UserLogs temp = gson.fromJson(stringBuilder.toString(), UserLogs.class);
                if(temp != null){
                    this.newsLog = temp.newsLog;
                    this.categoryLog = temp.categoryLog;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String, String> getCategoryLog(){
        return categoryLog;
    }

    public HashMap<String, String> getNewsLog(){
        return newsLog;
    }

    public void userOpenedNews(NewsCard newsCard, String categoryId){
        String numOfOpensString = categoryLog.get(categoryId);
        if(numOfOpensString != null){
            Integer numOfOpens = Integer.parseInt(numOfOpensString);
            numOfOpens++;
            categoryLog.put(categoryId, Integer.toString(numOfOpens));
        }else{
            categoryLog.put(categoryId, "1");
        }
        newsLog.put(Long.toString(new Date().getTime()), newsCard.getNewsId());
        wipeOlderLogs(context.getResources().getInteger(R.integer.number_of_days_to_keep_log));
        try {
            updateLogFile();
        } catch (IOException e) {
            Log.e("ERROR", "Error updating log file");
        }
    }

    private void wipeOlderLogs(int numOfDaysToWipe){
        Set<String> dates = newsLog.keySet();
        for(String dateString : dates){
            if(Long.parseLong(dateString) + numOfDaysToWipe * 24 * 60 * 60 * 1000 < new Date().getTime()){
                newsLog.remove(dateString);
            }
        }
    }

    public String getJsonString(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    private void updateLogFile() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(logFile));
        String json = getJsonString();
        writer.write(json);
        writer.close();
    }

}
