/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemasdistrubidos;

/**
 *
 * @author eddua
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList; 
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class LampsonyRedell extends JFrame {
    private JLabel pcLabel, waitingLabel;
    private JButton nuevoHiloButton;
    private JPanel panel;
    private Monitor monitor;
    private Integer contador = 1; 
    private ArrayList<String> nombresHilos = new ArrayList<>(); 
    private JTextArea waitingTextArea;
    private Lock lock;
    private Condition condition;

    public LampsonyRedell() {
       super("LampsonyRedell");
        setSize(300, 300);
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
        lock = new ReentrantLock();
        condition = lock.newCondition();

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

                lock.lock(); // Adquirir el bloqueo
                try {
                    thread.start();
                    String textoActual = waitingTextArea.getText(); 
                    String nuevoTexto = "Hilo creado: " + nombreHilo + "\n" + textoActual;
                    waitingTextArea.setText(nuevoTexto);
                } finally {
                    lock.unlock(); // Liberar el bloqueo
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LampsonyRedell();
            }
        });
    }

    class Monitor {
        private JLabel pcLabel, waitingLabel;
        private boolean pcDisponible;

        public Monitor(JLabel pcLabel, JLabel waitingLabel) {
            this.pcLabel = pcLabel;
            this.waitingLabel = waitingLabel;
            pcDisponible = true;
        }

        public void usarPC() {
            Thread currentThread = Thread.currentThread();

            lock.lock(); // Adquirir el bloqueo
            try {
                while (!pcDisponible) {
                    condition.await(); // Esperar hasta que la PC esté disponible
                }

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
                condition.signalAll(); // Notificar a todos los hilos en espera
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock(); // Liberar el bloqueo
            }
        }
    }
}
