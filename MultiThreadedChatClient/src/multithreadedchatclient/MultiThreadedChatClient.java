package multithreadedchatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

 public class MultiThreadedChatClient
{
    
     public static void main(String[] args)
    {
       MTCC client = new MTCC();
    }
     
     public static class MTCC
    {
         InetAddress serverAddress = null;
         int serverPort = 121;
         InetSocketAddress serverSocketAddress = null;
         public Socket clientSocket = null;
         
         private DataOutputStream output;
         private DataInputStream input;
         
         public MTCC()
        {
             try
            {
                 serverAddress = InetAddress.getByName("192.168.15.2");
            }
             catch (UnknownHostException ex)
            {
                 Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
             
             serverSocketAddress = new InetSocketAddress(serverAddress,serverPort);
             connectToServer();
        }
         
         public void connectToServer()
        {
            clientSocket = new Socket();
             try
            {
                 clientSocket.connect(serverSocketAddress);
            }
             catch (IOException ex)
            {
                 Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
             try
            {
                 output = new DataOutputStream (clientSocket.getOutputStream());
            }
             catch (IOException ex)
            {
                 Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
             try
            {
                 input = new DataInputStream(clientSocket.getInputStream());
            } 
             catch (IOException ex)
            {
                 Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
             Listener l = new Listener();
             Sender s = new Sender();
             
             l.listener.start();
             s.sender.start();
        }
         
         public class Listener implements Runnable
        {
             Thread listener;
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
                        System.out.println("RECEIVED: "+message+"\n");
                   }
                    catch (IOException ex)
                   {
                        Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex);
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
                     try
                    {
                        output.close();
                            clientSocket.close();
                        }
                         catch (IOException ex1)
                        {
                            Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                     Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
             @Override
             public void run()
            {
                 while (true)
                {
                    message = in.nextLine();
                    try
                   {
                        output.writeUTF(message);
                   }
                    catch (IOException ex)
                   {
                         try
                        {
                            input.close();
                            output.close();
                            clientSocket.close();
                        }
                         catch (IOException ex1)
                        {
                            Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                        Logger.getLogger(MultiThreadedChatClient.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
            }
        }
    }
}