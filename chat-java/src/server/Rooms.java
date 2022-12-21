package server;

import client.ChatClientIF;

import java.util.Vector;
public class Rooms {

    public String name;
    public String username;
    public String password;
    public ChatClientIF room;
    public Vector<Chatter> chatters;
    //constructor
    public Rooms(String name,String userName,String pass, ChatClientIF client){
        this.name = name;
        this.room = client;
        this.username=userName;
        this.password=pass;
        this.chatters= new Vector<Chatter>(10, 1);
    }


    //getters and setters
    public String getName(){
        return name;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){return password;}
    public ChatClientIF getRoom(){
        return room;
    }
    public Vector<Chatter> getClients(){
        return chatters;
    }
}
