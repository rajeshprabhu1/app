package com.ouam.app.adapter;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ouam.app.R;
import com.ouam.app.utils.DateUtil;
import com.ouam.app.utils.FileUtils;
import com.ouam.app.utils.RoundedImageView;
import com.ouam.app.utils.TextUtils;
import com.ouam.app.utils.TypingIndicator;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.UserMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<GroupChannel> mChannelList;

    private ConcurrentHashMap<SimpleTarget<Bitmap>, Integer> mSimpleTargetIndexMap;
    private ConcurrentHashMap<SimpleTarget<Bitmap>, GroupChannel> mSimpleTargetGroupChannelMap;
    private ConcurrentHashMap<String, Integer> mChannelImageNumMap;
    private ConcurrentHashMap<String, ImageView> mChannelImageViewMap;
    private ConcurrentHashMap<String, SparseArray<Bitmap>> mChannelBitmapMap;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    private String mUserIDStr;

    public interface OnItemClickListener {
        void onItemClick(GroupChannel channel);
    }

    interface OnItemLongClickListener {
        void onItemLongClick(GroupChannel channel);
    }

    public ChatListAdapter(Context activity, String mUserIDStr) {
        mContext = activity;
        this.mUserIDStr = mUserIDStr;
        mSimpleTargetIndexMap = new ConcurrentHashMap<>();
        mSimpleTargetGroupChannelMap = new ConcurrentHashMap<>();
        mChannelImageNumMap = new ConcurrentHashMap<>();
        mChannelImageViewMap = new ConcurrentHashMap<>();
        mChannelBitmapMap = new ConcurrentHashMap<>();

        mChannelList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_list1_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((Holder) holder).bind(mContext, mChannelList.get(position), mItemClickListener, mItemLongClickListener);

    }


    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_group_channel_list_unread_count)
        TextView mUnreadCountText;

        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.date_txt)
        TextView mDateTxt;

        @BindView(R.id.container_group_channel_list_typing_indicator)
        LinearLayout mTypingIndicatorContainer;

        @BindView(R.id.text_group_channel_list_message)
        TextView mLastMessageText;

        @BindView(R.id.user_img)
        RoundedImageView mUserImage;

        @BindView(R.id.view_background)
        public RelativeLayout mviewback;

        @BindView(R.id.view_foreground)
        public RelativeLayout mviewfore;


        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }


        /**
         * Binds views in the ViewHolder to information contained within the Group Channel.
         *
         * @param context
         * @param channel
         * @param clickListener     A listener that handles simple clicks.
         * @param longClickListener A listener that handles long clicks.
         */
        void bind(final Context context, final GroupChannel channel,
                  @Nullable final OnItemClickListener clickListener,
                  @Nullable final OnItemLongClickListener longClickListener) {
            mUserNameTxt.setText(TextUtils.getGroupChannelTitle(channel));
//            memberCountText.setText(String.valueOf(channel.getMemberCount()));

//            setChannelImage(context, channel, coverImage);
            if (channel.getMembers().size() > 1) {
                Glide.with(mContext)
                        .load(!mUserIDStr.equalsIgnoreCase(channel.getMembers().get(0).getUserId())
                                ? channel.getMembers().get(0).getProfileUrl() : channel.getMembers().get(1).getProfileUrl())
                        .apply(new RequestOptions().error(R.drawable.profile_bg).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(mUserImage);
            }


            int unreadCount = channel.getUnreadMessageCount();
            // If there are no unread messages, hide the unread count badge.
            if (unreadCount == 0) {
                mUnreadCountText.setVisibility(View.INVISIBLE);
            } else {
                mUnreadCountText.setVisibility(View.VISIBLE);
                mUnreadCountText.setText(String.valueOf(channel.getUnreadMessageCount()));
            }

            BaseMessage lastMessage = channel.getLastMessage();
            if (lastMessage != null) {
                mDateTxt.setVisibility(View.VISIBLE);
                mLastMessageText.setVisibility(View.VISIBLE);

                // Display information about the most recently sent message in the channel.
                mDateTxt.setText(String.valueOf(DateUtil.formatDateTime(lastMessage.getCreatedAt())));

                // Bind last message text according to the type of message. Specifically, if
                // the last message is a File Message, there must be special formatting.
                if (lastMessage instanceof UserMessage) {
                    mLastMessageText.setText(((UserMessage) lastMessage).getMessage());
                } else if (lastMessage instanceof AdminMessage) {
                    mLastMessageText.setText(((AdminMessage) lastMessage).getMessage());
                } else {
                    String lastMessageString = String.format(
                            context.getString(R.string.group_channel_list_file_message_text),
                            ((FileMessage) lastMessage).getSender().getNickname());
                    mLastMessageText.setText(lastMessageString);
                }
            } else {
                mDateTxt.setVisibility(View.INVISIBLE);
                mLastMessageText.setVisibility(View.INVISIBLE);
            }

            /*
             * Set up the typing indicator.
             * A typing indicator is basically just three dots contained within the layout
             * that animates. The animation is implemented in the {@link TypingIndicator#animate() class}
             */
            ArrayList<ImageView> indicatorImages = new ArrayList<>();
            indicatorImages.add((ImageView) mTypingIndicatorContainer.findViewById(R.id.typing_indicator_dot_1));
            indicatorImages.add((ImageView) mTypingIndicatorContainer.findViewById(R.id.typing_indicator_dot_2));
            indicatorImages.add((ImageView) mTypingIndicatorContainer.findViewById(R.id.typing_indicator_dot_3));

            TypingIndicator indicator = new TypingIndicator(indicatorImages, 600);
            indicator.animate();

            // debug
//            typingIndicatorContainer.setVisibility(View.VISIBLE);
//            lastMessageText.setText(("Someone is typing"));

            // If someone in the channel is typing, display the typing indicator.
            if (channel.isTyping()) {
                mTypingIndicatorContainer.setVisibility(View.VISIBLE);
                mLastMessageText.setText(("Someone is typing"));
            } else {
                // Display typing indicator only when someone is typing
                mTypingIndicatorContainer.setVisibility(View.GONE);
            }

            // Set an OnClickListener to this item.
            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(channel);
                    }
                });
            }

            // Set an OnLongClickListener to this item.
            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onItemLongClick(channel);
                        // return true if the callback consumed the long click
                        return true;
                    }
                });
            }
        }


    }

    public void clearMap() {
        mSimpleTargetIndexMap.clear();
        mSimpleTargetGroupChannelMap.clear();
        mChannelImageNumMap.clear();
        mChannelImageViewMap.clear();
        mChannelBitmapMap.clear();
    }

    public void setGroupChannelList(List<GroupChannel> channelList) {
        mChannelList = channelList;
        notifyDataSetChanged();
    }

    // If the channel is not in the list yet, adds it.
    // If it is, finds the channel in current list, and replaces it.
    // Moves the updated channel to the front of the list.
    public void updateOrInsert(BaseChannel channel) {
        if (!(channel instanceof GroupChannel)) {
            return;
        }

        GroupChannel groupChannel = (GroupChannel) channel;

        for (int i = 0; i < mChannelList.size(); i++) {
            if (mChannelList.get(i).getUrl().equals(groupChannel.getUrl())) {
                mChannelList.remove(mChannelList.get(i));
                mChannelList.add(0, groupChannel);
                notifyDataSetChanged();
                Log.v(ChatListAdapter.class.getSimpleName(), "Channel replaced.");
                return;
            }
        }
        ;
        mChannelList.add(0, groupChannel);
        notifyDataSetChanged();
    }

    public void Delete(BaseChannel channel) {
        if (!(channel instanceof GroupChannel)) {
            return;
        }

        GroupChannel groupChannel = (GroupChannel) channel;
//

//        groupChannel.deleteMessage(BASE_MESSAGE, new BaseChannel.DeleteMessageHandler() {
//            @Override
//            public void onResult(SendBirdException e) {
//                if (e != null) {    // Error.
//                    return;
//                }
//            }
//        });

        mChannelList.remove(channel);
        notifyDataSetChanged();
    }

    public void load() {
        try {
            File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
            appDir.mkdirs();

            File dataFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");

            String content = FileUtils.loadFromFile(dataFile);
            String[] dataArray = content.split("\n");

            // Reset channel list, then add cached data.
            mChannelList.clear();
            for (int i = 0; i < dataArray.length; i++) {
                mChannelList.add((GroupChannel) BaseChannel.buildFromSerializedData(Base64.decode(dataArray[i], Base64.DEFAULT | Base64.NO_WRAP)));
            }

            notifyDataSetChanged();
        } catch (Exception e) {
            // Nothing to load.
        }
    }


    public void save() {
        try {
            StringBuilder sb = new StringBuilder();

            // Save the data into file.
            File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
            appDir.mkdirs();

            File hashFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".hash");
            File dataFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");

            if (mChannelList != null && mChannelList.size() > 0) {
                // Convert current data into string.
                GroupChannel channel = null;
                for (int i = 0; i < Math.min(mChannelList.size(), 100); i++) {
                    channel = mChannelList.get(i);
                    sb.append("\n");
                    sb.append(Base64.encodeToString(channel.serialize(), Base64.DEFAULT | Base64.NO_WRAP));
                }
                // Remove first newline.
                sb.delete(0, 1);

                String data = sb.toString();
                String md5 = TextUtils.generateMD5(data);

                try {
                    String content = FileUtils.loadFromFile(hashFile);
                    // If data has not been changed, do not save.
                    if (md5.equals(content)) {
                        return;
                    }
                } catch (IOException e) {
                    // File not found. Save the data.
                }

                FileUtils.saveToFile(dataFile, data);
                FileUtils.saveToFile(hashFile, md5);
            } else {
                FileUtils.deleteFile(dataFile);
                FileUtils.deleteFile(hashFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLast(GroupChannel channel) {
        mChannelList.add(channel);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {

        mChannelList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(ClipData.Item item, int position) {

        // notify item added by position
        notifyItemInserted(position);
    }
}