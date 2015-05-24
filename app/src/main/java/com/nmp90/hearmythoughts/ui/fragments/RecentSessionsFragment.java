package com.nmp90.hearmythoughts.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nmp90.hearmythoughts.R;
import com.nmp90.hearmythoughts.constants.Constants;
import com.nmp90.hearmythoughts.providers.AuthProvider;
import com.nmp90.hearmythoughts.providers.FakeDataProvider;
import com.nmp90.hearmythoughts.stores.UsersStore;
import com.nmp90.hearmythoughts.ui.adapters.RecentSessionsAdapter;
import com.nmp90.hearmythoughts.ui.fragments.notifications.CreateSessionFragment;
import com.nmp90.hearmythoughts.ui.fragments.notifications.JoinSessionFragment;
import com.nmp90.hearmythoughts.ui.utils.NavUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-3-7.
 */
public class RecentSessionsFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = RecentSessionsFragment.class.getSimpleName();

    private Activity activity;

    @InjectView(R.id.action_join)
    FloatingActionButton actionJoin;

    @InjectView(R.id.action_create)
    FloatingActionButton actionCreate;

    @InjectView(R.id.multiple_actions)
    FloatingActionsMenu actionsMenu;

    @InjectView(R.id.lv_recent_sessions)
    ListView lvRecentSessions;

    @InjectView(R.id.tv_login_required)
    TextView tvLoginRequired;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UsersStore.getInstance(getActivity()).register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UsersStore.getInstance(getActivity()).unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_sessions, container, false);
        View headerView = inflater.inflate(R.layout.view_header_recent_sessions, container, false);

        ButterKnife.inject(this, view);
        //lvRecentSessions.addHeaderView(headerView);
        loadAdapter();

        actionCreate.setOnClickListener(this);
        actionJoin.setOnClickListener(this);

        if(AuthProvider.getInstance(getActivity()).isUserLoggedIn() == false) {
            actionsMenu.setVisibility(View.GONE);
            lvRecentSessions.setVisibility(View.GONE);
            tvLoginRequired.setVisibility(View.VISIBLE);
            showLoginFragment();
        }
        return view;
    }

    private void loadAdapter() {
        lvRecentSessions.setAdapter(new RecentSessionsAdapter(getActivity(), FakeDataProvider.getRecentSessions(getActivity())));
    }

    @Override
    public void onClick(View v) {
        if(AuthProvider.getInstance(getActivity()).isUserLoggedIn() == false) {
            showLoginFragment();
            return;
        }
        switch(v.getId()) {
            case R.id.action_join:
                actionsMenu.collapse();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new JoinSessionFragment()).addToBackStack(Constants.TAG_JOIN_SESSION)
                        .commit();
                break;
            case R.id.action_create:
                actionsMenu.collapse();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new CreateSessionFragment()).addToBackStack(Constants.TAG_CREATE_SESSION)
                        .commit();
                break;
        }
    }

    @Subscribe
    public void userLogin(UsersStore.UserLoginEvent userLoginEvent) {
        AuthProvider.getInstance(getActivity()).login(userLoginEvent.getUser());
        actionsMenu.setVisibility(View.VISIBLE);
        lvRecentSessions.setVisibility(View.VISIBLE);
        tvLoginRequired.setVisibility(View.GONE);
        if(isAdded()) {
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    private void showLoginFragment() {
        NavUtils.showLoginFragment(getActivity().getSupportFragmentManager(), false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }
}
