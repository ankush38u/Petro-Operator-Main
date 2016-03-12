package com.paprbit.module;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.paprbit.module.retrofit.adapter.AllRequestsAdapter;
import com.paprbit.module.retrofit.gson_pojo.RequestData;
import com.paprbit.module.retrofit.utility.ServiceGenerator;
import com.paprbit.module.retrofit.utility.Storage;
import com.paprbit.module.ui.MessageAdmin;
import com.paprbit.module.ui.MyRequests;
import com.paprbit.module.utility.UserIntraction;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ResideMenu resideMenu;
    Context mContext;
    ResideMenuItem requestsitem;
    ResideMenuItem complaintitem;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recView)
    RecyclerView recView;
    @Bind(R.id.parent_layout)
    LinearLayout parentLayout;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        setSupportActionBar(toolbar);
        slidemenuSetter();


        pd = new ProgressDialog(this);
        pd.setMessage("Loading .. ");
        pd.setCancelable(false);
        recView.setVisibility(View.INVISIBLE);
        recView.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(llm);
        setRecyclerView();
        setTitle("Request Queue");

    }

    private void setRecyclerView() {
        Call<List<RequestData>> call = ServiceGenerator.getService(getApplicationContext()).getRequestByUid(Storage.getStringFromPrefs(getApplicationContext(), getString(R.string.uid)));
        call.enqueue(new Callback<List<RequestData>>() {
            @Override
            public void onResponse(Response<List<RequestData>> response, Retrofit retrofit) {
                pd.hide();
                recView.setVisibility(View.VISIBLE);
                if (response.isSuccess()) {

                    if (response.body() != null && response.body().size() > 0) {
                        AllRequestsAdapter allRequestsAdapter = new AllRequestsAdapter(response.body(), MainActivity.this);
                        recView.setAdapter(allRequestsAdapter);
                    }

                } else {
                    //handle all error like 404,500 etc server errors based on request
                    UserIntraction.makeSnack(parentLayout, "Error Code: " + response.raw().code() + "  " + response.raw().message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                pd.hide();
                //  Log.d(TAG, t.getMessage());
                UserIntraction.makeSnack(parentLayout, getString(R.string.internet_error));
            }
        });
    }

    private void slidemenuSetter() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.image_bg);
        resideMenu.attachToActivity(this);

        // create menu items;
        String titles[] = {"NearBy Pumps", "Fill Petrol", "My Requests", "Monthly Expenses", "Complaint Admin"};
        int icon[] = {R.drawable.petrolpump, R.drawable.fill_petrol, R.drawable.old_requests, R.drawable.expenses, R.drawable.complaint};


        requestsitem = new ResideMenuItem(this, icon[2], titles[2]);
        requestsitem.setOnClickListener(this);
        resideMenu.addMenuItem(requestsitem, ResideMenu.DIRECTION_LEFT);

        complaintitem = new ResideMenuItem(this, icon[4], titles[4]);
        complaintitem.setOnClickListener(this);
        resideMenu.addMenuItem(complaintitem, ResideMenu.DIRECTION_LEFT);


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {

        if (v == requestsitem) {
            startActivity(new Intent(MainActivity.this, MyRequests.class));
        }
        if (v == complaintitem) {
            startActivity(new Intent(MainActivity.this, MessageAdmin.class));
        }
    }
}
