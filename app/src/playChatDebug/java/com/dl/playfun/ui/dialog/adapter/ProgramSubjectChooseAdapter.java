package com.dl.playfun.ui.dialog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dl.playfun.R;
import com.dl.playfun.entity.ThemeItemEntity;
import com.dl.playfun.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wulei
 */
public class ProgramSubjectChooseAdapter extends RecyclerView.Adapter<ProgramSubjectChooseAdapter.RecyclerHolder> {

    private final Context mContext;
    private List<ThemeItemEntity> dataList = new ArrayList<>();

    private ProgramSubjectChooseAdapterListener programSubjectChooseAdapterListener = null;

    public ProgramSubjectChooseAdapter(RecyclerView recyclerView) {
        this.mContext = recyclerView.getContext();
    }

    public ProgramSubjectChooseAdapterListener getProgramSubjectChooseAdapterListener() {
        return programSubjectChooseAdapterListener;
    }

    public void setProgramSubjectChooseAdapterListener(ProgramSubjectChooseAdapterListener programSubjectChooseAdapterListener) {
        this.programSubjectChooseAdapterListener = programSubjectChooseAdapterListener;
    }

    public void setData(List<ThemeItemEntity> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_program_subject, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        ThemeItemEntity itemEntity = dataList.get(position);
        holder.tvName.setText(itemEntity.getTitle());
        Glide.with(mContext)
                .load(StringUtil.getFullImageUrl(itemEntity.getSmallIcon()))
                .apply(new RequestOptions()
                        .placeholder(mContext.getResources().getDrawable(R.drawable.default_avatar))
                        .error(mContext.getResources().getDrawable(R.drawable.default_avatar)))
                .into(holder.ivIcon);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(v -> {
            if (programSubjectChooseAdapterListener != null) {
                int p = (int) v.getTag();
                programSubjectChooseAdapterListener.onItemClick(dataList.get(p));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ProgramSubjectChooseAdapterListener {
        void onItemClick(ThemeItemEntity itemEntity);
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon = null;
        TextView tvName = null;

        private RecyclerHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}