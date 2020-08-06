package com.fastbuyapp.omar.fastbuy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fastbuyapp.omar.fastbuy.entidades.Tutorial_Item;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {

    private List<Tutorial_Item> tutorialItems;

    public TutorialAdapter(List<Tutorial_Item> tutorialItems) {
        this.tutorialItems = tutorialItems;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TutorialViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutorial, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        holder.setTutorialData(tutorialItems.get(position));
    }

    @Override
    public int getItemCount() {
        return tutorialItems.size();
    }

    class TutorialViewHolder extends RecyclerView.ViewHolder{
        private ImageView imagenTuto;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenTuto = (ImageView) itemView.findViewById(R.id.imgTutorial);
        }

        void setTutorialData(Tutorial_Item tutorial_item){
            imagenTuto.setImageResource(tutorial_item.getImagen());
        }
    }
}
