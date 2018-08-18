package me.chaitanyavp.aerozen;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RecyclerListAdapter extends RecyclerView.Adapter<BoardViewHolder> {

  private static final String[] STRINGS = new String[]{
      "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"
  };

  private final ArrayList<String> mItems = new ArrayList<>();

  public RecyclerListAdapter() {
    mItems.addAll(Arrays.asList(STRINGS));
  }

  @NonNull
  @Override
  public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_board, parent, false);
    return new BoardViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BoardViewHolder holder, int position) {
//    holder.textView.setText(mItems.get(position));
  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }

  public void onItemDismiss(int position) {
    mItems.remove(position);
    notifyItemRemoved(position);
  }

  public boolean onItemMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(mItems, i, i + 1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(mItems, i, i - 1);
      }
    }
    notifyItemMoved(fromPosition, toPosition);
    return true;
  }
}
