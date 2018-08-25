package me.chaitanyavp.aerozen;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TaskRecyclerListAdapter extends RecyclerView.Adapter<BoardViewHolder> {

  private final ArrayList<String> mItems;
  private final RoomActivity roomActivity;

  private int dragFrom;
  private int dragTo;

  public TaskRecyclerListAdapter(ArrayList<String> tasks, RoomActivity room) {
    mItems = tasks;
    roomActivity = room;
    dragFrom = -1;
    dragTo = -1;
  }

  @NonNull
  @Override
  public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task, parent, false);
    return new BoardViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BoardViewHolder holder, int position) {
    holder.setName(mItems.get(position));
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
    if (dragFrom == -1){
      dragFrom = Math.min(fromPosition, toPosition);
    }
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(mItems, i, i + 1);
      }
      dragFrom = Math.min(dragFrom, fromPosition);
      dragTo = Math.max(dragTo, toPosition);
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(mItems, i, i - 1);
      }
      dragFrom = Math.min(dragFrom, toPosition);
      dragTo = Math.max(dragTo, fromPosition);
    }
    notifyItemMoved(fromPosition, toPosition);
    return true;
  }

  public void onDropped(){
    if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
      for(int i = dragFrom; i <= dragTo; i++){
        if(i < mItems.size()){
          roomActivity.setBoardPosition(mItems.get(i), i);
        }
      }
    }
    dragFrom = -1;
    dragTo = -1;
  }
}
