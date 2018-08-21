package me.chaitanyavp.aerozen;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class RecyclerListAdapter extends RecyclerView.Adapter<BoardViewHolder> {

  private final ArrayList<String> mItems;
  private final HashMap<String, String> mNames;
  private final RoomActivity roomActivity;

  public RecyclerListAdapter(ArrayList<String> items, HashMap<String, String> names, RoomActivity room) {
    mItems = items;
    mNames = names;
    roomActivity = room;
  }

  @NonNull
  @Override
  public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_board, parent, false);
    return new BoardViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BoardViewHolder holder, int position) {
    holder.setName(mNames.get(mItems.get(position)));
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
        roomActivity.setBoardPosition(mItems.get(i), i);
        roomActivity.setBoardPosition(mItems.get(i+1), i+1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(mItems, i, i - 1);
        roomActivity.setBoardPosition(mItems.get(i), i);
        roomActivity.setBoardPosition(mItems.get(i-1), i-1);
      }
    }
    notifyItemMoved(fromPosition, toPosition);
    return true;
  }
}
