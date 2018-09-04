package me.chaitanyavp.aerozen;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BoardViewHolder extends RecyclerView.ViewHolder {

  private TextView name;
  private View itemView;

  public BoardViewHolder(View itemView) {
    super(itemView);
    name = itemView.findViewById(R.id.section_label);
    this.itemView = itemView;
  }

  public void setBoard(final String boardID, final RoomActivity roomActivity){
    name.setText(roomActivity.getBoardName(boardID));
    itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        //    createBoardEditDialog
      }
    });
  }
}
