package srgpanov.yandex_test_task;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import srgpanov.yandex_test_task.Data.TranslatedWords;

/**
 * Created by Пан on 30.03.2017.
 */

public class FavoritsAdapter extends RecyclerView.Adapter<FavoritsAdapter.ViewHolder> implements Filterable,RealmChangeListener {
    private RealmResults<TranslatedWords> mFavoritsWords;
    private Realm mRealm;
    private ViewHolder.CustomClickListener mCustomClickListener;


    //конструктор адаптера, в него передаются данные которые будут биндиться
    public FavoritsAdapter(RealmResults<TranslatedWords> favoritsWords, Realm realm, ViewHolder.CustomClickListener listener) {
        this.mCustomClickListener = listener;
        mRealm = realm;
        mFavoritsWords = favoritsWords;
        mFavoritsWords.addChangeListener(this);
    }

    //    public TranslatedWords getItem(int position) {
//        return mFavoritsWords.get(position);
//    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmar_history_list, parent, false);
        return new ViewHolder(v, mCustomClickListener);
    }

    // Заменяет контент отдельного view
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TranslatedWords translatedWord = mFavoritsWords.get(position);
        if (translatedWord.isFavorits()) {
            holder.mItemImageView.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
        } else {
            holder.mItemImageView.setImageResource(R.drawable.ic_bookmark_grey_24dp);
        }
        holder.mPrimaryTextView.setText(translatedWord.getInputText());
        holder.mSecondaryTextView.setText(translatedWord.getTranslatedText());
        holder.mDirectionTextView.setText(translatedWord.getDirectionTranslation());

    }

//    @Override
//    public long getItemId(int position) {
//        return mFavoritsWords.get(position).getId();
//    }

    @Override
    public int getItemCount() {
        return mFavoritsWords.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (charSequence != null || charSequence.toString().equals("")) {
                    mFavoritsWords = filterWords(charSequence.toString());
                    notifyDataSetChanged();
                }
            }
        };
    }

    private RealmResults<TranslatedWords> filterWords(String query) {
        return mRealm
                .where(TranslatedWords.class)
                .beginGroup().contains("InputText", query)
                .or()
                .contains("TranslatedText", query)
                .endGroup()
                .findAll();
    }

    public void remove(int position) {
        mRealm.beginTransaction();
        mFavoritsWords.get(position).setFavorits(false);
        mRealm.commitTransaction();

        notifyItemRemoved(position);
    }

    @Override
    public void onChange(Object element) {
        notifyDataSetChanged();
    }


    //создаём ViewHolder, в нём находим все View нашео итема
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mItemImageView;
        private TextView mPrimaryTextView;
        private TextView mSecondaryTextView;
        private TextView mDirectionTextView;
        private CustomClickListener mListener;

        public ViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            this.mListener = customClickListener;
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
            mPrimaryTextView = (TextView) itemView.findViewById(R.id.item_primary_text);
            mSecondaryTextView = (TextView) itemView.findViewById(R.id.item_seconadary_text);
            mDirectionTextView = (TextView) itemView.findViewById(R.id.item_direction_translation_text);
            mItemImageView.setOnClickListener(this);
            mPrimaryTextView.setOnClickListener(this);
            mSecondaryTextView.setOnClickListener(this);
        }

        public interface CustomClickListener {
            void onItemClickListener(View view, int position);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClickListener(view, getAdapterPosition());
            }
        }
    }
}
