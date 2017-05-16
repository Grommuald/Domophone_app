package com.domophone.domophone;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<Message> {
    private String mUserId;

    public ChatListAdapter(Context context, String userId, List<Message> messages) {
        super(context, 0, messages);
        this.mUserId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.body = (TextView)convertView.findViewById(R.id.tvBody);
            convertView.setTag(holder);
        }
        Message previousMessage = null;
        Message message = getItem(position);
        Message nextMessage = null;
        if (position - 1 >= 0) {
            previousMessage = getItem(position-1);
        }
        if (position + 1 < super.getCount()) {
            nextMessage = getItem(position+1);
        }

        final ViewHolder holder = (ViewHolder)convertView.getTag();
        final boolean isMe = message.getUserId() != null && message.getUserId().equals(mUserId);
        boolean lastMessageOfCurrentUser = false;

        if (isMe) {
            if (previousMessage != null && nextMessage != null) {
                if (!previousMessage.getUserId().equals(mUserId)) {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_right, parent, false);
                } else if (previousMessage.getUserId().equals(mUserId) && nextMessage.getUserId().equals(mUserId)) {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_right_middle, parent, false);
                } else {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_right_end, parent, false);
                }
            } else {
                if (previousMessage == null && nextMessage == null) {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_right, parent, false);
                } else if (previousMessage == null) {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_right, parent, false);
                } else {
                    if (previousMessage.getUserId().equals(mUserId)) {
                        convertView = LayoutInflater.from(getContext()).
                                inflate(R.layout.item_chat_right_end, parent, false);
                    } else {
                        convertView = LayoutInflater.from(getContext()).
                                inflate(R.layout.item_chat_right, parent, false);
                    }
                }
            }
            holder.body = (TextView)convertView.findViewById(R.id.tvBody);
            convertView.setTag(holder);
        } else {
            if (previousMessage != null && nextMessage != null) {
                if (previousMessage.getUserId() != null && !message.getUserId().equals(previousMessage.getUserId())) {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_left, parent, false);
                    holder.username = (TextView) convertView.findViewById(R.id.username);
                    holder.username.setVisibility(View.VISIBLE);
                } else if (previousMessage.getUserId() != null && previousMessage.getUserId().equals(message.getUserId()) &&
                        nextMessage.getUserId() != null && nextMessage.getUserId().equals(message.getUserId())) {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_left_middle, parent, false);
                } else {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_left_end, parent, false);
                }
            } else {
                if (previousMessage == null && nextMessage == null) {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_left, parent, false );
                    holder.username = (TextView) convertView.findViewById(R.id.username);
                    holder.username.setVisibility(View.VISIBLE);
                } else if (previousMessage == null) {
                    convertView = LayoutInflater.from(getContext()).
                            inflate(R.layout.item_chat_left, parent, false);
                    holder.username = (TextView) convertView.findViewById(R.id.username);
                    holder.username.setVisibility(View.VISIBLE);
                } else {
                    if (previousMessage.getUserId().equals(message.getUserId())) {
                        convertView = LayoutInflater.from(getContext()).
                                inflate(R.layout.item_chat_left_end, parent, false);
                    } else {
                        convertView = LayoutInflater.from(getContext()).
                                inflate(R.layout.item_chat_left, parent, false);
                        holder.username = (TextView) convertView.findViewById(R.id.username);
                        holder.username.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (nextMessage != null && !message.getUserId().equals(nextMessage.getUserId())) {
                holder.imageOther = (ImageView)convertView.findViewById(R.id.ivProfileOther);
                holder.imageOther.setVisibility(View.VISIBLE);

            }
            holder.body = (TextView)convertView.findViewById(R.id.tvBody);
            convertView.setTag(holder);
        }

        final ImageView profileView = holder.imageOther;
        if (profileView != null)
            Picasso.with(getContext()).load(getProfileUrl(message.getUserId())).into(profileView);
        holder.body.setText(message.getBody());
        if (holder.username != null) {
            holder.username.setText(message.getUsername());
        }
        return convertView;
    }

    // Create a gravatar image based on the hash value obtained from userId
    private static String getProfileUrl(final String userId) {
        String hex = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] hash = digest.digest(userId.getBytes());
            final BigInteger bigInt = new BigInteger(hash);
            hex = bigInt.abs().toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
    }
    @Override
    public Message getItem(int position) {
        return super.getItem(super.getCount() - position - 1);
    }
    final class ViewHolder {
        public TextView username;
        public ImageView imageMe;
        public ImageView imageOther;
        public TextView body;
    }
}