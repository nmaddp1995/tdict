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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;


public class ConvertData extends JFrame

{
	// Variables declaration
	int count=0;
	private static final long serialVersionUID = 1L;
	private JLabel lblIndexFilePath;
	private JLabel lblDictFilePath;
	private JLabel lblDisplayProcess;
	private JTextField txtSourceFilePath;
	private JTextField txtDbFilePath;
	private JButton btnPerform;
	private JButton btnStop;
	private JButton btnIndexFileSelect;
	private JButton btnDictFileSelect;
	private JPanel contentPane;
	private JFileChooser fc;
	private String messageResult;
	private int start;
	private int countWord;
	private int numberWord;
	int result;
	boolean stop;
	
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
		btnStop=new JButton();
		btnIndexFileSelect=new JButton();
		btnDictFileSelect=new JButton();
	   	contentPane = (JPanel)this.getContentPane();
	    fc = new JFileChooser();
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
     	//lblIndexFilePath
		lblDisplayProcess.setHorizontalAlignment(SwingConstants.LEFT);
		
		//lblIndexFilePath
		lblDictFilePath.setHorizontalAlignment(SwingConstants.LEFT);
		lblIndexFilePath.setText(" Path store source dict");
		
		//lblDictFilePath
		lblDictFilePath.setHorizontalAlignment(SwingConstants.LEFT);
		lblDictFilePath.setText(" Path store database");
		
		// txtSourceFilePath
		txtSourceFilePath.setForeground(new Color(0, 0, 255));
		txtSourceFilePath.setToolTipText("Enter path store source dict");
		
		// txtDbFilePath
		txtDbFilePath.setForeground(new Color(0, 0, 255));
		txtDbFilePath.setToolTipText("Enter path store database");
		
		// btnPerform
		btnPerform.setText("Convert");
		btnPerform.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			
			{
				 //insert database
				 excuteConvert();
			}
			
			});
		// btnStop
		btnStop.setText("Stop");
		btnStop.setEnabled(false);
		btnStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			
			{
				stop=true;
			}
			
			});
		
		// btnIndexFileSelect
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
		// add component for Frame
		addComponent(contentPane, lblIndexFilePath, 35,10,126,18);
		addComponent(contentPane, lblDictFilePath, 35,47,126,18);
		addComponent(contentPane, txtSourceFilePath, 160,10,203,22);
		addComponent(contentPane, btnIndexFileSelect, 365,9,80,25);
		addComponent(contentPane, txtDbFilePath, 160,45,203,22);
		addComponent(contentPane, btnDictFileSelect, 365,44,80,25);
		addComponent(contentPane, btnPerform, 160,75,90,30);
		addComponent(contentPane, btnStop, 250,75,90,30);
		addComponent(contentPane, lblDisplayProcess, 35,110,200,18);
	
		//set title for program
		this.setTitle("Convert data for TDict");
		// set icon for program
		ImageIcon receivedIcon = new ImageIcon("resource\\images\\tri.png");
		Image logoImg=receivedIcon.getImage();
		this.setIconImage(logoImg);
		//set position display
		this.setLocation(new Point(400, 300));
		//set size display
		this.setSize(new Dimension(500, 200));
		//set event close window
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//set disable resizable
		this.setResizable(false);
	
	}
	
	
	/** Add Component Without a Layout Manager (Absolute Positioning) */
	
	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}
	public void excuteConvert()
	{
		//set empty for display result run 
		lblDisplayProcess.setText("");	
	   	//name dictionary
	   	String dictName=null;
	   	// path to file
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
			// Folder don't include any file
			JOptionPane.showMessageDialog(ConvertData.this,"Directory source dict have to include files: .idx,.dict and .ifo","Error",JOptionPane.ERROR_MESSAGE,null);
			txtSourceFilePath.requestFocus();
			return;
		}
		if(check<3)
		{
			// Folder don't include full file
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
	   		// open stream read file
	   		FileInputStream fstream=new FileInputStream(infoFilePath);
	   		DataInputStream in = new DataInputStream(fstream);
	   		BufferedReader  input = new BufferedReader (new InputStreamReader(in));
	   		String strLine;
	   		int k=0;
	   	    //Read File Line By Line
	   	    while ((strLine = input.readLine()) != null)   {
	   	    	if (strLine.contains("idxfilesize="))
	   	    	{
	   	    		String valueStr=strLine.replaceAll("idxfilesize=", "");
	   	    		idxFileSize = Integer.parseInt(valueStr.trim());
	   	    		k++;
	   	       	}
	   	    	if (strLine.contains("wordcount="))
	   	    	{
	   	    		String valueStr=strLine.replaceAll("wordcount=", "");
	   	    		numberWord = Integer.parseInt(valueStr.trim());
	   	    		k++;
	   	       	}
	   	    	if(k>1) break;
   	  	
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
		  
		    File checkFile = new File(dbStorePath+"\\"+dictName+".db");
		   	if (checkFile.exists())
		   	{
		   		
		   		result =JOptionPane.showConfirmDialog((ConvertData.this), "Are you override?", "Dupicate file!", JOptionPane.YES_NO_OPTION);
		   		if (result==1) 
		   		{
		   			setActive();
		   			return;	
		   		}
		   		else
		   		{
		   			boolean isDeleteSuccess=checkFile.delete();
		   			if (!isDeleteSuccess)
		   				{
		   					JOptionPane.showMessageDialog(ConvertData.this,"Delete file unsuccessful. Please close program after run again. ","Error",JOptionPane.ERROR_MESSAGE,null);
		   					setActive();
				   			return;	
		   				}
		   				
		   			
		   		}
		   	}
		   
		    //Disable controls before insert database
			setUnActive();
		    Class.forName("org.sqlite.JDBC");
		    Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbStorePath+"\\"+dictName+".db");
		    final Statement st = conn.createStatement();
		    // Create table and index
		    st.executeUpdate("CREATE TABLE IF NOT EXISTS "+dictName+"(Word TEXT NOT NULL PRIMARY KEY, Content TEXT,Id INTEGER NOT NULL);");
		    st.executeUpdate("CREATE INDEX wrod_idx ON "+dictName+"(Id);");
		    // Read file
	    	FileInputStream fileIndex=new FileInputStream(indexFilePath);
	    	final BufferedInputStream input = new BufferedInputStream(fileIndex);
	        // File content
	        FileInputStream fileDict=new FileInputStream(dataFilePath);
	        final BufferedInputStream dictInput = new BufferedInputStream(fileDict);
	        // Array store data read form File index 
	        final byte[] data = new byte[idxFileSize];
	        input.read(data);
	        
	        final String dictionName=dictName;
	        countWord=0;
			// Read data
	        new Thread(new Runnable() {
	    		   public void run() {
	    		      for (int i=0; i < data.length; i++) 
	    		      {
	    		    	  if (data[i] == '\0') 
	    		    	  {
	    		    		  	try
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
		    		               	// insert into database
		    		               	st.executeUpdate("INSERT INTO "+dictionName+"(Word,Content,Id) VALUES ('"+Utility.encodeContent(word)+"','"+Utility.encodeContent(content)+"',"+countWord+")");
		    		               	messageResult="Executing .... Insert Element "+countWord+"/"+numberWord;
		    		               	countWord++; 
		    		               	if (stop) 
		    		               	{
		    		               		setActive();
		    		               		messageResult="Insert to be cancel";
					    	  		  	i=data.length;
					     	  		   	return;
		    		               	}
		    		               	
	    		    		  	}
	    		    		  	catch(Exception e) 
			    		        {
	    		    		  		; 
			    		         }
		    		          
		    		          SwingUtilities.invokeLater(
		    		        		 new Runnable() 
		    		        		 {
		    		        			 public void run() 
		    		        			 { 
		    		        				 lblDisplayProcess.setText(messageResult);
		    		        			 }
		    		        		 }
		    		          );
		    		          try 
		    		          {
		    		        	 Thread.sleep(1000); 
		    		        
		    		          } catch(Exception e) 
		    		          {
		    		        	; 
		    		          }
	    		    	  }
	    		    
	    		      }
	    		      try
	    		      {
		    		      setActive();
		    		      lblDisplayProcess.setText("Create database success! ");
	    		      }
	    		      catch(Exception e) 
    		          {
	    		        	; 
	    		      }
	    		      ///
	    		    }
	    		}).start();
	       
      
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
    public void updateControl() throws Throwable
    {
       	      for (int i=0; i<100; i++) {
    	         System.out.println("thread "
    	            +Thread.currentThread().getName()+" step "+i);
    	         Thread.sleep(500);
    	      }
  	
    }
    public final  void setActive()
    {
    	 //set for control enable
  	 	 txtDbFilePath.setEnabled(true);
	  	 txtSourceFilePath.setEnabled(true);
	     btnIndexFileSelect.setEnabled(true);
	  	 btnDictFileSelect.setEnabled(true);
	  	 btnPerform.setEnabled(true);
	  	 btnStop.setEnabled(false);
    }
    public final void setUnActive()
    {
    	//set for control disable
  	 	 txtDbFilePath.setEnabled(false);
	  	 txtSourceFilePath.setEnabled(false);
	     btnIndexFileSelect.setEnabled(false);
	  	 btnDictFileSelect.setEnabled(false);
	  	 btnPerform.setEnabled(false);
	  	 btnStop.setEnabled(true);
    }
	public static void main(String[] args)
	
	{
		//set Look and Feel
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
		//perform run program convert data for TDict
		new ConvertData();
		
		};
		
}