/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.sistemasdistrubidos;

/**
 *
 * @author eddua
 * Brinch Hansen
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList; // Importa ArrayList

public class BrinchHansen extends JFrame {

    private JLabel pcLabel, waitingLabel;
    private JButton nuevoHiloButton;
    private JPanel panel;
    private Monitor monitor;
    private Integer contador = 1; 
    private ArrayList<String> nombresHilos = new ArrayList<>(); 
    private JTextArea waitingTextArea;
    
    public BrinchHansen() {
        super("Brinch Hansen");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        pcLabel = new JLabel("PC: Disponible");
        Font font = pcLabel.getFont();
        pcLabel.setFont(new Font(font.getName(), Font.PLAIN, 20));
        waitingTextArea = new JTextArea("Hilo en espera: Ninguno"); 
        waitingTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(waitingTextArea); 

        nuevoHiloButton = new JButton("Nuevo Hilo");

        panel.add(pcLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(nuevoHiloButton, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);

        monitor = new Monitor(pcLabel, waitingLabel);

        nuevoHiloButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        monitor.usarPC();
                    }
                });

              
                String nombreHilo = "Hilo " + contador++;
                thread.setName(nombreHilo);
                nombresHilos.add(nombreHilo); 
                thread.start();

               
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                       String textoActual = waitingTextArea.getText(); 
                        String nuevoTexto = "Hilo creado: " + nombreHilo + "\n" + textoActual;
                        waitingTextArea.setText(nuevoTexto);
                    }
                });
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BrinchHansen();
            }
        });
    }
    class Monitor {
   private JLabel pcLabel, waitingLabel;
    private boolean pcDisponible;
    private ArrayList<Thread> hilosEnEspera;

    public Monitor(JLabel pcLabel, JLabel waitingLabel) {
        this.pcLabel = pcLabel;
        pcDisponible = true;
        hilosEnEspera = new ArrayList<>();
    }

    public void usarPC() {
        Thread currentThread = Thread.currentThread();

        // Espera activa hasta que la PC esté disponible
        while (true) {
            synchronized (this) {
                if (pcDisponible && (hilosEnEspera.isEmpty() || hilosEnEspera.get(0) == currentThread)) {
                    pcDisponible = false;
                    hilosEnEspera.remove(currentThread);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            pcLabel.setText("PC: En uso por " + currentThread.getName());
                        }
                    });
                    break;
                }
            }
            // Espera activamente durante un breve período antes de volver a verificar
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Acceso al recurso
        try {
            Thread.sleep(3000); // Simulación de uso de la PC
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Liberar el recurso
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pcLabel.setText("PC: Disponible");
            }
        });
        synchronized (this) {
            pcDisponible = true;
        }
    }
}

}
