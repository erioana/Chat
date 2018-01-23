import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame implements ActionListener, KeyListener{
	private static final long serialVersionUID = 1L;
	
	private JTextArea message;
	private JTextField txtMsg;
	private JButton btnSend;
	private JButton btnClose;
	private JLabel lblHistory;
	private JLabel lblMsg;
	private JPanel pnlContent;
	private Socket socket;
	private OutputStream ou ;
	private Writer ouw; 
	private BufferedWriter bfw;
	
	private JTextField txtIP;
	private JTextField txtPort;
	private JTextField txtName;  
	
	public Client() throws IOException{	
		JLabel lblMessage = new JLabel("IP-Address and Port Information: ");
	    txtIP = new JTextField("127.0.0.1");
	    txtPort = new JTextField("Port:");
	    txtName = new JTextField("Username:");
	    pnlContent = new JPanel();
	    message = new JTextArea(10,20);
	    lblHistory = new JLabel("History");
	    lblMsg = new JLabel("Message");
	    txtMsg = new JTextField(20);
	    btnSend = new JButton("Send");
	    btnClose = new JButton("Close");
	    JScrollPane scroll = new JScrollPane(message);
	    
	    Object[] texts = {lblMessage, txtIP, txtPort, txtName };     
	    JOptionPane.showMessageDialog(null, texts);              
	    
	    message.setBackground(new Color(240,240,240));
	    message.setEditable(false);
	    setTitle(txtName.getText());
	    setContentPane(pnlContent);
	    setLocationRelativeTo(null);
	    setResizable(false);
	    setSize(250,300);
	    setVisible(true);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    
	    btnSend.addActionListener(this);
	    btnClose.addActionListener(this);
	    btnSend.addKeyListener(this);
	    txtMsg.addKeyListener(this);
	    btnSend.setToolTipText("Send Message");
	    btnClose.setToolTipText("Close Chat");
	    message.setLineWrap(true);  
	    pnlContent.add(lblHistory);
	    pnlContent.add(scroll);
	    pnlContent.add(lblMsg);
	    pnlContent.add(txtMsg);
	    pnlContent.add(btnClose);
	    pnlContent.add(btnSend);
	    pnlContent.setBackground(Color.GRAY);                                                      
	}
	
	/***
	  * This method is responsible to connect on server socket
	  * 
	  * @return IO Exception in case error
	*/
	public void connect() throws IOException{		
		socket = new Socket(txtIP.getText(),Integer.parseInt(txtPort.getText()));	  
		ou = socket.getOutputStream();	  
		ouw = new OutputStreamWriter(ou);	  
		bfw = new BufferedWriter(ouw);	 
		bfw.write(txtName.getText()+"\r\n");	  
		bfw.flush();	
	}
	
	/***
	  * This method is responsible to send the message for server socket
	  * 
	  * @param msg: message that will be sent
	  */
	  public void sendMessage(String msg) throws IOException{		  
		  if(msg.equals("Close")){	     
			  bfw.write("Disconnect \r\n");	      
			  message.append("Disconnect \r\n");    
		  }else{	      
			  bfw.write(msg+"\r\n");	      
			  message.append( txtName.getText() + ": " + txtMsg.getText()+"\r\n");	    
		  }	    
		  bfw.flush();	     
		  txtMsg.setText("");        
	}
	  
	 /**
	   * This method is responsible to receive the message from server
	 */
	  public void listening() throws IOException{
		  InputStream in = socket.getInputStream();	    
		  InputStreamReader inr = new InputStreamReader(in);	     
		  BufferedReader bfr = new BufferedReader(inr);	     
		  String msg = "";	                            
	      
		  while(!"Close".equalsIgnoreCase(msg))	                                      	         
			  if(bfr.ready()){	          
				  msg = bfr.readLine();	         
				  if(msg.equals("Close"))           
					  message.append("Server problem! \r\n");         
				  else	           
					  message.append(msg+"\r\n");                 
			  }
	  }   
	  
	  /***
	   * This method is responsible to close session
	   * 
	   */ 
	  public void close() throws IOException{		  
		  sendMessage("Close");		  
		  bfw.close();	    		   
		  ouw.close();	    
		  ou.close();	   	    
		  socket.close();
	 }
	   	  
	  @Override
	  public void actionPerformed(ActionEvent e) {		  
		  try {	       
			  if(e.getActionCommand().equals(btnSend.getActionCommand()))	         
				  sendMessage(txtMsg.getText());	       
			  else	          
				  if(e.getActionCommand().equals(btnClose.getActionCommand()))	          
					  close();	        
		  } catch (IOException e1) {	         			   
			  e1.printStackTrace();	      
		  }                       	
	  }
  
	  @Override	 
	  public void keyPressed(KeyEvent e) {	                  	  
		  if(e.getKeyCode() == KeyEvent.VK_ENTER){	    
			  try {	   
				  sendMessage(txtMsg.getText());	  
			  } catch (IOException e1) {	  	   
				  e1.printStackTrace();	    
			  }                                                            
		  }                       
	  }   
	  
	  @Override	 
	  public void keyReleased(KeyEvent arg0){}
	      
	  @Override
	  public void keyTyped(KeyEvent arg0){} 
	    
	  public static void main(String []args) throws IOException{      	
		  Client app = new Client();	
		  app.connect();	
		  app.listening();	
	  }          
  
}
