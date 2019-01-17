package com.ouam.app.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ouam.app.R;
import com.ouam.app.adapter.ChatListAdapter;
import com.ouam.app.chat.ConnectionManager;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.UserChatResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;
import com.ouam.app.utils.RecyclerItemTouchHelper;
import com.ouam.app.utils.RecylcerItemTouchHelper;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatListActivityScreen extends BaseActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, RecylcerItemTouchHelper.RecyclerItemTouchHelperListener {


    @BindView(R.id.chat_par_lay)
    LinearLayout mChatParLay;

    @BindView(R.id.header_txt)
    TextView mHeaderTxt;

    @BindView(R.id.chat_recycler_view)
    RecyclerView mChatRecyclerView;

    @BindView(R.id.header_lay)
    RelativeLayout mHeaderLay;

    @BindView(R.id.header_right_img_lay)
    RelativeLayout mHeaderRightImgLay;

    @BindView(R.id.header_right_img)
    ImageView mHeaderRightImg;

    private LinearLayoutManager mLayoutManager;


    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    private ArrayList<WithEntity> mWithEntityList = new ArrayList<>();

    private GroupChannelListQuery mChannelListQuery;

    private ChatListAdapter mChatListAdapter;


    private static final int CHANNEL_LIST_LIMIT = 15;
    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHANNEL_LIST";
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";

    private String mUserIDStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_chat_screen);
        initView();
    }

    private void initView() {

        ButterKnife.bind(this);

        setHeaderView();

        setupUI(mChatParLay);

        // callChatListApi();


        Gson gson = new Gson();
        String json = PreferenceUtil.getStringValue(this, AppConstants.USER_DETAILS);
        UserDetailsEntity mUserDetailsEntityRes = gson.fromJson(json, UserDetailsEntity.class);

        if (mUserDetailsEntityRes != null)
            AppConstants.ACCESS_TOKEN = mUserDetailsEntityRes.getAuthorizationToken();
        mUserIDStr = mUserDetailsEntityRes.getUserId();

        mChatListAdapter = new ChatListAdapter(this, mUserIDStr);
        mChatListAdapter.load();

        setUpRecyclerView();
        setUpChannelListAdapter();

        trackScreenName(getString(R.string.chat_list_screen));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecylcerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mChatRecyclerView);


        // making http call and fetching menu json


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(mChatRecyclerView);

    }



    private void setHeaderView() {

        /*Set header adjustment - status bar we applied transparent color so header tack full view*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mHeaderLay.post(() -> mHeaderLay.setPadding(0, getStatusBarHeight(ChatListActivityScreen.this), 0, 0));
        }
        mHeaderRightImgLay.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.plus_icon).mutate();
        drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mHeaderRightImg.setImageDrawable(drawable);
        mHeaderTxt.setVisibility(View.VISIBLE);
        mHeaderTxt.setText(getResources().getText(R.string.messages));


    }

    @OnClick({R.id.header_left_img_lay,R.id.header_right_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left_img_lay:
                onBackPressed();
                break;
            case R.id.header_right_img:
                nextScreen(NewMessageScreen.class, true);
                break;
        }
    }

    /*Chat List Api Call*/
    private void callChatListApi() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String url = AppConstants.BASE_URL + AppConstants.CHAT_LIST_URL;
            APIRequestHandler.getInstance().userChatApiCall(url, ChatListActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    callChatListApi();
                }
            });
        }
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof UserChatResponse) {
            UserChatResponse mResponse = (UserChatResponse) resObj;
            if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                mWithEntityList = mResponse.getWith();
//                setAdapter(mWithEntityList);
            }

        }
    }


    // Sets up recycler view
    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mChatRecyclerView.setLayoutManager(mLayoutManager);
        mChatRecyclerView.setAdapter(mChatListAdapter);
        mChatRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // If user scrolls to bottom of the list, loads more channels.
        mChatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChatListAdapter.getItemCount() - 1) {
                    loadNextChannelList();
                }
            }
        });

    }

    // Sets up channel list adapter
    private void setUpChannelListAdapter() {
        mChatListAdapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupChannel channel) {
                enterGroupChannel(channel);
            }
        });
//        //
//        mChatListAdapter.setOnItemLongClickListener(new GroupChannelListAdapter.OnItemLongClickListener() {
//            @Override
//            public void onItemLongClick(final GroupChannel channel) {
//                showChannelOptionsDialog(channel);
//            }
//        });
    }


    @Override
    public void onBackPressed() {
        backScreen(true);
    }


    @Override
    protected void onResume() {

        ConnectionManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID, mUserIDStr,
                new ConnectionManager.ConnectionManagementHandler() {
                    @Override
                    public void onConnected(boolean reconnect) {
                        refresh();
                    }
                });

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
            }

            @Override
            public void onChannelChanged(BaseChannel channel) {
                mChatListAdapter.clearMap();
                mChatListAdapter.updateOrInsert(channel);
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel channel) {
                mChatListAdapter.notifyDataSetChanged();
            }
        });


        if (AppConstants.FROM_NOTIFICATION.equals(AppConstants.KEY_TRUE)) {
            AppConstants.FROM_NOTIFICATION = AppConstants.KEY_FALSE;
            nextScreen(ChatConversationActivityScreen.class, true);
        }

        super.onResume();
    }


    private void refresh() {
        refreshChannelList(CHANNEL_LIST_LIMIT);
    }

    /**
     * Creates a new query to get the list of the user's Group Channels,
     * then replaces the existing dataset.
     *
     * @param numChannels The number of channels to load.
     */
    private void refreshChannelList(int numChannels) {
        DialogManager.getInstance().showProgress(this);

        mChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
        mChannelListQuery.setLimit(numChannels);

        mChannelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                DialogManager.getInstance().hideProgress();

                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }

                int count = 0;
                /*check the unreadmsg count*/
                for (int i = 0; i < list.size(); i++) {
                    count = count + list.get(i).getUnreadMessageCount();
                }
                PreferenceUtil.storeStringValue(ChatListActivityScreen.this,
                        AppConstants.UNREAD_MESSAGE_COUNT, String.valueOf(count));


                mChatListAdapter.clearMap();
                mChatListAdapter.setGroupChannelList(list);
                mChatRecyclerView.setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);
                mNoValueTxt.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);

            }
        });

//        if (mSwipeRefresh.isRefreshing()) {
//            mSwipeRefresh.setRefreshing(false);
//        }
    }


    @Override
    public void onPause() {
        mChatListAdapter.save();

        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
    }


    /**
     * Enters a Group Channel. Upon entering, a GroupChatFragment will be inflated
     * to display messages within the channel.
     *
     * @param channel The Group Channel to enter.
     */
    void enterGroupChannel(GroupChannel channel) {
        String channelUrl = channel.getUrl();

        for (int i = 0; i < channel.getMembers().size(); i++) {
            if (!mUserIDStr.equalsIgnoreCase(channel.getMembers().get(i).getUserId())) {
                AppConstants.SEND_BIRD_USER_NAME = channel.getMembers().get(i).getNickname();
                AppConstants.SEND_BIRD_USER_IMAGE = channel.getMembers().get(i).getProfileUrl();
                break;
            }
        }

        enterGroupChannel(channelUrl);
    }


    /**
     * Enters a Group Channel with a URL.
     *
     * @param channelUrl The URL of the channel to enter.
     */
    void enterGroupChannel(String channelUrl) {
        AppConstants.SEND_BIRD_CHANNER_URL = channelUrl;
        nextScreen(ChatConversationActivityScreen.class, true);
    }


    /**
     * Loads the next channels from the current query instance.
     */
    private void loadNextChannelList() {
        mChannelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }

                for (GroupChannel channel : list) {
                    mChatListAdapter.addLast(channel);
                }
            }
        });
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ChatListAdapter.Holder) {
            // get the removed item name to display it in snack bar
            String name = mWithEntityList.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final WithEntity deletedItem = mWithEntityList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mChatListAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(mChatParLay,name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
