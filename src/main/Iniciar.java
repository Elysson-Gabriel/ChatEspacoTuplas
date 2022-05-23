/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import models.Sala;
import models.TuplaEspecial;
import models.Usuario;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;
import pages.SalaCadastro;
import pages.SalasListagem;

/**
 *
 * @author elysson
 */
public class Iniciar extends javax.swing.JFrame {
    
    private JavaSpace space;
    private Usuario usuario;
    private boolean criacao;
    /**
     * Creates new form UsuarioCadastro
     * @throws java.lang.ClassNotFoundException
     */
    public Iniciar() throws ClassNotFoundException {
        initComponents();
        
        System.out.println("Procurando pelo servico JavaSpace...");
        Lookup finder = new Lookup(JavaSpace.class);
        this.space = (JavaSpace) finder.getService();
        if (this.space == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
                System.exit(-1);
        } 
        System.out.println("O servico JavaSpace foi encontrado.");
        System.out.println(this.space);
        
        this.criacao = true;
        
    }
    
    public Iniciar(Usuario usuario, JavaSpace space){
        initComponents();
        
        this.usuario = usuario;
        this.space = space;
        
        this.jLabelTitulo.setText("Mudança de sala do usuário");
        this.jTextFieldNome.setText(usuario.nome);
        this.jTextFieldNome.setEnabled(false);
        
        this.criacao = false;
        
        Usuario uTemplate = new Usuario();
        uTemplate.nome = usuario.nome;
        
        Sala sTemplate = new Sala();
        sTemplate.nome = usuario.sala;

        Usuario u = null;
        
        try {
            u = (Usuario) space.take(uTemplate, null, Lease.FOREVER);
            u.sala = "";
            this.usuario = u;
            this.space.write(this.usuario, null, Lease.FOREVER);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(SalasListagem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Sala s = null;
        
        if(usuario.sala != null && !usuario.sala.equals("")){
            try {
                s = (Sala) space.take(sTemplate, null, Lease.FOREVER);
                s.qtdUsu -= 1;
                this.space.write(s, null, Lease.FOREVER);
            } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
                Logger.getLogger(SalasListagem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public boolean criarUsuario(){
        String nome = jTextFieldNome.getText();
        boolean retorno = false;
        
        if(nome.isEmpty() || nome.contains(" ")){
            JOptionPane.showMessageDialog(this, "Informe um nome de usuário válido", 
                    "Tente novamente!", JOptionPane.ERROR_MESSAGE);
            return false;
            
        }else if(existeUsuario(nome) && criacao){
            JOptionPane.showMessageDialog(this, "Nome de usuário já está sendo utilizado", 
                    "Tente novamente!", JOptionPane.ERROR_MESSAGE);
            return false;
            
        }else if(!criacao){
            retorno = true;
        }else{
            this.usuario = new Usuario();
            this.usuario.nome = nome;
            this.usuario.sala = "";
            this.usuario.qtdMsg = 0;
            try {
                this.space.write(usuario, null, Lease.FOREVER);
                retorno = true;
            } catch (TransactionException | RemoteException ex) {
                Logger.getLogger(Iniciar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return retorno;
    }
    
    public boolean existeSala(){
        boolean retorno = false;
        TuplaEspecial template = new TuplaEspecial();
        TuplaEspecial t = null;
        
        try {
            t = (TuplaEspecial) space.read(template, null, 500);
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Não existem salas, por favor crie uma nova.", 
                    "Tente novamente!", JOptionPane.ERROR_MESSAGE);
                return false;
            }else{
                retorno = true;
            }
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(Iniciar.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retorno;
    }
    
    public boolean existeUsuario(String nomeUsuario){
        Usuario uTemplate = new Usuario();
        uTemplate.nome = nomeUsuario;
        Usuario u = null;
        try {
            u = (Usuario) space.read(uTemplate, null, 500);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(SalasListagem.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (u == null) {
            return false;
        }
        return true;
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
        jLabelNome = new javax.swing.JLabel();
        jTextFieldNome = new javax.swing.JTextField();
        jButtonEntrar = new javax.swing.JButton();
        jButtonCriar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanelPrincipal.setBackground(new java.awt.Color(255, 255, 255));

        jLabelTitulo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitulo.setText("Novo Usuário");

        jLabelNome.setText("Nome:");

        jTextFieldNome.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldNomeFocusGained(evt);
            }
        });
        jTextFieldNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNomeKeyReleased(evt);
            }
        });

        jButtonEntrar.setText("Entrar em sala existente");
        jButtonEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEntrarActionPerformed(evt);
            }
        });

        jButtonCriar.setText("Criar nova sala");
        jButtonCriar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/usuario.png"))); // NOI18N

        javax.swing.GroupLayout jPanelPrincipalLayout = new javax.swing.GroupLayout(jPanelPrincipal);
        jPanelPrincipal.setLayout(jPanelPrincipalLayout);
        jPanelPrincipalLayout.setHorizontalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabelNome)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldNome))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jButtonEntrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCriar, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanelPrincipalLayout.setVerticalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(8, 8, 8)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNome)
                    .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEntrar)
                    .addComponent(jButtonCriar))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNomeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldNomeFocusGained

    }//GEN-LAST:event_jTextFieldNomeFocusGained

    private void jTextFieldNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNomeKeyReleased

    }//GEN-LAST:event_jTextFieldNomeKeyReleased

    private void jButtonEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEntrarActionPerformed
        // TODO add your handling code here:
        if(existeSala() && criarUsuario()){
            try {
                new SalasListagem(this.usuario, this.space).setVisible(true);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Iniciar.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.dispose();
        }
    }//GEN-LAST:event_jButtonEntrarActionPerformed

    private void jButtonCriarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarActionPerformed
        // TODO add your handling code here:
        if(criarUsuario()){
            try {
                new SalaCadastro(this.usuario, this.space).setVisible(true);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Iniciar.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.dispose();
        }
    }//GEN-LAST:event_jButtonCriarActionPerformed

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
            java.util.logging.Logger.getLogger(Iniciar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Iniciar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Iniciar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Iniciar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Iniciar().setVisible(true);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Iniciar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCriar;
    private javax.swing.JButton jButtonEntrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelNome;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JTextField jTextFieldNome;
    // End of variables declaration//GEN-END:variables
}
