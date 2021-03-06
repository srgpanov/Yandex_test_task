package srgpanov.yandex_test_task.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import srgpanov.yandex_test_task.Data.FavoritsWord;
import srgpanov.yandex_test_task.R;

/**
 * Created by Пан on 30.03.2017.
 */

public class FavoritsAdapter extends RecyclerView.Adapter<FavoritsAdapter.ViewHolder> implements Filterable, RealmChangeListener {
    private RealmResults<FavoritsWord> mFavoritsWords;
    private Realm mRealm;
    private ViewHolder.CustomClickListener mCustomClickListener;


    //конструктор адаптера, в него передаются данные которые будут биндиться
    public FavoritsAdapter(RealmResults<FavoritsWord> favoritsWords, Realm realm, boolean sorting, ViewHolder.CustomClickListener listener) {
        this.mCustomClickListener = listener;
        mRealm = realm;
        if (sorting) {
            mFavoritsWords = favoritsWords;
        } else {
            mFavoritsWords = favoritsWords.sort("Id", Sort.DESCENDING);
        }
        mFavoritsWords.addChangeListener(this);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmar_history_list, parent, false);
        return new ViewHolder(v, mCustomClickListener);
    }

    // Заменяет контент отдельного view
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FavoritsWord favoritWord = mFavoritsWords.get(position);
        if (favoritWord.isFavorits()) {
            holder.mItemImageView.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
        } else {
            holder.mItemImageView.setImageResource(R.drawable.ic_bookmark_grey_24dp);
        }
        holder.mPrimaryTextView.setText(favoritWord.getInputText());
        holder.mSecondaryTextView.setText(favoritWord.getTranslatedText());
        holder.mDirectionTextView.setText(favoritWord.getDirectionTranslation());

    }

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
                if (charSequence != null) {
                    mFavoritsWords = filterWords(charSequence.toString().toLowerCase());
                    notifyDataSetChanged();
                }
            }
        };
    }

    private RealmResults<FavoritsWord> filterWords(final String query) {
        return mRealm
                .where(FavoritsWord.class)
                .beginGroup()
                .contains("InputText", query.toLowerCase())
                .or()
                .contains("TranslatedText", query.toLowerCase())
                .endGroup()
                .findAll();
    }

    //метод удаляет элемент избранного
    public void remove(final int position) {
        final int id = mFavoritsWords.get(position).getId();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {//удаляем в асинхроной транзакции
                FavoritsWord word = realm.where(FavoritsWord.class).equalTo("Id", id).findFirst();
                if (word != null) {
                    if (word.getHistoryWords() != null) {//если в истории есть избранное слово, делаем его не избранным
                        word.getHistoryWords().setFavorits(false);
                    }
                    word.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                notifyItemRemoved(position); //обновлем адаптер по завержению удаления
            }
        });

    }

    //метод сортировки
    public void sort(boolean increasing) {
        if (increasing) {
            mFavoritsWords = mFavoritsWords.sort("Id", Sort.ASCENDING);
            notifyDataSetChanged();

        } else {
            mFavoritsWords = mFavoritsWords.sort("Id", Sort.DESCENDING);
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {

        return mFavoritsWords.get(position).getId();
    }
// не стал обновлять адаптер при изменении данных, потому что анимация удаления прерывалась
    @Override
    public void onChange(Object element) {
    }


    //создаём ViewHolder, в нём находим все View нашео итема
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mItemImageView;
        private TextView mPrimaryTextView;
        private TextView mSecondaryTextView;
        private TextView mDirectionTextView;
        private RelativeLayout mLayout;
        private CustomClickListener mListener;

        public ViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            this.mListener = customClickListener;
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
            mPrimaryTextView = (TextView) itemView.findViewById(R.id.item_primary_text);
            mSecondaryTextView = (TextView) itemView.findViewById(R.id.item_seconadary_text);
            mDirectionTextView = (TextView) itemView.findViewById(R.id.item_direction_translation_text);
            mLayout=(RelativeLayout)itemView.findViewById(R.id.item_container) ;
            mLayout.setOnClickListener(this);
            mItemImageView.setOnClickListener(this);
            mPrimaryTextView.setOnClickListener(this);
            mSecondaryTextView.setOnClickListener(this);
        }
//используем интерфейс для обработки события в фрагменте
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
