package com.telegroup.nezavisnetvapp.recommendations;


import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.telegroup.nezavisnetvapp.AppSingleton;
import com.telegroup.nezavisnetvapp.ArticleDetailsFragment;
import com.telegroup.nezavisnetvapp.model.NewsCard;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class RecommendationEngine {

    private class CategoryPercentagePair{

        public CategoryPercentagePair(String category, double percentage){
            this.category = category;
            this.percentage = percentage;
        }

        private String category;
        private double percentage;

        public String getCategory(){
            return category;
        }

        public double getPercentage(){
            return percentage;
        }

        @Override
        public String toString(){
            return category + " : " + percentage;
        }
    }

    ArrayList<NewsCard> toReturn;
    private Context context;

    public RecommendationEngine(Context context){
        this.context = context;
    }

    public HashMap<String, Integer> getAmountsToGet(){
        HashMap<String, String> categoryLog = ArticleDetailsFragment.userLogs.getCategoryLog();
        ArrayList<CategoryPercentagePair> categoryPercentagePairs = new ArrayList<>();
        HashMap<String, Integer> numOfRecommendations = new HashMap<>();
        int totalOpenedNews = 0;
        for(String category : categoryLog.keySet()){
            totalOpenedNews += Integer.parseInt(categoryLog.get(category));
        }
        for(String category : categoryLog.keySet()){
            int openedNewsForCategory = Integer.parseInt(categoryLog.get(category));
            CategoryPercentagePair pair = new CategoryPercentagePair(category, (double) openedNewsForCategory / (double) totalOpenedNews);
            categoryPercentagePairs.add(pair);
            System.out.println("!!!!!!" + pair);
        }
        Collections.sort(categoryPercentagePairs, new Comparator<CategoryPercentagePair>() {
            @Override
            public int compare(CategoryPercentagePair categoryPercentagePair, CategoryPercentagePair t1) {
                return Double.compare(categoryPercentagePair.getPercentage(), t1.getPercentage()) * (-1);
            }
        });
        int unused = 3;
        int counter = 0;
        while(unused > 0){
            double currentPercentage = categoryPercentagePairs.get(counter).getPercentage();
            if(currentPercentage > 0.66){
                numOfRecommendations.put(categoryPercentagePairs.get(counter).getCategory(), 3);
                unused -= 3;
            }else if(currentPercentage > 0.33 && currentPercentage <= 0.66){
                numOfRecommendations.put(categoryPercentagePairs.get(counter).getCategory(), 2);
                unused -= 2;
            }else{
                numOfRecommendations.put(categoryPercentagePairs.get(counter).getCategory(), 2);
                unused -= 1;
            }
            counter++;
        }
        return numOfRecommendations;
    }

}
