package com.paulobsa.desafioandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulobsa.desafioandroid.model.Item;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RepoListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private RepoListAdapterOnclickHandler mHandler;
    private Context mContext;
    private List<Item> items;
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoaderVisible = false;

    public RepoListAdapter(RepoListAdapterOnclickHandler handler, Context context) {
        this.mHandler = handler;
        this.mContext = context;
        items = new ArrayList<>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_item, parent, false));
            case LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder baseViewHolder, final int position) {
        baseViewHolder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == items.size() - 1 ? LOADING : ITEM;
        } else {
            return ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void addAll(List<Item> items) {
        this.items.addAll(items);
        isLoaderVisible = false;
        notifyDataSetChanged();
    }

    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    private void removeItem(Item item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = true;
        addItem(new Item());
    }

    public void removeLoading() {
        isLoaderVisible = false;
        if (items != null) {
            int position = items.size() - 1;
            Item item = getItem(position);
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        items = null;
    }

    Item getItem(int position) {
        return items.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        CardView mCard;
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);

            this.mCard = itemView.findViewById(R.id.repo_info_card);
            this.textView = itemView.findViewById(R.id.textViewTitle);
        }

        @Override
        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            Item item = items.get(position);

            textView.setText(item.getName());
            mCard.setOnClickListener(view -> mHandler.onCardClick(items.get(position).getName()));
        }
    }

    public class FooterHolder extends BaseViewHolder {
        ProgressBar mProgressBar;

        FooterHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        protected void clear() {
        }

    }

    public interface RepoListAdapterOnclickHandler {
        void onCardClick(String repoJson);
    }

}
