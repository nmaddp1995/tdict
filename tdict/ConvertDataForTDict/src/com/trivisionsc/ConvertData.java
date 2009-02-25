package com.trivisionsc;
/*
 * ConvertDataForTDict
 * Target: convert from dictionary open source of StartDict to database for TDict Program   
 */

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConvertData{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
    	// initialize 
    	ConvertData convert=new ConvertData();
    	//name dictionary
    	String dictName="longman";
    	// path  directory to store file XML Result
    	String xmlFileStoreDirectoryPath="data";
    	// path file index
    	String indexFilePath="src\\"+dictName+".idx";
    	//path file content
    	String dataFilePath="src\\"+dictName+".dict";
    	// size of file index
    	int idxFileSize=771616;
    	// perform convert
    	convert.createData(xmlFileStoreDirectoryPath,indexFilePath,dataFilePath,idxFileSize,dictName);
    }
   public void createData(String xmlFileStoreDirectoryPath,String indexFilePath,String dataFilePath,int idxFileSize,String dictName) throws Exception  {
        // Open connect database SQLite
	    Class.forName("org.sqlite.JDBC");
	    Connection conn = DriverManager.getConnection("jdbc:sqlite:data\\"+dictName+".db");
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
                // insert into database
               	st.executeUpdate("INSERT INTO "+dictName+"(Word,Content,Id) VALUES ('"+Utility.encodeContent(word)+"','"+Utility.encodeContent(content)+"',"+j+")");
                j++;    
        	   
            }
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
 
}


