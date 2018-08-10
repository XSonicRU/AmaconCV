import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Amacon {
    private static String buf;
    private static HashMap<Integer,Integer> buttonmap = new HashMap<>();

    public static void main(String[] args) throws IOException, NativeHookException, AWTException {
        MapButtons();
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
        System.out.println("Client or server?");
        Scanner s = new Scanner(System.in);
        if (s.nextLine().equalsIgnoreCase("server")) {
            System.out.println("Starting server actions...");
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Waiting for client to connect...");
            System.out.println("Your local IP: " + getIP(true));
            System.out.println("Your global IP: " + getIP(false));
            Socket sc = ss.accept();
            System.out.println("Connected!");
            Robot r = new Robot();
            BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            Thread l_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            buf = br.readLine();
                        } catch (IOException ignored) {
                        }
                        System.out.println("Got " + buf);
                        try {
                            if (buf.charAt(buf.length() - 1) == '1') {
                                System.out.println("keypress, " + buf.substring(0, buf.indexOf(' ')));
                                r.keyPress(buttonmap.get(Integer.parseInt(buf.substring(0, buf.indexOf(' ')))));
                            } else {
                                System.out.println("keypress, " + buf.substring(0, buf.indexOf(' ')));
                                r.keyRelease(buttonmap.get(Integer.parseInt(buf.substring(0, buf.indexOf(' ')))));
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid/Unmapped button!");
                        }
                    }
                }
            });
            l_thread.start();
            System.out.println("Listening... type anything to finish");
            s.nextLine();
            sc.close();
            l_thread.interrupt();
            System.exit(0);
        } else {
            System.out.println("Starting client actions...");
            GlobalScreen.registerNativeHook();
            System.out.println("Input your friend's IP");
            Socket sc = new Socket(s.nextLine(), 5000);
            if (sc.isConnected()) {
                System.out.println("Connected");
            } else {
                System.out.println("Can't connect, try again");
                System.exit(0);
            }
            PrintWriter pw = new PrintWriter(sc.getOutputStream(), true);
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

                }

                @Override
                public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
                    System.out.println(nativeKeyEvent.getKeyCode() + " pressed!");
                    pw.println(nativeKeyEvent.getKeyCode() + " " + '1');
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
                    System.out.println(nativeKeyEvent.getKeyCode() + " released!");
                    pw.println(nativeKeyEvent.getKeyCode() + " " + '0');
                }
            });
            System.out.println("Listening... type anything to finish");
            s.nextLine();
            sc.close();
            pw.close();
            GlobalScreen.unregisterNativeHook();
            System.exit(0);
        }
    }

    private static String getIP(boolean isLocal) {
        String ip = "";
        if (isLocal) {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip = socket.getLocalAddress().getHostAddress();
            } catch (Exception e) {
                System.out.println("Error!");
                System.exit(0);
            }
        } else {
            try {
                URL myip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader(myip.openStream()));
                ip = in.readLine();
            } catch (IOException ex) {
                System.out.println("It seems you have no internet connection");
            }
        }
        return ip;
    }
    private static void MapButtons(){
        buttonmap.put(16,KeyEvent.VK_Q);
        buttonmap.put(17, KeyEvent.VK_W);
        buttonmap.put(18, KeyEvent.VK_E);
        buttonmap.put(19, KeyEvent.VK_R);
        buttonmap.put(20, KeyEvent.VK_T);
        buttonmap.put(21, KeyEvent.VK_Y);
        buttonmap.put(22, KeyEvent.VK_U);
        buttonmap.put(23, KeyEvent.VK_I);
        buttonmap.put(24, KeyEvent.VK_O);
        buttonmap.put(25,KeyEvent.VK_P);
        buttonmap.put(30, KeyEvent.VK_A);
        buttonmap.put(31, KeyEvent.VK_S);
        buttonmap.put(32,KeyEvent.VK_D);
        buttonmap.put(33,KeyEvent.VK_F);
        buttonmap.put(34,KeyEvent.VK_G);
        buttonmap.put(35,KeyEvent.VK_H);
        buttonmap.put(36, KeyEvent.VK_J);
        buttonmap.put(37, KeyEvent.VK_K);
        buttonmap.put(38, KeyEvent.VK_L);
    }
}
