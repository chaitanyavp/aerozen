package me.chaitanyavp.aerozen;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

public class BoardViewHolder extends RecyclerView.ViewHolder {

  private TextView name;

  public BoardViewHolder(View itemView) {
    super(itemView);
    name = itemView.findViewById(R.id.section_label);
  }

  public void setName(String text){
    name.setText(text);
  }
}
