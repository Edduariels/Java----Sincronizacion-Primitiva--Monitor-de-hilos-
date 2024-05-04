/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.sistemasdistrubidos;

/**
 *
 * @author eddua
 * Hoare
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList; // Importa ArrayList

public class Sistemasdistrubidos extends JFrame {

    private JLabel pcLabel, waitingLabel;
    private JButton nuevoHiloButton;
    private JPanel panel;
    private Monitor monitor;
    private Integer contador = 1; 
    private ArrayList<String> nombresHilos = new ArrayList<>(); 
    private JTextArea waitingTextArea;
    
    public Sistemasdistrubidos() {
        super("Hoare");
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
                new Sistemasdistrubidos();
            }
        });
    }

    class Monitor {
        private JLabel pcLabel, waitingLabel;
        private boolean pcDisponible;
        private StringBuilder hilosEnEspera;

        public Monitor(JLabel pcLabel, JLabel waitingLabel) {
            this.pcLabel = pcLabel;
            this.waitingLabel = waitingLabel;
            pcDisponible = true;
            hilosEnEspera = new StringBuilder();
        }

        public synchronized void usarPC() {
            Thread currentThread = Thread.currentThread();
       
            while (!pcDisponible) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (pcDisponible) {
                pcDisponible = false;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        pcLabel.setText("PC: En uso por " + currentThread.getName());
                    }
                });

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        pcLabel.setText("PC: Disponible");
                    }
                });
                pcDisponible = true;
            }
            notifyAll();
        }
    }
}