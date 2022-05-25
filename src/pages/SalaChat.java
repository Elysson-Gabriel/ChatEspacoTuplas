/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pages;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import main.Iniciar;
import models.Message;
import models.Sala;
import models.Usuario;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

/**
 *
 * @author elysson
 */
public class SalaChat extends javax.swing.JFrame {
    
    private JavaSpace space;
    private Usuario usuario;
    private Sala sala;
    private int msgAtualPub;
    private int msgAtualPriv;
    
    /**
     * Creates new form SalasListagem
     */
    public SalaChat() {
        initComponents();
    }
    
    public SalaChat(Usuario usuario, JavaSpace space, Sala sala) {
        initComponents();
        
        this.usuario = usuario;
        this.usuario.sala = sala.nome;
        this.space = space;
        this.sala = sala;
        this.msgAtualPub = this.sala.qtdMsg + 1;
        this.msgAtualPriv = this.usuario.qtdMsg + 1;
        
        jLabelTitulo.setText("USUÁRIO: " + usuario.nome + " - SALA: " + sala.nome);
        this.setTitle("SALA: " + sala.nome + " - USUÁRIO: " + usuario.nome);
        
        AtualizaChatPublico chatPublico = new AtualizaChatPublico();
        chatPublico.start();
        
        AtualizaChatPrivado chatPrivado = new AtualizaChatPrivado();
        chatPrivado.start();
    }
    
    public void enviarMensagemChat(){
        String message = "";
        message = mensagem.getText();
        
        if(message.equals("/lista")){
            exibeUsuarios();
        }else if(message.matches("/.* .*")){
            int idx = message.indexOf(" ");
            String uDestino = message.substring(1, idx);
            String mDestino = message.substring(idx+1, message.length());
            
            enviarMensagemPrivada(uDestino, mDestino);
        }else if(!message.isEmpty()){
            Sala sTemplate = new Sala();
            sTemplate.nome = this.sala.nome;
            Sala s = null;
            try {
                s = (Sala) space.take(sTemplate, null, Lease.FOREVER);
            } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
                Logger.getLogger(SalaCadastro.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (s == null) {
                System.out.println("Tempo de espera esgotado. Encerrando...");
                System.exit(0);
            }
            
            Message msg = new Message();
            msg.content = message;
            msg.usuario = this.usuario.nome;
            s.qtdMsg += 1;
            msg.ordem = s.qtdMsg;
            msg.destino = s.nome;
            this.sala = s;
            
            try {
                this.space.write(msg, null, Lease.FOREVER);
                this.space.write(this.sala, null, Lease.FOREVER);
            } catch (TransactionException | RemoteException ex) {
                Logger.getLogger(SalaChat.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        mensagem.setText("");
    }
    
    public void enviarMensagemPrivada(String uDestino, String mDestino){
        if(!mDestino.isEmpty()){
            Usuario uTemplate = new Usuario();
            uTemplate.nome = uDestino;
            uTemplate.sala = this.sala.nome;
            Usuario u = null;
            try {
                u = (Usuario) space.take(uTemplate, null, 500);
            } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
                Logger.getLogger(SalaCadastro.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Usuário " + uDestino + " não está na sala."
                        + " Utilize o comando /lista no chat para obter os usuários da sala", 
                    "Tente novamente!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Message msg = new Message();
            msg.content = mDestino;
            msg.usuario = this.usuario.nome;
            u.qtdMsg += 1;
            msg.ordem = u.qtdMsg;
            msg.destino = uDestino;
            //this.usuario = u;
            
            try {
                this.space.write(msg, null, Lease.FOREVER);
                this.space.write(u, null, Lease.FOREVER);
                
                chatArea.setText(chatArea.getText() + "\n[Mensagem para usuário " + msg.destino
                        + "]: " + msg.content);
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
                
            } catch (TransactionException | RemoteException ex) {
                Logger.getLogger(SalaChat.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

        this.mensagem.setText("");
    }
    
    public void exibeUsuarios(){
        
        Sala sTemplate = new Sala();
        sTemplate.nome = this.sala.nome;
        Sala s = null;
        
        Usuario uTemplate = new Usuario();
        uTemplate.sala = this.sala.nome;
        Usuario u = null;

        try {
            s = (Sala) space.read(sTemplate, null, Lease.FOREVER);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(SalaCadastro.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.sala = s;

        ArrayList<String> usuarios = new ArrayList<String>();
        ArrayList<Integer> qtdMsgUsu = new ArrayList<Integer>();

        for (int i = 0; i < s.qtdUsu; i++) {
            try {
                u = (Usuario) space.take(uTemplate, null, Lease.FOREVER);
                usuarios.add(u.nome);
                qtdMsgUsu.add(u.qtdMsg);
            } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
                Logger.getLogger(SalasListagem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        String listUsu = "\n*********************************\n"
                + "- Lista de usuários na sala " + this.sala.nome + ": ";
        
        for (int i = 0; i < usuarios.size(); i++) {
            uTemplate.nome = usuarios.get(i);
            uTemplate.qtdMsg = qtdMsgUsu.get(i);
            try {
                this.space.write(uTemplate, null, Lease.FOREVER);
                listUsu += "\n " + usuarios.get(i);
            } catch (TransactionException | RemoteException ex) {
                Logger.getLogger(SalasListagem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        chatArea.setText(chatArea.getText() + "\n" + listUsu + "\n*********************************\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
        
    }
    
    private class AtualizaChatPublico extends Thread{

        public AtualizaChatPublico() {
        }

        @Override
        public void run(){
            while(true){
                Message template = new Message();
                template.ordem = msgAtualPub;
                template.destino = sala.nome;
                Message msg;
                try {
                    msg = (Message) space.read(template, null, Lease.FOREVER);

                    if(msg != null){
                        msgAtualPub += 1;
                        chatArea.setText(chatArea.getText() + "\n" + msg.usuario + ": " + msg.content);
                        chatArea.setCaretPosition(chatArea.getDocument().getLength());
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    private class AtualizaChatPrivado extends Thread{

        public AtualizaChatPrivado() {
        }

        @Override
        public void run(){
            while(true){
                Message template = new Message();
                template.ordem = msgAtualPriv;
                template.destino = usuario.nome;
                Message msg;
                try {
                    msg = (Message) space.read(template, null, Lease.FOREVER);

                    if(msg != null){
                        msgAtualPriv += 1;
                        
                        if(!msg.usuario.equals(msg.destino)){
                            chatArea.setText(chatArea.getText() + "\n[Mensagem individual] " + msg.usuario + ": " + msg.content);
                            chatArea.setCaretPosition(chatArea.getDocument().getLength());
                        }
                        
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelPrincipal = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
        jButtonEnviar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextArea();
        mensagem = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanelPrincipal.setBackground(new java.awt.Color(255, 255, 255));

        jLabelTitulo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitulo.setText("Nome sala");

        jButtonEnviar.setText("Enviar");
        jButtonEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnviarActionPerformed(evt);
            }
        });

        chatArea.setEditable(false);
        chatArea.setColumns(20);
        chatArea.setRows(5);
        chatArea.setFocusable(false);
        jScrollPane3.setViewportView(chatArea);

        mensagem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mensagemKeyPressed(evt);
            }
        });

        jLabel2.setText("Comandos para o chat:");

        jLabel3.setText("/lista - Obter a lista de todos os usuários na sala");

        jLabel4.setText("/[usuario] [mensagem] - Postar mensagem individualmente para outro usuário");

        javax.swing.GroupLayout jPanelPrincipalLayout = new javax.swing.GroupLayout(jPanelPrincipal);
        jPanelPrincipal.setLayout(jPanelPrincipalLayout);
        jPanelPrincipalLayout.setHorizontalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTitulo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(mensagem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEnviar))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelPrincipalLayout.setVerticalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mensagem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jMenu1.setText("Opções");

        jMenuItem1.setText("Mudar de sala");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnviarActionPerformed
        // TODO add your handling code here:
        enviarMensagemChat();
    }//GEN-LAST:event_jButtonEnviarActionPerformed

    private void mensagemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mensagemKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == evt.VK_ENTER){
            enviarMensagemChat();
        }
    }//GEN-LAST:event_mensagemKeyPressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        new Iniciar(this.usuario, this.space).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SalaChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SalaChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SalaChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SalaChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SalaChat().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chatArea;
    private javax.swing.JButton jButtonEnviar;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField mensagem;
    // End of variables declaration//GEN-END:variables
}
