package es.unavarra.tlm.dscr_19_11.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.unavarra.tlm.dscr_19_11.ObjectsJson.Message;
import es.unavarra.tlm.dscr_19_11.R;

public class MessageListAdapter extends BaseAdapter {

    private List<Message> messages;
    private ArrayList<Message> messagesArrayList;
    private Context context;

    public MessageListAdapter (Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
        this.messagesArrayList = new ArrayList<>();
        this.messagesArrayList.addAll(messages);
    }


    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem (int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View messages = layoutInflater.inflate(R.layout.activity_message_listview, null);

        TextView messageText = messages.findViewById(R.id.messageText);
        TextView messageTime = messages.findViewById(R.id.messageTime);

        Message messageRecord = getItem(position);
        messageText.setText(messageRecord.getText());

        String username = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("username", null);

        if (messageRecord.getUser().getName().equals(username)) {
            messageText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            messageTime.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault()).parse(messageRecord.getReceived_at());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            messageTime.setText(String.format(Locale.getDefault(), "%02d:%02d %02d/%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        messageTime.setTextColor(Color.LTGRAY);

        return messages;
    }

    public void filter (String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        messages.clear();
        if (charText.length() == 0) {
            messages.addAll(messagesArrayList);
        } else {
            for (Message wp : messagesArrayList) {
                if (wp.getText().toLowerCase().contains(charText)) {
                    messages.add(wp);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void addMessage (Message newMessage) {
        messages.add(newMessage);
        notifyDataSetChanged();
    }


}
