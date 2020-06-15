package com.nitkkr.nitkkr_admin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HomeCardViewAdapter extends RecyclerView.Adapter<HomeCardViewAdapter.HomeCardViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;

    public HomeCardViewAdapter(Context mContext, List<String> titles, List<Integer> images){
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public HomeCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.home_card_view, viewGroup, false);
        return new HomeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCardViewHolder homeCardViewHolder, int i) {
        homeCardViewHolder.title.setText(titles.get(i));
        homeCardViewHolder.gridIcon.setImageResource(images.get(i));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }


    public class HomeCardViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView gridIcon;
        public HomeCardViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.home_card_text);
            gridIcon = itemView.findViewById(R.id.home_card_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getAdapterPosition()){
                        case 0: //Add Student
                                v.getContext().startActivity(new Intent(v.getContext(),AddStudent.class));
                                break;
                        case 1: //Add Employee
                            v.getContext().startActivity(new Intent(v.getContext(),AddEmployee.class));
                                //TODO: intent
                                break;
                        case 2: //Add fee
                            v.getContext().startActivity(new Intent(v.getContext(),FeeUpdateActivity.class));
                            //TODO: intent
                            break;
                        case 3: //Add subject allocation
                            v.getContext().startActivity(new Intent(v.getContext(),SubjectAllotmentActivity.class));
                            //TODO: intent
                            break;
                        case 4: //Add course management
                            v.getContext().startActivity(new Intent(v.getContext(),CourseManagement.class));
                            break;
                        case 5: //Add student management
                            //TODO: intent
                            break;
                        case 6: //Add Employee management
                            //TODO: intent
                            break;
                    }
                }
            });
        }
    }
}
