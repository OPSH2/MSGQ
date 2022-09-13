package ui;

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
import javax.jms.MessageProducer;
import javax.jms.Session;
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

public class MessageSender extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JLabel lbSend;
	private JTextArea inputMessage;
	private JButton btnSend;
	private Session session;
	private MessageProducer producer;

	/**
	 * Launch the application.
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		
	
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageSender frame = new MessageSender();
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
	public MessageSender() throws Exception {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 651, 260);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lbSend = new JLabel("Send Message");
		lbSend.setForeground(new Color(64, 0, 128));
		lbSend.setHorizontalAlignment(SwingConstants.CENTER);
		lbSend.setFont(new Font("Tahoma", Font.BOLD, 15));
		lbSend.setBackground(new Color(128, 128, 192));
		lbSend.setBounds(0, 0, 631, 45);
		contentPane.add(lbSend);
		
		inputMessage = new JTextArea();
		inputMessage.setForeground(new Color(128, 128, 128));
		inputMessage.setFont(new Font("Monospaced", Font.PLAIN, 14));
		inputMessage.setBounds(40, 45, 560, 94);
		contentPane.add(inputMessage);
	
		
		btnSend = new JButton("Send");
		btnSend.setForeground(new Color(255, 128, 0));
		btnSend.setBackground(new Color(64, 0, 128));
		btnSend.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnSend.setBounds(500, 163, 100, 35);
		btnSend.addActionListener(this);
		contentPane.add(btnSend);
		
		
		BasicConfigurator.configure();
		//config environment for JNDI
		Properties settings=new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
		"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		
		Context ctx=new InitialContext(settings);
		//lookup JMS connection factory
		ConnectionFactory factory=
		(ConnectionFactory)ctx.lookup("ConnectionFactory");
		//lookup destination. (If not exist-->ActiveMQ create once)
		Destination destination=
		(Destination) ctx.lookup("dynamicQueues/thanthidet");
		//get connection using credential
		Connection con=factory.createConnection("admin","admin");
		//connect to MOM
		con.start();
		//create session
		session=con.createSession(
		/*transaction*/false,
		/*ACK*/Session.AUTO_ACKNOWLEDGE
		);
		//create producer
		 producer = session.createProducer(destination);
		//create text message
//		Message msg=session.createTextMessage("hello mesage from ActiveMQ");
//		producer.send(msg);
		
		
//		Person p=new Person(1001, "Thân Thị Đẹt", new Date());
//		String xml=new XMLConvert<Person>(p).object2XML(p);
//		msg=session.createTextMessage(xml);
//		producer.send(msg);
//		//shutdown connection
//		session.close();con.close();
//		System.out.println("Finished...");
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	
	try {
		String xml= inputMessage.getText().trim();
		Message	msg=session.createTextMessage(xml);
		producer.send(msg);
		inputMessage.setText("");
		//shutdown connection
		System.out.println("Sent...");
	} catch (JMSException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	}
}




