package com.smartpantry.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** Custom adapter that lists suggested recipes. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface OnRecipeClick {
        void onClick(Recipe recipe);
    }

    private List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClick listener;

    public RecipeAdapter(OnRecipeClick listener) {
        this.listener = listener;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Recipe r = recipes.get(position);
        h.tvName.setText(r.getName());
        h.tvInfo.setText(r.getIngredients().size() + " ingredients - tap to view method");
        h.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvRecipeName);
            tvInfo = v.findViewById(R.id.tvRecipeInfo);
        }
    }
}
