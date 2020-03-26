package es.unavarra.tlm.dscr_19_11.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.unavarra.tlm.dscr_19_11.Entities.Access;
import es.unavarra.tlm.dscr_19_11.Entities.AccessDao;
import es.unavarra.tlm.dscr_19_11.Entities.DaoMaster;
import es.unavarra.tlm.dscr_19_11.Entities.DaoSession;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.Chat;
import es.unavarra.tlm.dscr_19_11.R;

public class ChatsListAdapter extends BaseAdapter {

    private List<Chat> chats;
    private ArrayList<Chat> chatsArrayList;
    private Context context;
    private AccessDao accessDao;

    public ChatsListAdapter(List<Chat> chats, Context context,  AccessDao accessDao) {
        this.chats = chats;
        this.context = context;
        this.accessDao = accessDao;
        this.chatsArrayList = new ArrayList<>();
        this.chatsArrayList.addAll(chats);
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Chat getItem (int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View chats = layoutInflater.inflate(R.layout.activity_chat_listview, null);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "access_db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        accessDao = daoSession.getAccessDao();

        TextView chatUser = chats.findViewById(R.id.chatUser);
        TextView chatLastMessage = chats.findViewById(R.id.chatLastMessage);
        TextView mensajesNoLeidos = chats.findViewById(R.id.mensajesNoLeidos);



        Chat chatRecord = getItem(position);

        chats.setId(chatRecord.getId());





        String username = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("username", null);

        if (chatRecord.getUsers().get(0).getName().equals(username)) {
            chatUser.setText(String.format(Locale.getDefault(),"User: %1$s", chatRecord.getUsers().get(1).getName()));
        } else {
            chatUser.setText(String.format(Locale.getDefault(),"User: %1$s", chatRecord.getUsers().get(0).getName()));
        }
        chatLastMessage.setText(String.format(Locale.getDefault(), "Last msg: %1$s", escribirLastMsg(chatRecord, accessDao)));
        mensajesNoLeidos.setText(String.format(Locale.getDefault(), "Mensajes no leidos: %1$d", calcularMensajes(chatRecord, accessDao)));

        if(calcularMensajes(chatRecord, accessDao)>=1){


            chats.setBackgroundColor(context.getColor(R.color.colorPrimary));

        }

        return chats;
    }

    private String escribirLastMsg( Chat chatRecord,AccessDao accessDao){


        List<Access> list =  accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(chatRecord.getId())).list();
        String lastMsg;
        if(list.size()>0){
            lastMsg = list.get(0).getLast_msg();
        }else{
            lastMsg = " ";
        }

        return  lastMsg;
    }

    private int calcularMensajes(Chat chatRecord,AccessDao accessDao){


        List<Access> list =  accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(chatRecord.getId())).list();
        int numero = 0;
        if(list.size()>0){
            numero = list.get(0).getNew_num()-list.get(0).getLast_num();
        }

        return numero;
    }

    public void filter (String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        chats.clear();
        String username = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("username", null);
        if (charText.length() == 0) {
            chats.addAll(chatsArrayList);
        } else {
            for (Chat wp : chatsArrayList) {
                if (wp.getUsers().get(0).getName().toLowerCase(Locale.getDefault()).contains(charText) && !wp.getUsers().get(0).getName().equals(username)) {
                    chats.add(wp);
                } else if (wp.getUsers().get(1).getName().toLowerCase(Locale.getDefault()).contains(charText) && !wp.getUsers().get(1).getName().equals(username)) {
                    chats.add(wp);
                }
            }
        }

        notifyDataSetChanged();

    }


}