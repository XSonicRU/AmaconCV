import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Amacon {
    public static void main(String[] args) throws IOException, NativeHookException {
        System.out.println("Client or server?");
        if(new Scanner(System.in).nextLine().equalsIgnoreCase("server")){
            System.out.println("Starting server actions...");
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Waiting for client to connect...");
            ss.accept();
        }else{
            System.out.println("Starting client actions...");
            GlobalScreen.registerNativeHook();

        }
    }
}
