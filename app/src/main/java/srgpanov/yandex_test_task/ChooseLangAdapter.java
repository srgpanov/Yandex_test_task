package srgpanov.yandex_test_task;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import srgpanov.yandex_test_task.Data.Langauge;

/**
 * Created by Пан on 04.04.2017.
 */

public class ChooseLangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mItems;
    private final int LANGAUGE = 0, CATEGORY = 1;
    private ChoseLangHolder.CustomClickListener mCustomClickListener;

    public ChooseLangAdapter(List<Object> items, ChoseLangHolder.CustomClickListener customClickListener) {
        this.mCustomClickListener = customClickListener;
        this.mItems = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof ArrayList) {
            return LANGAUGE;
        } else if (mItems.get(position) instanceof String) {
            return CATEGORY;
        }
        return -1;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case LANGAUGE:
                View viewlang = inflater.inflate(R.layout.item_choose_lang_item, parent, false);
                viewHolder = new ChoseLangHolder(viewlang, mCustomClickListener);
                break;
            case CATEGORY:
                View viewCategory = inflater.inflate(R.layout.item_choose_lang_category, parent, false);
                viewHolder = new CategoryHolder(viewCategory);
                break;
            default:
                View viewlang1 = inflater.inflate(R.layout.item_choose_lang_item, parent, false);
                viewHolder = new ChoseLangHolder(viewlang1, mCustomClickListener);
                break;
        }
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        if (mItems.get(position) instanceof Langauge) {
            return ((Langauge) mItems.get(position)).getId();
        } else return -1;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case LANGAUGE:
                ChoseLangHolder langHolder = (ChoseLangHolder) holder;
                configureChooseLangHolder(langHolder, position);
                break;
            case CATEGORY:
                CategoryHolder categoryHolder = (CategoryHolder) holder;
                configureCategoryHolder(categoryHolder, position);
                break;
            default:
                ChoseLangHolder langHolder1 = (ChoseLangHolder) holder;
                configureChooseLangHolder(langHolder1, position);
                break;
        }
    }
//
//    @Override
//    public void onBindViewHolder(ChoseLangHolder holder, int position) {
//        holder.mLangTextView.setText(mLanguages.get(position));
//    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void configureChooseLangHolder(ChoseLangHolder langHolder, int position) {
        Langauge langauge = (Langauge) mItems.get(position);
        if (langauge != null) {
            langHolder.mLangTextView.setText(langauge.getName());
            if (langauge.isFavorite()) {
                langHolder.mImageView.setImageResource(R.drawable.ic_star_yellow_24dp);
            } else {
                langHolder.mImageView.setImageResource(R.drawable.ic_star_border_grey_24dp);
            }
        }
    }


    private void configureCategoryHolder(CategoryHolder categoryHolder, int position) {
        String category = (String) mItems.get(position);
        if (category != null) {
            categoryHolder.mcategoryTextView.setText(category);
        }
    }

//создаём ViewHolder, в нём находим все View нашео итема
public static class ChoseLangHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView mLangTextView;
    private ImageView mImageView;
    private CustomClickListener mListener;

    public ChoseLangHolder(View itemView, CustomClickListener customClickListener) {
        super(itemView);
        this.mListener = customClickListener;
        mLangTextView = (TextView) itemView.findViewById(R.id.choose_lang_text_view);
        mLangTextView.setOnClickListener(this);
        mImageView = (ImageView) itemView.findViewById(R.id.choose_lang_image_view);
        mImageView.setOnClickListener(this);
    }

    public interface CustomClickListener {
        void onLangClickListener(View view, int position);
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onLangClickListener(view, getAdapterPosition());
        }
    }
}

public static class CategoryHolder extends RecyclerView.ViewHolder {
    private TextView mcategoryTextView;

    public CategoryHolder(View itemView) {
        super(itemView);
        mcategoryTextView = (TextView) itemView.findViewById(R.id.langauge_category);
    }
}
}
