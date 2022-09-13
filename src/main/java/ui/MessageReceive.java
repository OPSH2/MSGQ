package ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

public class MessageReceive extends JFrame {

	private JPanel contentPane;
	private JLabel lblReceiveMessage;
	private JTextArea inputMessage;
	private Date date;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageReceive frame = new MessageReceive();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public MessageReceive() throws Exception {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 692, 365);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblReceiveMessage = new JLabel("Receive Message");
		lblReceiveMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblReceiveMessage.setForeground(new Color(64, 0, 128));
		lblReceiveMessage.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblReceiveMessage.setBackground(new Color(128, 128, 192));
		lblReceiveMessage.setBounds(10, 0, 631, 45);
		contentPane.add(lblReceiveMessage);
		
		inputMessage = new JTextArea();
		inputMessage.setForeground(Color.GRAY);
		inputMessage.setFont(new Font("Monospaced", Font.PLAIN, 14));
		inputMessage.setBounds(50, 45, 577, 243);
		contentPane.add(inputMessage);
		
		
		// thiết lập môi trường cho JMS
				BasicConfigurator.configure();
				// thiết lập môi trường cho JJNDI
				Properties settings = new Properties();
				settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
				settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
				// tạo context
				Context ctx = new InitialContext(settings);
				// lookup JMS connection factory
				Object obj = ctx.lookup("ConnectionFactory");
				ConnectionFactory factory = (ConnectionFactory) obj;
				// lookup destination
				Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
				// tạo connection
				Connection con = factory.createConnection("admin", "admin");
				// nối đến MOM
				con.start();
				// tạo session
				Session session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
				// tạo consumer
				MessageConsumer receiver = session.createConsumer(destination);
				// blocked-method for receiving message - sync
				// receiver.receive();
				// Cho receiver lắng nghe trên queue, chừng có message thì notify - async
				System.out.println("Tý was listened on queue...");
				receiver.setMessageListener(new MessageListener() {
					
					// có message đến queue, phương thức này được thực thi
					public void onMessage(Message msg) {// msg là message nhận được
						try {
							if (msg instanceof TextMessage) {
								TextMessage tm = (TextMessage) msg;
								String txt = tm.getText();
								date = new Date();
								date.getTime();
								inputMessage.append(txt +"\t"+ date+ "\n");
							
//								System.out.println("Nhận được " + txt);
								msg.acknowledge();// gửi tín hiệu ack
							} else if (msg instanceof ObjectMessage) {
								ObjectMessage om = (ObjectMessage) msg;
								System.out.println(om);
							}
							// others message type....
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
		
	}
	
	
}
