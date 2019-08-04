package com.paulobsa.desafioandroid;

import com.paulobsa.desafioandroid.model.Item;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RepoListViewModel extends ViewModel {
    private MutableLiveData<List<Item>> items;

    public LiveData<List<Item>> getItems() {
        if (items == null) {
            items = new MutableLiveData<List<Item>>();
            loadRepoList();
        }
        return items;
    }

    private void loadRepoList() {
        // Do an asynchronous operation to fetch users.
    }
}
