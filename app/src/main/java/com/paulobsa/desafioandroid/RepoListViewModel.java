package com.paulobsa.desafioandroid;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paulobsa.desafioandroid.model.Item;
import com.paulobsa.desafioandroid.model.SearchResult;
import com.paulobsa.desafioandroid.util.Util;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RepoListViewModel extends ViewModel {
    private static final int PAGE_START = 1;
    private static final int TOTAL_PAGES = 34;

    private int currentPage = PAGE_START;
    private boolean isFirstAttempt = true;
    private RequestQueue queue;
    private Gson mGson;
    private SearchResult searchResult;
    private MutableLiveData<List<Item>> items;
    private MutableLiveData<Boolean> progressBarState;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Boolean> addLoading;
    private MutableLiveData<Boolean> isLastPage;
    private MutableLiveData<Boolean> showErrorMessage;

    public void init(Context context) {
        queue = Volley.newRequestQueue(context);
        if (isFirstAttempt) {
            fetchData();
        }
    }

    public LiveData<List<Item>> getItems() {
        if (items == null) {
            items = new MutableLiveData<>();
        }
        return items;
    }

    public LiveData<Boolean> getProgressBarState() {
        if (progressBarState == null) {
            progressBarState = new MutableLiveData<>();
        }
        return progressBarState;
    }

    public LiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
        }

        return isLoading;
    }

    public LiveData<Boolean> getAddLoading() {
        if (addLoading == null) {
            addLoading = new MutableLiveData<>();
        }

        return addLoading;
    }

    public LiveData<Boolean> getIsLastPage() {
        if (isLastPage == null) {
            isLastPage = new MutableLiveData<>();
        }

        return isLastPage;
    }


    public LiveData<Boolean> getShowErrorMessage() {
        if (showErrorMessage == null) {
            showErrorMessage = new MutableLiveData<>();
        }

        return showErrorMessage;
    }

    public void fetchData() {
        mGson = gsonBuilder();
        isLoading.postValue(true);
        if (currentPage == PAGE_START) {
            isLastPage.postValue(false);
            progressBarState.postValue(true);
            fetchRepoInfo(PAGE_START);
        } else {
            addLoading.postValue(true);
            fetchRepoInfo(currentPage);
        }

        if (currentPage >= TOTAL_PAGES) isLastPage.setValue(true);
    }

    private void fetchRepoInfo(Integer page) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Util.buildJavaSearch(page).toString(),
                onResponse, onResponseError);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private final Response.Listener<String> onResponse = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            removeLoading();
            searchResult = mGson.fromJson(response, SearchResult.class);
            isLoading.postValue(false);
            currentPage++;
            progressBarState.postValue(false);
            items.postValue(searchResult.getItems());
        }
    };

    private final Response.ErrorListener onResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.v(Util.LOG_TAG, error.toString());
            removeLoading();
            showErrorMessage.setValue(true);
            isLoading.postValue(false);
        }
    };

    private Gson gsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        return gsonBuilder.create();
    }

    private void removeLoading() {
        if (!isFirstAttempt) addLoading.postValue(false);
        isFirstAttempt = false;
    }
}
