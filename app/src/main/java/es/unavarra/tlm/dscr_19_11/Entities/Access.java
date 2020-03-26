package es.unavarra.tlm.dscr_19_11.Entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Access {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private int chat_id;

    @NotNull
    private int last_num;

    @NotNull
    private int new_num;


    private String last_msg;


    @Generated(hash = 997814398)
    public Access(Long id, int chat_id, int last_num, int new_num,
            String last_msg) {
        this.id = id;
        this.chat_id = chat_id;
        this.last_num = last_num;
        this.new_num = new_num;
        this.last_msg = last_msg;
    }


    @Generated(hash = 1253708747)
    public Access() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public int getChat_id() {
        return this.chat_id;
    }


    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }


    public int getLast_num() {
        return this.last_num;
    }


    public void setLast_num(int last_num) {
        this.last_num = last_num;
    }


    public int getNew_num() {
        return this.new_num;
    }


    public void setNew_num(int new_num) {
        this.new_num = new_num;
    }


    public String getLast_msg() {
        return this.last_msg;
    }


    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }








}