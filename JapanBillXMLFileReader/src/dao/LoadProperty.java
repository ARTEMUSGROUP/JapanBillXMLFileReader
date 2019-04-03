package dao;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
public class LoadProperty {
	public Properties loadProperty() throws Exception{
		File f = new File("emailList.property");
		FileInputStream fis = new FileInputStream(f);
		Properties fileProp = new Properties();
		fileProp.load(fis);
		fis.close();
		return fileProp;
	}
	public void moveFile(File objmFile,String destPath){
		Boolean result1=true;
		File destFile=null;
		StringBuffer objmBuffer = null;
		BufferedWriter objmBufferedWriter=null;
		BufferedReader objmBufferedReader=null;
		try {
			//code to read from file
			objmBuffer=new StringBuffer();
			objmBufferedReader=new BufferedReader(new FileReader(objmFile));
			String s="";
			while((s=objmBufferedReader.readLine())!=null){
				objmBuffer.append(s);
			}
			
			//code to write into file
			destFile=new File(destPath);
			destFile.createNewFile();
			objmBufferedWriter=new BufferedWriter(new FileWriter(destFile));
			objmBufferedWriter.write(objmBuffer.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			result1=false;
		}finally{
			try {
				if(objmBufferedWriter!=null){
					objmBufferedWriter.flush();
					objmBufferedWriter.close();
				}if(objmBufferedReader!=null){
					objmBufferedReader.close();
				}
				if(result1){
					
					objmFile.delete();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
