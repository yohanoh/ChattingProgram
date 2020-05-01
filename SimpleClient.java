package chat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class SimpleClient extends Thread implements ActionListener{
	
	Frame frame;
	TextArea ta;
	TextField tf, tf2;
	Dialog dialog;
	Label label;
	
	Socket s1;
	DataOutputStream dos;
	DataInputStream dis;
	boolean stop;
	String host;
	
	public SimpleClient() {
		launchFrame();
	}
	
	public void launchFrame() {
		frame = new Frame("�ϴ��� ä��");
		ta = new TextArea();
		tf = new TextField();
		frame.setBackground(Color.lightGray);
		ta.setEditable(false);
		frame.add(ta, BorderLayout.CENTER);
		frame.add(tf, BorderLayout.SOUTH);
		tf.addActionListener(this);
		frame.setSize(500, 300);
		frame.setVisible(true);
		tf.requestFocus();
		dialog = new Dialog(frame, "���� IP", true);
		label = new Label("������ ���� IP �ּҸ� �Է��ϼ���");
		tf2 = new TextField(15);
		dialog.add(label, BorderLayout.NORTH);
		dialog.add(tf2, BorderLayout.CENTER);
		tf2.addActionListener(this);
		dialog.pack();
		dialog.setVisible(true);
		service();
		tf2.requestFocus();
	}
	
	public void service() {
		try {
			s1 = new Socket(host, 5432);
			dis = new DataInputStream(s1.getInputStream());
			dos = new DataOutputStream(s1.getOutputStream());
			ta.append("���ӿϷ�.. \n");
			this.start();
		}catch(IOException e) {e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		new SimpleClient();
	}
	
	public void actionPerformed(ActionEvent action) {
		if(tf==action.getSource()) {
			try {
				String msg = tf.getText();
				ta.append(msg + "\n");
				if(msg.equals("exit")) {
					ta.append("bye");
					stop = true;
					dos.close();
					s1.close();
					System.exit(0);;
				}else {
					dos.writeUTF("���� : " + msg);
					tf.setText(" ");
				}
			}catch(IOException e) {
				ta.append(e.toString() + "\n");
			}
			
		}else {
			host = tf2.getText().trim();
			if(host.equals("")) host = "localhost";
			dialog.dispose();
		}
	}
	
	public void run() {
		System.out.println("Thread started...");
		try {
			while(!stop) {
				ta.append(dis.readUTF() + "\n");
			}
			dis.close();
			s1.close();
		}catch(EOFException e) {
			ta.append("�����κ��� ������ ���������ϴ�.\n");
		}catch(IOException e1) {
			e1.printStackTrace();
		}
	}
}
