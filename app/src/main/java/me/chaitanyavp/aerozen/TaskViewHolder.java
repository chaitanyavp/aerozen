package me.chaitanyavp.aerozen;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class TaskViewHolder extends RecyclerView.ViewHolder {

  private TextView name;

  public TaskViewHolder(View itemView) {
    super(itemView);
    name = itemView.findViewById(R.id.section_label);
  }

  public void setName(String text){
    name.setText(text);
  }
}
