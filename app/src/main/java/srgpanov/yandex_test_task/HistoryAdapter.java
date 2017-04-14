package srgpanov.yandex_test_task;

import android.content.Context;
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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> implements Filterable, RealmChangeListener {
    private RealmResults<TranslatedWords> mTranslatedWords;
    private Realm mRealm;
    private ViewHolder.CustomClickListener mCustomClickListener;
    Context mContext;


    //конструктор адаптера, в него передаются данные которые будут биндиться
    public HistoryAdapter(RealmResults<TranslatedWords> translatedWords, Realm realm, ViewHolder.CustomClickListener listener) {
        this.mCustomClickListener = listener;
        mRealm=realm;
        mTranslatedWords = translatedWords;
        mTranslatedWords.addChangeListener(this);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmar_history_list, parent, false);
        return new ViewHolder(v, mCustomClickListener);
    }


    // Заменяет контент отдельного view
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TranslatedWords translatedWord = mTranslatedWords.get(position);
        if (translatedWord.isFavorits()) {
            holder.mItemImageView.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
        } else {
            holder.mItemImageView.setImageResource(R.drawable.ic_bookmark_grey_24dp);
        }
        holder.mPrimaryTextView.setText(translatedWord.getInputText());
        holder.mSecondaryTextView.setText(translatedWord.getTranslatedText());
        holder.mDirectionTextView.setText(translatedWord.getDirectionTranslation());

    }


    @Override
    public int getItemCount() {
        return mTranslatedWords.size();
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
                    mTranslatedWords = filterWords(charSequence.toString());
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

    public void remove(final int position) {
        final int id = mTranslatedWords.get(position).getId();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(realm.where(TranslatedWords.class).equalTo("Id", id).findFirst()!=null)
                realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().deleteFromRealm();
            }
        });
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
