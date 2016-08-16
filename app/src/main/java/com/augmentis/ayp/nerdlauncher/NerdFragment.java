package com.augmentis.ayp.nerdlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Theerawuth on 8/15/2016.
 */
public class NerdFragment extends Fragment {

    private static final String TAG = "NerdFragment";

    public static NerdFragment newInstance() {

        Bundle args = new Bundle();

        NerdFragment fragment = new NerdFragment();
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView mRecycleView;
    List<ResolveInfo> mActivities;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd, container, false);

        mRecycleView = (RecyclerView) v.findViewById(R.id.nerd_recycle_view);
        mRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();
        mRecycleView.setAdapter(new NerdAdapter(mActivities));
        return v;
    }


    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "Found " + activities.size() +  " activities.");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();

                String labelA = a.loadLabel(pm).toString();
                String labelB = b.loadLabel(pm).toString();

                // INSENSITIVE = "ไม่สนใจว่าเป็นอะไร ตัวอักษรใหญ่-เล็ก"
                return String.CASE_INSENSITIVE_ORDER.compare(labelA, labelB);
            }
        });

        mActivities = activities;

    }

    public class NerdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private ImageView mIconImageView;
        private ResolveInfo mresolveInfo;

        public NerdViewHolder(View itemView) {
            super(itemView);

            mIconImageView = (ImageView) itemView.findViewById(R.id.icon_app);
            mNameTextView = (TextView) itemView.findViewById(R.id.name_app);

            itemView.setOnClickListener(this);


        }

        protected void bindActivity(ResolveInfo resolveInfo){
            mresolveInfo = resolveInfo;

            PackageManager pm = getActivity().getPackageManager();
            String activitiesName = resolveInfo.loadLabel(pm).toString();
            Drawable activitiesIcon = resolveInfo.loadIcon(pm);


            mNameTextView.setText(activitiesName);
            mIconImageView.setImageDrawable(activitiesIcon);

        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mresolveInfo.activityInfo;

            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);


            Log.i(TAG, "Launching : " + activityInfo.applicationInfo.packageName
                    + " --> " + activityInfo.name);

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
    }

    public class NerdAdapter extends RecyclerView.Adapter<NerdViewHolder>{

        List<ResolveInfo> mActivities;

        NerdAdapter(List<ResolveInfo> activities){
            mActivities = activities;
        }

        @Override
        public NerdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_nerd_icon, parent, false);

            return new NerdViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(NerdViewHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);

            holder.bindActivity(resolveInfo);

        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }


    }
}
