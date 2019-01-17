package com.ouam.app.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.ouam.app.R;
import com.ouam.app.adapter.GroupChatAdapter;
import com.ouam.app.chat.ConnectionManager;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.FileUtils;
import com.ouam.app.utils.PreferenceUtil;
import com.ouam.app.utils.RoundedImageView;
import com.ouam.app.utils.UrlPreviewInfo;
import com.ouam.app.utils.WebUtils;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatConversationActivityScreen extends BaseActivity {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_EDIT = 1;

    private static final int CHANNEL_LIST_LIMIT = 30;
    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT";
    private static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;


    @BindView(R.id.layout_group_chat_root)
    RelativeLayout mRootLayout;

    @BindView(R.id.user_info_lay)
    RelativeLayout mUserInfoLay;

    @BindView(R.id.button_group_chat_upload)
    ImageView mUploadFileImage;

    @BindView(R.id.recycler_group_chat)
    RecyclerView mChatRecyclerView;

    @BindView(R.id.edittext_group_chat_message)
    EditText mMessageEditText;

    @BindView(R.id.layout_group_chat_current_event)
    LinearLayout mCurretntEventLay;

    @BindView(R.id.text_group_chat_current_event)
    TextView mCurrentEventTxt;

    @BindView(R.id.button_group_chat_send)
    ImageView mMessageSendImg;

    @BindView(R.id.user_img)
    RoundedImageView mUserImage;

    @BindView(R.id.user_name_txt)
    TextView mUserNameTxt;

    private GroupChannel mChannel;

    private boolean mIsTyping;

    private int mCurrentState = STATE_NORMAL;
    private BaseMessage mEditingMessage = null;


    private GroupChatAdapter mConversationAdapter;

    private LinearLayoutManager mLayoutManager;


    private InputMethodManager mIMM;
    private HashMap<BaseChannel.SendFileMessageWithProgressHandler, FileMessage> mFileProgressHandlerMap;

    private String mUserIDStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_conversation_chat_screen);
        initView();
    }


    /*InitViews*/
    private void initView() {

        ButterKnife.bind(this);

        mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mFileProgressHandlerMap = new HashMap<>();


        mConversationAdapter = new GroupChatAdapter(this);
        setUpChatListAdapter();

        // Load messages from cache.
        mConversationAdapter.load(AppConstants.SEND_BIRD_CHANNER_URL);

        setUpRecyclerView();


        mIsTyping = false;
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mIsTyping) {
                    setTypingStatus(true);
                }

                if (s.length() == 0) {
                    setTypingStatus(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        Gson gson = new Gson();
        String json = PreferenceUtil.getStringValue(this, AppConstants.USER_DETAILS);
        UserDetailsEntity mUserDetailsEntityRes = gson.fromJson(json, UserDetailsEntity.class);

        if (mUserDetailsEntityRes != null) {
            mUserIDStr = mUserDetailsEntityRes.getUserId();
        }


        Glide.with(this)
                .load(AppConstants.SEND_BIRD_USER_IMAGE)
                .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).
                        diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .into(mUserImage);
        mUserNameTxt.setText(AppConstants.SEND_BIRD_USER_NAME);

        trackScreenName(getString(R.string.chat_conversation_screen));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mUserInfoLay.post(new Runnable() {
                public void run() {
                    mUserInfoLay.setPadding(0, getStatusBarHeight(ChatConversationActivityScreen.this), 0, 0);

                }
            });
        }
    }


    /**
     * Notify other users whether the current user is typing.
     *
     * @param typing Whether the user is currently typing.
     */
    private void setTypingStatus(boolean typing) {
        if (mChannel == null) {
            return;
        }

        if (typing) {
            mIsTyping = true;
            mChannel.startTyping();
        } else {
            mIsTyping = false;
            mChannel.endTyping();
        }
    }


    private void setUpChatListAdapter() {
        mConversationAdapter.setItemClickListener(new GroupChatAdapter.OnItemClickListener() {
            @Override
            public void onUserMessageItemClick(UserMessage message) {
                // Restore failed message and remove the failed message from list.
                if (mConversationAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mConversationAdapter.isTempMessage(message)) {
                    return;
                }


                if (message.getCustomType().equals(GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE)) {
                    try {
                        UrlPreviewInfo info = new UrlPreviewInfo(message.getData());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getUrl()));
                        startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFileMessageItemClick(FileMessage message) {
                // Load media chooser and remove the failed message from list.
                if (mConversationAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mConversationAdapter.isTempMessage(message)) {
                    return;
                }


                // onFileMessageClicked(message);
            }
        });


        mConversationAdapter.setItemLongClickListener(new GroupChatAdapter.OnItemLongClickListener(){
            @Override
            public void onUserMessageItemLongClick(UserMessage message, int position) {
                showMessageOptionsDialog(message, position);
            }

            @Override
            public void onFileMessageItemLongClick(FileMessage message, int position) {
                showMessageOptionsDialog(message, position);
            }



            @Override
            public void onAdminMessageItemLongClick(AdminMessage message) {
            }
        });
    }


    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mChatRecyclerView.setLayoutManager(mLayoutManager);
        mChatRecyclerView.setAdapter(mConversationAdapter);
        mChatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mConversationAdapter.getItemCount() - 1) {
                    mConversationAdapter.loadPreviousMessages(CHANNEL_LIST_LIMIT, null);
                }
            }
        });
    }

    private void editMessage(final BaseMessage message, String editedMessage) {
        mChannel.updateUserMessage(message.getMessageId(), editedMessage, null, null, new BaseChannel.UpdateUserMessageHandler() {
            @Override
            public void onUpdated(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    DialogManager.getInstance().showToast(getApplicationContext(), "Error " + e.getCode() + ": " + e.getMessage());
                    return;
                }

                mConversationAdapter.loadLatestMessages(CHANNEL_LIST_LIMIT, new BaseChannel.GetMessagesHandler() {
                    @Override
                    public void onResult(List<BaseMessage> list, SendBirdException e) {
                        mConversationAdapter.markAllMessagesAsRead();
                    }
                });
            }
        });
    }


    /**
     * Deletes a message within the channel.
     * Note that users can only delete messages sent by oneself.
     *
     * @param message The message to delete.
     */
    private void deleteMessage(final BaseMessage message ) {
        mChannel.deleteMessage(message, new BaseChannel.DeleteMessageHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    // Error!
                    DialogManager.getInstance().showToast(getApplicationContext(), "Error " + e.getCode() + ": " + e.getMessage());
                    return;
                }

                mConversationAdapter.loadLatestMessages(CHANNEL_LIST_LIMIT, new BaseChannel.GetMessagesHandler() {
                    @Override
                    public void onResult(List<BaseMessage> list, SendBirdException e) {
                        mConversationAdapter.markAllMessagesAsRead();
                    }
                });
            }
        });
    }


    private void showMessageOptionsDialog(final BaseMessage message, final int position) {
//        String[] options = new String[]{"Edit message", "Delete message"};
        String[] options = new String[]{"Delete message"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0) {
//                    setState(STATE_EDIT, message, position);
//                } else if (which == 1) {
                deleteMessage(message);
//                }
            }
        });
        builder.create().show();
    }


    private void setState(int state, BaseMessage editingMessage, final int position) {
        switch (state) {
            case STATE_NORMAL:
                mCurrentState = STATE_NORMAL;
                mEditingMessage = null;

                mUploadFileImage.setVisibility(View.VISIBLE);
                mMessageEditText.setText("");
                break;

            case STATE_EDIT:
                mCurrentState = STATE_EDIT;
                mEditingMessage = editingMessage;

                mUploadFileImage.setVisibility(View.GONE);
                String messageString = ((UserMessage) editingMessage).getMessage();
                if (messageString == null) {
                    messageString = "";
                }
                mMessageEditText.setText(messageString);
                if (messageString.length() > 0) {
                    mMessageEditText.setSelection(0, messageString.length());
                }

                mMessageEditText.requestFocus();
                mMessageEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIMM.showSoftInput(mMessageEditText, 0);

                        mChatRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mChatRecyclerView.scrollToPosition(position);
                            }
                        }, 500);
                    }
                }, 100);
                break;
        }
    }

    private void sendUserMessage(String text) {
        List<String> urls = WebUtils.extractUrls(text);
        if (urls.size() > 0) {
            sendUserMessageWithUrl(text, urls.get(0));
            return;
        }

        UserMessage tempUserMessage = mChannel.sendUserMessage(text, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    DialogManager.getInstance().showToast(getApplicationContext(), "Send failed with error " + e.getCode() + ": " + e.getMessage());
                    mConversationAdapter.markMessageFailed(userMessage.getRequestId());
                    return;
                }

                // Update a sent message to RecyclerView
                mConversationAdapter.markMessageSent(userMessage);
            }
        });

        // Display a user message to RecyclerView
        mConversationAdapter.addFirst(tempUserMessage);
    }


    private void sendUserMessageWithUrl(final String text, String url) {
        new WebUtils.UrlPreviewAsyncTask() {
            @Override
            protected void onPostExecute(UrlPreviewInfo info) {
                UserMessage tempUserMessage = null;
                BaseChannel.SendUserMessageHandler handler = new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            // Error!
//                            Log.e(LOG_TAG, e.toString());
//                            Toast.makeText(
//                                    getActivity(),
//                                    "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
//                                    .show();

                            DialogManager.getInstance().showToast(getApplicationContext(),
                                    "Send failed with error " + e.getCode() + ": " + e.getMessage());


                            mConversationAdapter.markMessageFailed(userMessage.getRequestId());
                            return;
                        }

                        // Update a sent message to RecyclerView
                        mConversationAdapter.markMessageSent(userMessage);
                    }
                };

                try {
                    // Sending a message with URL preview information and custom type.
                    String jsonString = info.toJsonString();
                    tempUserMessage = mChannel.sendUserMessage(text, jsonString, GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE, handler);
                } catch (Exception e) {
                    // Sending a message without URL preview information.
                    tempUserMessage = mChannel.sendUserMessage(text, handler);
                }


                // Display a user message to RecyclerView
                mConversationAdapter.addFirst(tempUserMessage);
            }
        }.execute(url);
    }

    private void retryFailedMessage(final BaseMessage message) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.retry_txt))
                .setPositiveButton(R.string.resend_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (message instanceof UserMessage) {
                                String userInput = ((UserMessage) message).getMessage();
                                sendUserMessage(userInput);
                            } else if (message instanceof FileMessage) {
                                Uri uri = mConversationAdapter.getTempFileMessageUri(message);
                                sendFileWithThumbnail(uri);
                            }
                            mConversationAdapter.removeFailedMessage(message);
                        }
                    }
                })
                .setNegativeButton(R.string.delete_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            mConversationAdapter.removeFailedMessage(message);
                        }
                    }
                }).show();
    }


    /**
     * Sends a File Message containing an image file.
     * Also requests thumbnails to be generated in specified sizes.
     *
     * @param uri The URI of the image, which in this case is received through an Intent request.
     */
    private void sendFileWithThumbnail(Uri uri) {
        // Specify two dimensions of thumbnails to generate
        List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
        thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
        thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

        Hashtable<String, Object> info = FileUtils.getFileInfo(this, uri);

        if (info == null) {
            DialogManager.getInstance().showToast(this, getString(R.string.extract_file_failed));
            return;
        }

        final String path = (String) info.get("path");
        final File file = new File(path);
        final String name = file.getName();
        final String mime = (String) info.get("mime");
        final int size = (Integer) info.get("size");

        if (path.equals("")) {
            DialogManager.getInstance().showToast(this, getString(R.string.file_must_be_local_storage));
        } else {
            BaseChannel.SendFileMessageWithProgressHandler progressHandler = new BaseChannel.SendFileMessageWithProgressHandler() {
                @Override
                public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
                    FileMessage fileMessage = mFileProgressHandlerMap.get(this);
                    if (fileMessage != null && totalBytesToSend > 0) {
                        int percent = (totalBytesSent * 100) / totalBytesToSend;
                        mConversationAdapter.setFileProgressPercent(fileMessage, percent);
                    }
                }

                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    if (e != null) {
                        DialogManager.getInstance().showToast(getApplicationContext(),
                                e.getCode() + ":" + e.getMessage());

                        mConversationAdapter.markMessageFailed(fileMessage.getRequestId());
                        return;
                    }

                    mConversationAdapter.markMessageSent(fileMessage);
                }
            };

            // Send image with thumbnails in the specified dimensions
            FileMessage tempFileMessage = mChannel.sendFileMessage(file, name, mime, size, "", null, thumbnailSizes, progressHandler);

            mFileProgressHandlerMap.put(progressHandler, tempFileMessage);

            mConversationAdapter.addTempFileMessageInfo(tempFileMessage, uri);
            mConversationAdapter.addFirst(tempFileMessage);
        }
    }

//    private void onFileMessageClicked(FileMessage message) {
//        String type = message.getType().toLowerCase();
//        if (type.startsWith("image")) {
//            Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
//            i.putExtra("url", message.getUrl());
//            i.putExtra("type", message.getType());
//            startActivity(i);
//        } else if (type.startsWith("video")) {
//            Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
//            intent.putExtra("url", message.getUrl());
//            startActivity(intent);
//        } else {
//            showDownloadConfirmDialog(message);
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Set this as true to restore background connection management.
        SendBird.setAutoBackgroundDetection(true);

        if (requestCode == INTENT_REQUEST_CHOOSE_MEDIA && resultCode == Activity.RESULT_OK) {
            // If user has successfully chosen the image, show a dialog to confirm upload.
            if (data == null) {
                Log.d("", "data is null!");
                return;
            }

            sendFileWithThumbnail(data.getData());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.button_group_chat_send, R.id.button_group_chat_upload, R.id.header_left_first_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_group_chat_send:
                if (mCurrentState == STATE_EDIT) {
                    String userInput = mMessageEditText.getText().toString().trim();
                    if (userInput.length() > 0) {
                        if (mEditingMessage != null) {
                            editMessage(mEditingMessage, userInput);
                        }
                    } else {
                        DialogManager.getInstance().showToast(this, getString(R.string.please_enter_message));
                    }
                    setState(STATE_NORMAL, null, -1);
                } else {
                    String userInput = mMessageEditText.getText().toString().trim();
                    if (userInput.length() > 0) {
                        sendUserMessage(userInput);
                        mMessageEditText.setText("");
                    } else {
                        DialogManager.getInstance().showToast(this, getString(R.string.please_enter_message));

                    }
                }
                break;
            case R.id.button_group_chat_upload:
                requestMedia();
                break;
            case R.id.header_left_first_img:
                onBackPressed();
                break;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMedia() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            Intent intent = new Intent();

            // Pick images or videos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType("*/*");
                String[] mimeTypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            } else {
                intent.setType("image/* video/*");
            }

            intent.setAction(Intent.ACTION_GET_CONTENT);

            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_MEDIA);

            // Set this as false to maintain connection
            // even when an external Activity is started.
            SendBird.setAutoBackgroundDetection(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(mRootLayout, "Storage access permissions are required to upload/download files.",
                    Snackbar.LENGTH_LONG)
                    .setAction("Okay", new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Permission has not been granted yet. Request it directly.
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ConnectionManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID, mUserIDStr,
                new ConnectionManager.ConnectionManagementHandler() {
                    @Override
                    public void onConnected(boolean reconnect) {
                        refresh();
                    }
                });

        mConversationAdapter.setContext(this); // Glide bug fix (java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity)

        // Gets channel from URL user requested

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                if (baseChannel.getUrl().equals(AppConstants.SEND_BIRD_CHANNER_URL)) {
                    mConversationAdapter.markAllMessagesAsRead();
                    // Add new message to view
                    mConversationAdapter.addFirst(baseMessage);
                }
            }

            @Override
            public void onMessageDeleted(BaseChannel baseChannel, long msgId) {
                super.onMessageDeleted(baseChannel, msgId);
                if (baseChannel.getUrl().equals(AppConstants.SEND_BIRD_CHANNER_URL)) {
                    mConversationAdapter.delete(msgId);
                }
            }

            @Override
            public void onMessageUpdated(BaseChannel channel, BaseMessage message) {
                super.onMessageUpdated(channel, message);
                if (channel.getUrl().equals(AppConstants.SEND_BIRD_CHANNER_URL)) {
                    mConversationAdapter.update(message);
                }
            }

            @Override
            public void onReadReceiptUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(AppConstants.SEND_BIRD_CHANNER_URL)) {
                    mConversationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(AppConstants.SEND_BIRD_CHANNER_URL)) {
                    List<Member> typingUsers = channel.getTypingMembers();
                    displayTyping(typingUsers);
                }
            }

        });
    }


    private void refresh() {
        if (mChannel == null) {
            GroupChannel.getChannel(AppConstants.SEND_BIRD_CHANNER_URL, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    mChannel = groupChannel;
                    mConversationAdapter.setChannel(mChannel);
                    mConversationAdapter.loadLatestMessages(CHANNEL_LIST_LIMIT, new BaseChannel.GetMessagesHandler() {
                        @Override
                        public void onResult(List<BaseMessage> list, SendBirdException e) {
                            mConversationAdapter.markAllMessagesAsRead();
                        }
                    });
                }
            });
        } else {
            mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    mConversationAdapter.loadLatestMessages(CHANNEL_LIST_LIMIT, new BaseChannel.GetMessagesHandler() {
                        @Override
                        public void onResult(List<BaseMessage> list, SendBirdException e) {
                            mConversationAdapter.markAllMessagesAsRead();
                        }
                    });
                    // updateActionBarTitle();
                }
            });
        }
    }


    /**
     * Display which users are typing.
     * If more than two users are currently typing, this will state that "multiple users" are typing.
     *
     * @param typingUsers The list of currently typing users.
     */
    private void displayTyping(List<Member> typingUsers) {

        if (typingUsers.size() > 0) {
            mCurretntEventLay.setVisibility(View.VISIBLE);
            String string;

            if (typingUsers.size() == 1) {
                string = typingUsers.get(0).getNickname() + " is typing";
            } else if (typingUsers.size() == 2) {
                string = typingUsers.get(0).getNickname() + " " + typingUsers.get(1).getNickname() + " is typing";
            } else {
                string = "Multiple users are typing";
            }
            mCurrentEventTxt.setText(string);
        } else {
            mCurretntEventLay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        AppConstants.SEND_BIRD_USER_IMAGE = "";
        AppConstants.SEND_BIRD_USER_NAME = "";
        AppConstants.SEND_BIRD_CHANNER_URL = "";
        backScreen(true);


    }


}
