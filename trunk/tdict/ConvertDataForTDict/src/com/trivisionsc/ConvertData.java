package com.trivisionsc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;


public class ConvertData extends JFrame

{
	// Variables declaration
	int count=0;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblIndexFilePath;
	private JLabel lblDictFilePath;
	private JLabel lblDisplayProcess;
	private JTextField txtSourceFilePath;
	private JTextField txtDbFilePath;
	private JButton btnPerform;
	private JButton btnIndexFileSelect;
	private JButton btnDictFileSelect;
	private JPanel contentPane;
	private JFileChooser fc;

	// End of variables declaration
	
	public ConvertData()
	
	{
		super();
		createLayout();
		this.setVisible(true);
	
	}
	
	
	private void createLayout()
	
	{
	
		// Initialize
		lblIndexFilePath = new JLabel();
		lblDictFilePath = new JLabel();
		lblDisplayProcess= new JLabel();
		txtSourceFilePath = new JTextField();
		txtDbFilePath = new JTextField();
		btnPerform = new JButton();
		btnIndexFileSelect=new JButton();
		btnDictFileSelect=new JButton();
	   	contentPane = (JPanel)this.getContentPane();
	    fc = new JFileChooser();
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
     	//lblIndexFilePath
		lblDisplayProcess.setHorizontalAlignment(SwingConstants.LEFT);
		lblDisplayProcess.setForeground(new Color(255, 0, 0));
		
		//lblIndexFilePath
		lblDictFilePath.setHorizontalAlignment(SwingConstants.LEFT);
		lblIndexFilePath.setForeground(new Color(0, 0, 255));
		lblIndexFilePath.setText(" Path store source dict");
		
		//lblDictFilePath
	
		lblDictFilePath.setHorizontalAlignment(SwingConstants.LEFT);
		lblDictFilePath.setForeground(new Color(0, 0, 255));
		lblDictFilePath.setText(" Path store database");
		
		// txtSourceFilePath
		txtSourceFilePath.setForeground(new Color(0, 0, 255));
		txtSourceFilePath.setSelectedTextColor(new Color(0, 0, 255));
		txtSourceFilePath.setToolTipText("Enter path store source dict");
		txtSourceFilePath.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent e)
		
		{
					;
		}
		});
		
		// txtDbFilePath
		txtDbFilePath.setForeground(new Color(0, 0, 255));
		txtDbFilePath.setToolTipText("Enter path store database");
		txtDbFilePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			
			{
				;
			}
		
			});
		
		// btnPerform
		btnPerform.setBackground(new Color(204, 204, 204));
		btnPerform.setForeground(new Color(0, 0, 255));
		btnPerform.setText("Convert");
		btnPerform.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			
			{
				 excuteConvert();
			}
			
			});
		
		// btnIndexFileSelect
		btnIndexFileSelect.setBackground(new Color(204, 204, 204));
		btnIndexFileSelect.setForeground(new Color(0, 0, 255));
		btnIndexFileSelect.setText("Browser...");
		btnIndexFileSelect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			
			{
				int returnVal = fc.showOpenDialog(ConvertData.this);
				

	            if (returnVal == JFileChooser.APPROVE_OPTION) 
	            {
	                File file = fc.getSelectedFile();
	                txtSourceFilePath.setText(file.getAbsolutePath());
	            
	            } 
			}
			
			});
		// btnDictFileSelect
		btnDictFileSelect.setBackground(new Color(204, 204, 204));
		btnDictFileSelect.setForeground(new Color(0, 0, 255));
		btnDictFileSelect.setText("Browser...");
		btnDictFileSelect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			
			{
				int returnVal = fc.showOpenDialog(ConvertData.this);

	            if (returnVal == JFileChooser.APPROVE_OPTION) 
	            {
	                File file = fc.getSelectedFile();
	                txtDbFilePath.setText(file.getAbsolutePath());
	            } 
			}
			
			});	
		// contentPane
		
		contentPane.setLayout(null);
		
		contentPane.setBorder(BorderFactory.createEtchedBorder());
		
		contentPane.setBackground(new Color(204, 204, 204));
		
		addComponent(contentPane, lblIndexFilePath, 35,10,126,18);
		
		addComponent(contentPane, lblDictFilePath, 35,47,126,18);
		
		addComponent(contentPane, txtSourceFilePath, 160,10,203,22);
		
		addComponent(contentPane, btnIndexFileSelect, 365,9,80,25);
		
		addComponent(contentPane, txtDbFilePath, 160,45,203,22);
		
		addComponent(contentPane, btnDictFileSelect, 365,44,80,25);
		//lblDisplayProcess
		
		addComponent(contentPane, btnPerform, 160,75,90,30);
		
		addComponent(contentPane, lblDisplayProcess, 35,110,200,18);
	
		this.setTitle("Convert data for TDict");
		
		// set icon for program
		ImageIcon receivedIcon = new ImageIcon("resource\\images\\tri.png");
		Image logoImg=receivedIcon.getImage();
		
		this.setIconImage(logoImg);
		
		this.setLocation(new Point(400, 300));
		
		this.setSize(new Dimension(500, 200));
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		this.setResizable(false);
		
		this.setBackground(new Color(200));
		
	
	}
	
	 
	
	/** Add Component Without a Layout Manager (Absolute Positioning) */
	
	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}
	public void excuteConvert()
	{
			
	   	//name dictionary
	   	String dictName=null;
	   	String sourceStorePath=txtSourceFilePath.getText();
		String dbStorePath=txtDbFilePath.getText();
		// path file source dictionary don't exits
		File directorySource = new File(sourceStorePath);  
		if (sourceStorePath==null || sourceStorePath.equals("")||!directorySource.exists())
		{
			JOptionPane.showMessageDialog(ConvertData.this,"Please select path for source dict store directory","Error",JOptionPane.ERROR_MESSAGE,null);
			txtSourceFilePath.requestFocus();
			return;
		}
		
		// path file database don't exits
		File directoryDb = new File(dbStorePath);  
		if (dbStorePath==null || dbStorePath.equals("")||!directoryDb.exists())
		{
			JOptionPane.showMessageDialog(ConvertData.this,"Please select path for database store directory","Error",JOptionPane.ERROR_MESSAGE,null);
			txtDbFilePath.requestFocus();
			return;
		}	
		// path file source dictionary don't exits
		if (sourceStorePath==null || sourceStorePath.equals(""))
		{
			JOptionPane.showMessageDialog(ConvertData.this,"Please select path for source dict store directory","Error",JOptionPane.ERROR_MESSAGE,null);
			return;
		}
		
		// check in source directory include: file index,dictionary and information?
		File[] files = directorySource.listFiles();  
		int check=0;
		if (files.length>0)
		{
			for (int i = 0; i < files.length; i++)  
			{  
				if(files[i].getName().endsWith(".idx"))
				{
					check++;
					dictName=files[i].getName().replaceAll(".idx", "");
				}
				if(files[i].getName().endsWith(".dict"))
					check++;
				if(files[i].getName().endsWith(".ifo"))
					check++;
			}
		}
		else
		{
			JOptionPane.showMessageDialog(ConvertData.this,"Directory source dict have to include files: .idx,.dict and .ifo","Error",JOptionPane.ERROR_MESSAGE,null);
			txtSourceFilePath.requestFocus();
			return;
		}
		if(check<3)
		{
			JOptionPane.showMessageDialog(ConvertData.this,"Directory source dict have to include files: .idx,.dict and .ifo","Error",JOptionPane.ERROR_MESSAGE,null);
			txtSourceFilePath.requestFocus();
			return;
		}
		
        // path file information
	   	String infoFilePath=sourceStorePath+"\\"+dictName+".ifo";
		// path file index
	   	String indexFilePath=sourceStorePath+"\\"+dictName+".idx";
	   	//path file content
	   	String dataFilePath=sourceStorePath+"\\"+dictName+".dict";
	   	// read file information to get max size file index
	   	int idxFileSize=0;
	   	try
	   	{
	   		FileInputStream fstream=new FileInputStream(infoFilePath);
	   		DataInputStream in = new DataInputStream(fstream);
	   		BufferedReader  input = new BufferedReader (new InputStreamReader(in));
	   		String strLine;
	   	    //Read File Line By Line
	   	    while ((strLine = input.readLine()) != null)   {
	   	    	if (strLine.contains("idxfilesize="))
	   	    	{
	   	    		String valueStr=strLine.replaceAll("idxfilesize=", "");
	   	    		idxFileSize = Integer.parseInt(valueStr.trim());
	   	    		break;

	   	    	}
	   	    }
	   	    //Close the input stream
	   	    input.close();
	   	}
	   	catch(Exception e)
	   	{
	   		e.printStackTrace();
	   	}
	   	// perform convert
	   	this.createData(dbStorePath,indexFilePath,dataFilePath,idxFileSize,dictName);
		   
	 }
	@SuppressWarnings("deprecation")
	public void createData(String dbStorePath,String indexFilePath,String dataFilePath,int idxFileSize,String dictName){
	        // Open connect database SQLite
	   try
	   {
		    Class.forName("org.sqlite.JDBC");
		    Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbStorePath+"\\"+dictName+".db");
		    Statement st = conn.createStatement();
		    // Create table and index
		    st.executeUpdate("CREATE TABLE IF NOT EXISTS "+dictName+"(Word TEXT NOT NULL PRIMARY KEY, Content TEXT,Id INTEGER NOT NULL);");
		    st.executeUpdate("CREATE INDEX wrod_idx ON "+dictName+"(Id);");
		    // Read file
	    	FileInputStream fileIndex=new FileInputStream(indexFilePath);
	        BufferedInputStream input = new BufferedInputStream(fileIndex);
	        // File content
	        FileInputStream fileDict=new FileInputStream(dataFilePath);
	        BufferedInputStream dictInput = new BufferedInputStream(fileDict);
	        // Array store data read form File index 
	        byte[] data = new byte[idxFileSize];
	        input.read(data);
	        int start = 0;
	        int j=0;
			// Read data
	        this.enable(false);
	        for (int i = 0; i < data.length; i++) 
	        {	
	        
	            if (data[i] == '\0') 
	            {
	            	//Read data form index
	                int lengthOfData = i - start;
	                byte[] tmp = new byte[lengthOfData];
	                System.arraycopy(data, start, tmp, 0, lengthOfData);
	                int length = byteArrayToInt(data, i+5);
	                //Word 
	                String word = new String(tmp, "UTF-8");
	                //Read content from file data
	                byte[] value = new byte[length];
	                dictInput.read(value);
	                //Content
	                String content = new String(value,"UTF-8");
	                i += 9;
	                start = i;
	            	//String message="Executing .... Insert Element "+j;
	               	// insert into database
	               	st.executeUpdate("INSERT INTO "+dictName+"(Word,Content,Id) VALUES ('"+Utility.encodeContent(word)+"','"+Utility.encodeContent(content)+"',"+j+")");
	            	
	            	j++;    
	        	   
	            }
	        }
	        this.enable(true);
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }
          
    }
	 
	 //method to calculate value of byteArray 
    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    } 
	public static void main(String[] args)
	
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception ex)
		{
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
		
		new ConvertData();
		
		};
	
}