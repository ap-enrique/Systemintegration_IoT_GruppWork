package com.example.grupparbete;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import com.fazecast.jSerialComm.SerialPort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Scanner;

@Service
public class SerialPortService {

    private SerialPort serialPort;

    @PostConstruct
    public void init() {
        // Lista alla tillgängliga seriella portar
        SerialPort[] availablePorts = SerialPort.getCommPorts();
        for (SerialPort port : availablePorts) {
            System.out.println("Tillgängliga portar: " + port.getSystemPortName());
        }

        // Välj en specifik port, här COM3
        serialPort = SerialPort.getCommPort("COM3");
        serialPort.setComPortParameters(9600, 8, 1, 0); // Sätt rätt portparametrar
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        // Öppna porten
        if (serialPort.openPort()) {
            System.out.println("Porten " + serialPort.getSystemPortName() + " är öppen.");
        } else {
            System.out.println("Kunde inte öppna porten.");
        }

        // Starta en tråd för att läsa data
        new Thread(this::readData).start();
    }

    private void readData() {
        try (InputStream inputStream = serialPort.getInputStream();
             Scanner data = new Scanner(inputStream)) {
            while (data.hasNextLine()) {
                String line = data.nextLine();
                System.out.println("Mottaget: " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void close() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Porten är stängd.");
        }
    }
}



