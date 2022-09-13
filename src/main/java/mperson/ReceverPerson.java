package mperson;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

import helper.XMLConvert;
import object.Person;

public class ReceverPerson extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JLabel lbSend;
	private JTextArea inputMessage;
	private JButton btnSend;
	private Session session;
	private MessageProducer producer;
	private JTextArea viewText;
	private Connection con;
	private Date date;

	/**
	 * Launch the application.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReceverPerson frame = new ReceverPerson();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws Exception
	 */
	public ReceverPerson()  {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 721, 354);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		lbSend = new JLabel("Message Receive");
		lbSend.setForeground(new Color(64, 0, 128));
		lbSend.setHorizontalAlignment(SwingConstants.CENTER);
		lbSend.setFont(new Font("Tahoma", Font.BOLD, 15));
		lbSend.setBackground(new Color(128, 128, 192));
		lbSend.setBounds(0, 0, 718, 35);
		contentPane.add(lbSend);

		inputMessage = new JTextArea();
		inputMessage.setFocusable(true);
		inputMessage.setForeground(new Color(128, 128, 128));
		inputMessage.setFont(new Font("Monospaced", Font.PLAIN, 14));
		inputMessage.setBounds(26, 271, 543, 35);
		contentPane.add(inputMessage);

		btnSend = new JButton("Send");
		btnSend.setForeground(new Color(255, 128, 0));
		btnSend.setBackground(new Color(64, 0, 128));
		btnSend.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnSend.setBounds(579, 271, 100, 35);
		btnSend.addActionListener(this);
		contentPane.add(btnSend);

		viewText = new JTextArea();
		viewText.setBounds(26, 34, 653, 226);
		contentPane.add(viewText);

		 try {
			message();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void message() throws Exception {
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
						System.out.println("Nhận được " + txt);

						int indexStart = txt.indexOf("<hoten>");
						int indexEnd = txt.indexOf("</hoten>");
						int indexMSStart = txt.indexOf("<mssv>");
						int indexMSEnd = txt.indexOf("</mssv>");
						System.out.println("index " + indexStart);
							System.out.println(txt);
							String textMS = txt.substring(indexMSStart, indexMSEnd);
							String text = txt.substring(indexStart + 7, indexEnd);
							text.replaceAll("<hoten>", "");
							textMS.replaceAll("<mssv>", "");
							if(viewText.getText().indexOf(textMS) == -1) {
								viewText.append("\nMSSV: " + textMS);
							}
							viewText.append("\nMessages: " + text);
						
						msg.acknowledge();// gửi tín hiệu ack
					} else if (msg instanceof ObjectMessage) {
						ObjectMessage om = (ObjectMessage) msg;
						System.out.println(om);
					}
//others message type....
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		try {
		

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
