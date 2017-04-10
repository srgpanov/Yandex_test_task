package srgpanov.yandex_test_task;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Пан on 04.04.2017.
 */

public class ChooseLangAdapter extends RecyclerView.Adapter<ChooseLangAdapter.ChoseLangHolder> {

    private List<String> mLanguages;
    private ChoseLangHolder.CustomClickListener mCustomClickListener;

    public ChooseLangAdapter(List<String> strings, ChoseLangHolder.CustomClickListener customClickListener) {
        this.mCustomClickListener=customClickListener;
        this.mLanguages = strings;
    }

    @Override
    public ChoseLangHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_lang_item, parent, false);
        return new ChoseLangHolder(view, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(ChoseLangHolder holder, int position) {
        holder.mLangTextView.setText(mLanguages.get(position));
    }

    @Override
    public int getItemCount() {
        return mLanguages.size();
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
            void onLangClickListener(View view,int position);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onLangClickListener(view, getAdapterPosition());
            }
        }
    }
}
