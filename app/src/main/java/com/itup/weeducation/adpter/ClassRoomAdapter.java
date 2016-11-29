package com.itup.weeducation.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itup.weeducation.GlobalParameters;
import com.itup.weeducation.R;
import com.itup.weeducation.model.ClassRoom;

import java.util.ArrayList;

/**
 * Created by Alex-Dell on 10/13/2016.
 */

public class ClassRoomAdapter extends RecyclerView.Adapter<ClassRoomAdapter.ClassRoomViewHolder>
        implements View.OnClickListener{

    private View.OnClickListener Listener;
    private ArrayList<ClassRoom> ClassRoomList;

    public ClassRoomAdapter() {
        this.ClassRoomList = GlobalParameters.ClassRoomList;
    }

    public static class ClassRoomViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvTitle;
        private TextView tvSubTitle;

        public ClassRoomViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvSubTitle = (TextView)itemView.findViewById(R.id.tvSubTitle);
        }

        public void bindClassRoom(ClassRoom classRoom) {
            itemView.setTag(classRoom);
            tvTitle.setText(classRoom.title);
            tvSubTitle.setText(classRoom.sub_title);
        }

    }

    @Override
    public ClassRoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_item_classroom, viewGroup, false);

        itemView.setOnClickListener(this);
        ClassRoomViewHolder classRoomViewHolder = new ClassRoomViewHolder(itemView);

        return classRoomViewHolder;
    }

    @Override
    public void onBindViewHolder(ClassRoomViewHolder classRoomViewHolder, int position) {
        ClassRoom item = ClassRoomList.get(position);
        classRoomViewHolder.bindClassRoom(item);
    }

    @Override
    public int getItemCount() {
        return ClassRoomList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.Listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(Listener != null)
            Listener.onClick(view);
    }

}
