package jmt.com.myapplication.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jmt.com.myapplication.R;
import jmt.com.myapplication.adapters.GroupAdapter;
import jmt.com.myapplication.helpers.VolleyRequest;
import jmt.com.myapplication.interfaces.IVolleyCallback;
import jmt.com.myapplication.models.Group;

public class GroupsFragment extends Fragment {
    public View rootView;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupList = new ArrayList<>();

        new getDataGroups().execute();

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class getDataGroups extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            Map<String, String> paramsURL = new HashMap<>();
            new VolleyRequest(getContext()).GET("/group/", paramsURL, new IVolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject result) {
                    try {
                        Gson gson = new Gson();
                        JSONArray dataGroups = result.getJSONArray("data");
                        groupList = gson.fromJson(dataGroups.toString(), new TypeToken<List<Group>>() {
                        }.getType());

                        Log.d("Group", groupList.toString());

                        // set loading screen to off
                        rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                        groupAdapter = new GroupAdapter(getContext(), groupList);
                        recyclerView.setAdapter(groupAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return false;
        }
    }
}
