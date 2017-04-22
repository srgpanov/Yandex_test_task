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
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import srgpanov.yandex_test_task.Data.FavoritsWord;
import srgpanov.yandex_test_task.DeleteFavotitsDialog;
import srgpanov.yandex_test_task.FavoritsAdapter;
import srgpanov.yandex_test_task.OnChoiceItem;
import srgpanov.yandex_test_task.R;
import srgpanov.yandex_test_task.Utils.ConstantManager;

/**
 * Created by Пан on 28.03.2017.
 */

public class FavoritsFragment extends android.app.Fragment  {
    private RecyclerView mRecyclerViewFavorits;
    private Toolbar mFavoritsToolbar;
    private RealmResults<FavoritsWord> mFavoritsWords;
    private FavoritsAdapter mFavoritsAdapter;
    private Realm mRealm;
    private SharedPreferences mPreferences;
    private OnChoiceItem mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRealm = Realm.getDefaultInstance();
        View rootView = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        mFavoritsToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_favorits);
        setupToolbar();
        mRecyclerViewFavorits = (RecyclerView) rootView.findViewById(R.id.recycler_view_favorits);
        setupRecycleView();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener=(OnChoiceItem)getActivity();
    }

    private void setupToolbar() {
        mFavoritsToolbar.inflateMenu(R.menu.favorits_menu);
        MenuItem searchItem = mFavoritsToolbar.getMenu().findItem(R.id.menu_search_favorits);
        MenuItem deleteItem = mFavoritsToolbar.getMenu().findItem(R.id.menu_delete_favorits);
        MenuItem sortDescendingItem = mFavoritsToolbar.getMenu().findItem(R.id.menu_sort_descending_favorits);
        MenuItem sortAscendingItem = mFavoritsToolbar.getMenu().findItem(R.id.menu_sort_ascending_favorits);
        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FragmentManager manager = getFragmentManager();
                DeleteFavotitsDialog deleteFavotitsDialog = new DeleteFavotitsDialog();
                deleteFavotitsDialog.setTargetFragment(FavoritsFragment.this, ConstantManager.CODE_DELETE_FAVORITS);
                deleteFavotitsDialog.show(manager, "deleteDialog");
                return true;
            }
        });

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.query_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mFavoritsAdapter.getFilter().filter(newText);
                return true;
            }
        });
        sortAscendingItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!mPreferences.getBoolean(ConstantManager.SORTING_FAVORITS, true)) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putBoolean(ConstantManager.SORTING_FAVORITS, true);
                    editor.apply();
                    mFavoritsAdapter.sort(true);
                    Toast.makeText(getActivity(), "sortAscendingItem", Toast.LENGTH_SHORT).show();
                    return true;
                } else return true;
            }
        });
        sortDescendingItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (mPreferences.getBoolean(ConstantManager.SORTING_FAVORITS, true)) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putBoolean(ConstantManager.SORTING_FAVORITS, false);
                    editor.apply();
                    mFavoritsAdapter.sort(false);
                    Toast.makeText(getActivity(), "sortDescendingItem", Toast.LENGTH_SHORT).show();
                    return true;
                } else return true;
            }
        });
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
    }

    private void setupRecycleView() {
        mFavoritsWords = mRealm.where(FavoritsWord.class).findAllAsync();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewFavorits.setLayoutManager(linearLayoutManager);
        mRecyclerViewFavorits.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mFavoritsAdapter = new FavoritsAdapter(mFavoritsWords, mRealm,mPreferences.getBoolean(ConstantManager.SORTING_FAVORITS, true), new FavoritsAdapter.ViewHolder.CustomClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                switch (view.getId()) {
                    case R.id.item_image_view:
                        mFavoritsAdapter.remove(position);
                        break;
                    case R.id.item_primary_text:
                        mListener.OnChoiceItem((int)mFavoritsAdapter.getItemId(position),false);
                        break;
                    case R.id.item_seconadary_text:
                        mListener.OnChoiceItem((int)mFavoritsAdapter.getItemId(position),false);
                        break;
                }
            }
        });
        mRecyclerViewFavorits.setAdapter(mFavoritsAdapter);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mFavoritsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConstantManager.CODE_DELETE_FAVORITS) {
                final Realm newRealm = Realm.getDefaultInstance();
                newRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<FavoritsWord> realmResults = realm.where(FavoritsWord.class).findAll();
                        for (FavoritsWord word : realmResults) {
                            if (word.getHistoryWords() != null) {
                                word.getHistoryWords().setFavorits(false);
                            }
                        }
                        realmResults.deleteAllFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        newRealm.close();
                        mFavoritsAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "delete", Toast.LENGTH_SHORT).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        newRealm.close();
                        Toast.makeText(getActivity(), "no", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
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
                FavoritsAdapter adapter = (FavoritsAdapter) recyclerView.getAdapter();
//                if(adapter.isUndoOn()&&adapter.isPendingRemoval(position)){
//                    return 0;
//                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();

                FavoritsAdapter adapter = (FavoritsAdapter) mRecyclerViewFavorits.getAdapter();
//                boolean undoOn = adapter.isUndoOn();
//                if (undoOn) {
//                    adapter.pendingRemoval(swipedPosition);
//                } else {
                adapter.remove(swipedPosition);
//                }

            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
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
        itemTouchHelper.attachToRecyclerView(mRecyclerViewFavorits);
    }

    private void setUpAnimationDecoratorHelper() {
        mRecyclerViewFavorits.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
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

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
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
