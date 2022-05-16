package chatesptup;

import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class ReadMessage {

    public static void main(String[] args) {
        try {
            System.out.println("Procurando pelo servico JavaSpace...");
            Lookup finder = new Lookup(JavaSpace.class);
            JavaSpace space = (JavaSpace) finder.getService();
            if (space == null) {
                    System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
                    System.exit(-1);
            } 
            System.out.println("O servico JavaSpace foi encontrado.");
	    System.out.println(space);

            while (true) {
                Message template = new Message();
                Message msg = (Message) space.take(template, null, Lease.FOREVER);
                if (msg == null) {
                    System.out.println("Tempo de espera esgotado. Encerrando...");
                    System.exit(0);
                }
                System.out.println("Mensagem recebida: "+ msg.content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
