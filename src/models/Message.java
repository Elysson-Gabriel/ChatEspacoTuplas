package models;

import net.jini.core.entry.Entry;

public class Message implements Entry {
    
    public String content;
    public String usuario;
    public String destino;
    public Integer ordem;
    
    public Message() {
    }
    
}
