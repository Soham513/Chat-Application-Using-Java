import java.awt.LayoutManager;
import java.io.BufferedReader;
//import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*; //for networking concepts

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


class Server extends JFrame{
    ServerSocket server; //variable
    Socket socket;
        //variable to read data
        BufferedReader br; // to read data -- //the input stream we are accepting from socket will be given to BufferedReader to read the data
        PrintWriter out;//to write the data
    
                //declare components
                private JLabel heading = new JLabel("Server Area");
        private JTextArea messagArea = new JTextArea();
        private JTextField messagInput = new JTextField();
        private java.awt.Font font = new Font("Roboto",Font.PLAIN,20);
    
    
    
    
        //constructer...
        public Server(){
           try {
            server =  new ServerSocket(7777);
            
            System.out.println("server is ready to accept connection");
            System.out.println("waiting...");
            //to accept the req
            socket= server.accept();//will accept the connection of Client i.e connection of Socket 

            br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // took input stream from socket and gave it to InputStreamReader i.e the data which we are getting in form of byte from the socket InputStreamReader will convert it to char after which BufferReader will create a buffer of it

            out = new PrintWriter(socket.getOutputStream());
/*
 * what getInputStream is doing is creating a uni direction stream (data comes from only one direction) pipe from which we can take data as input
 * same for getOutputStream it will give us a pipe to 
 */

                    createGUI();//method call    
                    handelEvents(); //will listen to event 

            //now to start reading and writing 
            startReading(); //funcitions
            startWriting();

        } catch (Exception e) {
            e.printStackTrace();
        } //port to tell client which port to contact
    }
        private void handelEvents(){
                messagInput.addKeyListener(new KeyListener() {

                    @Override
                    public void keyTyped(KeyEvent e) {
                        
                       // throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        
                       // throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        
                       System.out.println("key released"+e.getKeyCode()); // it will give us the code of the key we pressed key code of enter is 10 so we will work on it 
                        if(e.getKeyCode() == 10){
                           // System.out.println("you have pressed enter button");
                           String contentToSend = messagInput.getText();
                           messagArea.append("Me :"+contentToSend+"\n");
                           out.println(contentToSend);
                           out.flush();
                           messagInput.setText("");
                           messagInput.requestFocus();

                        }
                    }
                    
                });
        }
            private void createGUI(){
                //code for GUI
                this.setTitle("Client Messager[END]"); //this is our window
                this.setSize(600, 600);
                this.setLocationRelativeTo(null); //this will set our window in center
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//
                

                    //coding for component
                    heading.setFont(font);
                    messagArea.setFont(font);
                    messagInput.setFont(font);
                        
                        ImageIcon icon = new ImageIcon("client-1295901_640.png");
                        Image scaleImage = icon.getImage().getScaledInstance(28, 28,Image.SCALE_DEFAULT);
                        heading.setIcon(new ImageIcon(scaleImage));
                        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
                        heading.setHorizontalAlignment(SwingConstants.CENTER);//will set it at center
                        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                        messagArea.setEditable(false);
                            //text position
                            messagInput.setHorizontalAlignment(SwingConstants.CENTER);//this will start writing form center
                        
                        //layout of Frame

                        this.setLayout((LayoutManager) new BorderLayout()); //Border layout will divide the page in 5 parts north -- heading  center--msg south--input area
                        //adding the component to the frame
                        this.add(heading,BorderLayout.NORTH);
                        JScrollPane jscrollPane = new JScrollPane(messagArea);
                        this.add(jscrollPane,BorderLayout.CENTER);
                        this.add(messagInput,BorderLayout.SOUTH);

                    this.setVisible(true);
            }


        /*
         * We have to read and write at same time continously so here we need the concept of multithreading
         * we will create 2 thread one thread will read the data 
         * 2nd thread will take data form user and will send to client
         */
        public void startReading(){
          //Runnable is an interface whose instance can run as a thread ()-> this is a lamda expression 
          //created a therad
            Runnable r1=()->{
                System.out.println("reader started");
                //to read continously we will use  while loop
                    try{
                while(true && !socket.isClosed()){
                    
                    String msg = br.readLine(); // it will read the data from Client
                    if(msg.equals("exit")){ //if client types exit it will stop and get out of the  loop
                        System.out.println("Client has terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client Terminated the Chat");
                        messagInput.setEnabled(false);
                        socket.close();//we have to close the thread when Client terminates the process
                        break;
                    }
                    messagArea.append("Client : "+msg+"\n");
               
                }
                }catch(Exception e){
                    e.printStackTrace();
                }
            };
            new Thread(r1).start();

        }
        public void startWriting(){
            Runnable r2=()->{
                System.out.println("writer started");
                try{
                while(true){
                    
                        //here we are trying to write the data
                        //before doing the writing operation we have to take data from console 
                        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                        String content = br1.readLine();
                        out.println(content);
                        out.flush();//if sometime data does not transfer it will do it by force
                        
                        if(content.equals("exit")){
                            socket.close();
                            break;
                        }
                    
                }
                } catch (Exception e) {
                        e.printStackTrace();
                    }
            };
            //Creating a Thread class object to start both the thread
            new Thread(r2).start();

        }
    public static void main(String[] args) {
        System.out.println("this is server...going to start");
        new Server();//obj created
    }

}