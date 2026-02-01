package com.yisu.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.yisu.app.R;
import com.yisu.app.model.Hotel;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {

    private List<Hotel> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Hotel hotel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public HotelAdapter(List<Hotel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = list.get(position);
        
        // Hotel name (with English name if available)
        if (hotel.nameEn != null && !hotel.nameEn.isEmpty()) {
            holder.tvName.setText(hotel.name + " / " + hotel.nameEn);
        } else {
            holder.tvName.setText(hotel.name);
        }
        
        holder.tvCity.setText(hotel.city != null ? hotel.city : "");
        holder.tvStar.setText(hotel.starRating != null ? hotel.starRating : "");
        holder.tvPrice.setText("¥" + (int)hotel.minPrice + "起");
        
        // Load cover image
        String imageUrl = hotel.coverImage != null && !hotel.coverImage.isEmpty() 
            ? hotel.coverImage 
            : (hotel.images != null && !hotel.images.isEmpty() ? hotel.images : null);
        
        if (imageUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.color.primary_blue)
                    .error(R.color.primary_blue)
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.primary_blue));
        }
        
        // Show review count and favorite count if available
        if (hotel.reviewCount > 0 || hotel.favoriteCount > 0) {
            StringBuilder info = new StringBuilder();
            if (hotel.reviewCount > 0) {
                info.append("点评").append(hotel.reviewCount);
            }
            if (hotel.favoriteCount > 0) {
                if (info.length() > 0) info.append(" · ");
                info.append("收藏").append(hotel.favoriteCount);
            }
            holder.tvExtraInfo.setText(info.toString());
            holder.tvExtraInfo.setVisibility(View.VISIBLE);
        } else {
            holder.tvExtraInfo.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if(listener != null) listener.onItemClick(hotel);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvCity, tvStar, tvPrice, tvExtraInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivHotelImage);
            tvName = itemView.findViewById(R.id.tvHotelName);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvStar = itemView.findViewById(R.id.tvStarRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvExtraInfo = itemView.findViewById(R.id.tvExtraInfo);
        }
    }
}
