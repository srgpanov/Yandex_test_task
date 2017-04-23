package srgpanov.yandex_test_task.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmResults;
import srgpanov.yandex_test_task.Data.FavoritsWord;
import srgpanov.yandex_test_task.Data.TranslatedWords;
import srgpanov.yandex_test_task.DeleteHistoryDialog;
import srgpanov.yandex_test_task.adapters.HistoryAdapter;
import srgpanov.yandex_test_task.OnChoiceItem;
import srgpanov.yandex_test_task.R;
import srgpanov.yandex_test_task.Utils.ConstantManager;


public class HistoryFragment extends android.app.Fragment {
    private RecyclerView mRecyclerView;
    private Toolbar mHistoryToolbar;
    private RealmResults<TranslatedWords> mTranslatedWords;
    private HistoryAdapter mHistoryAdapter;
    private Realm mRealm;
    private SharedPreferences mPreferences;
    private OnChoiceItem mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRealm = Realm.getDefaultInstance();
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        mHistoryToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_history);
        setupToolbar();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_history);
        setupRecycleView();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mListener=(OnChoiceItem)getActivity();//листенер для
        super.onActivityCreated(savedInstanceState);
    }

    private void setupToolbar() {//сделал меню через тулбар,а не через экшн бар,
        mHistoryToolbar.inflateMenu(R.menu.history_menu);
        MenuItem searchItem = mHistoryToolbar.getMenu().findItem(R.id.menu_search_history);
        MenuItem deleteItem = mHistoryToolbar.getMenu().findItem(R.id.menu_delete_history);
        MenuItem sortDescendingItem = mHistoryToolbar.getMenu().findItem(R.id.menu_sort_descending_history);
        MenuItem sortAscendingItem = mHistoryToolbar.getMenu().findItem(R.id.menu_sort_ascending_history);
        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                  @Override
                                                  public boolean onMenuItemClick(MenuItem menuItem) {
                                                      FragmentManager manager = getFragmentManager();
                                                      DeleteHistoryDialog deleteHistoryDialog = new DeleteHistoryDialog();
                                                      deleteHistoryDialog.setTargetFragment(HistoryFragment.this, ConstantManager.CODE_DELETE_HISTORY);
                                                      deleteHistoryDialog.show(manager, "deleteDialog");
                                                      return true;
                                                  }
                                              }
        );

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mHistoryAdapter.getFilter().filter(newText);
                return true;
            }
        });
        //вешаем листенеры на кнопки сортировки и сохраняем в шаред преференс
        sortAscendingItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!mPreferences.getBoolean(ConstantManager.SORTING_HISTORY, true)) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putBoolean(ConstantManager.SORTING_HISTORY, true);
                    editor.apply();
                    mHistoryAdapter.sort(true);
                    return true;
                } else return true;
            }
        });
        sortDescendingItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (mPreferences.getBoolean(ConstantManager.SORTING_HISTORY, true)) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putBoolean(ConstantManager.SORTING_HISTORY, false);
                    editor.apply();
                    mHistoryAdapter.sort(false);
                    return true;
                } else return true;
            }
        });
    }

//при переключенни на фрагмент во вью пейджере обновляем данные в адаптере
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mHistoryAdapter.notifyDataSetChanged();
        }
    }
//получаем результат из диалог фрагмента удаления истории
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstantManager.CODE_DELETE_HISTORY) {
                final Realm newRealm = Realm.getDefaultInstance();
                newRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(TranslatedWords.class).findAll().deleteAllFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        newRealm.close();
                        mHistoryAdapter.notifyDataSetChanged();

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        newRealm.close();

                    }
                });

            }

        }
    }

    private void setupRecycleView() {
        mTranslatedWords = mRealm.where(TranslatedWords.class).findAllAsync();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mHistoryAdapter = new HistoryAdapter(mTranslatedWords, mRealm, mPreferences.getBoolean(ConstantManager.SORTING_HISTORY, true), new HistoryAdapter.ViewHolder.CustomClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                switch (view.getId()) {
                    case R.id.item_image_view:
                        setFavoritWord(position);
                        break;
                    case R.id.item_primary_text:
                        mListener.OnChoiceItem((int)mHistoryAdapter.getItemId(position), true);
                        break;
                    case R.id.item_seconadary_text:
                        mListener.OnChoiceItem((int)mHistoryAdapter.getItemId(position), true);
                        break;
                    case R.id.item_container:
                        mListener.OnChoiceItem((int)mHistoryAdapter.getItemId(position), true);
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mHistoryAdapter);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
    }


//метод добавляет слово в избранное
    private void setFavoritWord(final int position) {
        final int id = (int)mHistoryAdapter.getItemId(position);
        final Realm newRealm = Realm.getDefaultInstance();
        newRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {//меняем "избранноесть" слова в истории
                boolean fav = realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().isFavorits();
                realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().setFavorits(!fav);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                newRealm.close();
                mHistoryAdapter.notifyItemChanged(position);
                final Realm newRealm1 = Realm.getDefaultInstance();
                newRealm1.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().isFavorits()) {//если слово в истории сделали избранным то добавляем слово в избранное
                            Number currentIdNum = realm.where(FavoritsWord.class).max("Id");
                            int nextId;
                            if (currentIdNum == null) {
                                nextId = 1;
                            } else {
                                nextId = currentIdNum.intValue() + 1;
                            }
                            FavoritsWord word = realm.createObject(FavoritsWord.class, nextId);
                            word.setInputText(realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().getInputText());
                            word.setTranslatedText(realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().getTranslatedText());
                            word.setDirectionTranslation(realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().getDirectionTranslation());
                            word.setFavorits(true);
                            word.setHistoryWords(realm.where(TranslatedWords.class).equalTo("Id", id).findFirst());
                            word.setDefenitions(realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().getDefenitions());
                            realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().setFavoritsWord(word);
                        } else {//если убрали "избранность" у слова в истории то удаляем связанное "избранное" слово
                            if (realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().getFavoritsWord() != null)
                                realm.where(TranslatedWords.class).equalTo("Id", id).findFirst().getFavoritsWord().deleteFromRealm();
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        newRealm1.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        newRealm1.close();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                newRealm.close();
            }
        });

    }
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(getActivity(), R.drawable.ic_clear_grey_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) getActivity().getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                HistoryAdapter adapter = (HistoryAdapter) recyclerView.getAdapter();

                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                HistoryAdapter adapter = (HistoryAdapter) mRecyclerView.getAdapter();
                adapter.remove(swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }
                if (!initiated) {
                    init();
                }
                //рисуем красный бэкграунд
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(canvas);

                //рисуем крестик
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(canvas);
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
//рисуем анимацию удаления
    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            Drawable background;
            boolean initiated;
            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                if (!initiated) {
                    init();
                }
                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;
                    int left = 0;
                    int right = parent.getWidth();
                    int top = 0;
                    int bottom = 0;
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }
                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }



}
