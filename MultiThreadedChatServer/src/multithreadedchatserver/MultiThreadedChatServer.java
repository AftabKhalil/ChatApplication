package multithreadedchatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

 public class MultiThreadedChatServer
{    
     public static void main(String[] args)
    {
        MTCS myServer = new MTCS();
       
    }
     
     public static class MTCS
    {
         private InetAddress  localAddress = null;
         private ServerSocket serverSocket = null;
         private int          port = 121;
         public Socket clientSocket = null;
         
         private DataInputStream input = null;
         private DataOutputStream output = null;
         
         public MTCS()
        {    
             try
            {
                 localAddress = InetAddress.getLocalHost();
            } 
             catch (UnknownHostException ex)
            {
                 Logger.getLogger(MTCS.class.getName()).log(Level.SEVERE, null, ex);
            }
                 InetSocketAddress serverSocketAddress = new InetSocketAddress(localAddress,port);
             try 
            {
                 serverSocket = new ServerSocket();
            }
             catch (IOException ex)
            {
                 Logger.getLogger(MultiThreadedChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
             try
            {
                 serverSocket.bind(serverSocketAddress,1);
                 System.out.println("Server Ready TO Listen At "+serverSocket.getLocalSocketAddress());
            } 
             catch (IOException ex)
            {
                 Logger.getLogger(MultiThreadedChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
             acceptClient();
        }
         
         public void acceptClient()
        {
             try
            {
                 clientSocket = serverSocket.accept();
                 System.out.println("Client received : "+serverSocket.toString());
            }
             catch (IOException ex)
            {
                 Logger.getLogger(MultiThreadedChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
             try
            {
                 input = new DataInputStream(clientSocket.getInputStream());
            }
             catch (IOException ex)
            {
                 Logger.getLogger(MultiThreadedChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
    
             Listener l = new Listener();
             Sender s = new Sender();
             
             l.listener.start();
             s.sender.start();
        }
         
         public class Listener implements Runnable
        {   
             private Thread listener;
             public Listener()
            {
                 listener = new Thread(this);
            }
             @Override
             public void run()
            {
                 while(true)
                {
                     try
                    {
                        String message = input.readUTF();
                        System.out.println(message);
                    }
                     catch (IOException ex)
                    {
                         Logger.getLogger(MultiThreadedChatServer.class.getName()).log(Level.SEVERE, null, ex);
                         System.exit(0);
                    }
                }
            }     
        }
         public class Sender implements Runnable
        {
             String message;
             Thread sender;
             Scanner in;
             public Sender()
            {
                 sender = new Thread(this);
                 in = new Scanner(System.in);
                 try
                {
                     output = new DataOutputStream(clientSocket.getOutputStream());
                }
                 catch (IOException ex)
                {
                     Logger.getLogger(MultiThreadedChatServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
             @Override
             public void run()
            {
                 while(true)
                    {
                     message = in.nextLine();
                     try
                    {
                         output.writeUTF(message);
                    }
                     catch (IOException ex)
                    {
                         Logger.getLogger(MultiThreadedChatServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}