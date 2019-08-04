package com.paulobsa.desafioandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.paulobsa.desafioandroid.model.Item;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class RepoListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RepoListAdapter.RepoListAdapterOnclickHandler {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private RepoListAdapter mRepoListAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private RepoListViewModel viewModel;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_list);

        mRecyclerView = findViewById(R.id.repo_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRepoListAdapter = new RepoListAdapter(this, this);
        mRecyclerView.setAdapter(mRepoListAdapter);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        progressBar = findViewById(R.id.progressBarRepoList);

        viewModel = ViewModelProviders.of(this).get(RepoListViewModel.class);

        viewModel.getItems().observe(this, items -> {
            // update UI
            setRepoList(items);
        });

        viewModel.getProgressBarState().observe(this, progressBarState -> {
            if(progressBarState) {
                enableProgressBar();
            } else {
                disableProgressBar();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            this.isLoading = isLoading;
        });

        viewModel.getAddLoading().observe(this, addLoading -> {
            if (addLoading) {
                mRepoListAdapter.addLoading();
            } else {
                removeLoading();
            }
        });

        viewModel.getIsLastPage().observe(this, isLastPage -> {
            this.isLastPage = isLastPage;
        });

        viewModel.getShowErrorMessage().observe(this, showErrorMessage -> {
            if(showErrorMessage) showErrorMessage();
        });

        viewModel.init(this);

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                viewModel.fetchData();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


    }

    private void setRepoList(List<Item> items) {
        mRepoListAdapter.addAll(items);
    }

    @Override
    public void onCardClick(String repoJson) {
        Toast.makeText(this, "Clicou!", Toast.LENGTH_LONG).show();
    }

    private void showErrorMessage() {
        Toast.makeText(this, "Problema de conexão!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
        mRepoListAdapter.clear();
        viewModel.init(this);
    }

    private void disableProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void enableProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void removeLoading() {
        mRepoListAdapter.removeLoading();
    }

}
