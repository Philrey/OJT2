
package ojt2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class myFunctions {
    private static Connection connection=null;
    private static Statement statement=null;
    private static PreparedStatement preparedStatement=null;
    private static ResultSet resultSet=null;
    private BufferedReader br;
    private BufferedWriter bw;
    private String line,cLine;
    
    private static String ip_address="http://localhost:84/EnrollmentSystem/";
    private static int [] studentOrder = new int[] {6,0,1,5,2,7,3,9,4};
    private static int [] teacherOrder = new int[] {6,0,5,1,3,9,4,8,2,7,10};
    private static int [] subjectOrder = new int[] {1,3,0,2};
    private static int [] curriculumOrder = new int[] {3,2,0,4,1};
    
    private static int prevSelection=0;
    //private static String     
    
    public static ImageIcon loginBtn = new ImageIcon("/myPackage/loginBtn_hover.png");
    
    // <editor-fold desc="Getters and Setters">
    public int [] getStudentOrder(){
        return  studentOrder;
    }
    public int [] getTeacherOrder(){
        return  teacherOrder;
    }
    public int [] getSubjectOrder(){
        return  subjectOrder;
    }
    public int getPreviousSelection(){
        return prevSelection;
    }
    public int [] getCurriculumOrder(){
        return curriculumOrder;
    }
    
    public void setPreviousSelection(int value){
        prevSelection = value;
    }
    // </editor-fold>
    //<editor-fold desc="CRUD Methods">
    //C= Create Method //
    protected boolean add_values(String tableName,String columnNames,String [] values){
        String toSend = "";
        String [] column = values;
        for(int n=0;n<column.length;n++){
            if(column[n].contains("null")){
                toSend+=column[n];
            }else{
                if(column[n].contains("now()")){
                    toSend+=column[n];
                }else{
                    toSend+="'"+column[n]+"'";
                }
            }            
            if(n!=column.length-1){
                toSend+=",";
            }
        }
        
        try {
            String url = ip_address+"insertValues.php?tName="+tableName+"&cNames="+columnNames+"&cValues="+toSend;
            url = url.replace("%", "%25");
            url = url.replace(" ", "%20");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
            in.close();
            //print in String
            //System.out.println(response.toString());


            //Read JSON response and print
            JSONObject myResponse = new JSONObject(response.toString());
            JSONArray res = myResponse.getJSONArray("queryResult");
            //System.out.println(res.getJSONObject(0).getString("result"));
            
            return true;
        } catch (Exception e) {
            System.err.println("Exception Found");
            return false;
        }
    }
    //R= Read Method //
    protected String [] return_values(String select,String from,String where,int [] order){
        String [] lines;
        String cLine;
        
        try {
            String url = ip_address+"returnValues.php?select="+select+"&from="+from+"&where="+where;
            url = url.replace("%", "%25");
            url = url.replace(" ", "%20");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            
            if(responseCode != 200){
                JOptionPane.showMessageDialog(null, "Server Error. Please check your connection.");
                return null;
            }
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
            in.close();
            //print in String
            //System.out.println(response.toString());
            

            //Read JSON response and print
            JSONObject myResponse = new JSONObject(response.toString());
            JSONArray res = myResponse.getJSONArray("result");
            
            //Get column names
            
            
            
            if(res.length() > 0){
                //Get column names
                JSONObject sample = res.getJSONObject(0);
                cLine = "";
                for(int n=0;n<sample.names().length();n++){
                    //System.out.println("n "+sample.names().getString(n));
                }
                
                //Get values based on column name keys
                for(int n=0;n<res.length();n++){
                    JSONObject row = res.getJSONObject(n);
                    String temp = "";
                    for(int x=0;x<order.length;x++){
                        //System.err.println(row.names().getString(order[x]));
                        temp+=row.getString(row.names().getString(order[x]))+"@@";
                    }
                    cLine+=temp+"//";
                }
                lines = cLine.split("//");
                return lines;
            }else{
                System.err.println("No result");
            }
        } catch (Exception e) {
            System.err.println("Exception Found");
        }
        
        return null;
    }
    //U= Update Method //
    
    //JSON Query
    protected boolean update_values(String tableName,String [] sets, String where){
        String set = "";
        for(int n=0;n<sets.length;n++){
            if(n!=sets.length-1){
                set+=sets[n]+",";
            }else{
                set+=sets[n];
            }
        }
        
        try {
            String url = ip_address+"updateValues.php?table="+tableName+"&set="+set+"&where="+where;
            url = url.replace("%", "%25");
            url = url.replace(" ", "%20");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
            in.close();
            //print in String
            //System.out.println(response.toString());


            //Read JSON response and print
            JSONObject myResponse = new JSONObject(response.toString());
            JSONArray res = myResponse.getJSONArray("updateResult");
            //System.out.println(res.getJSONObject(0).getString("result"));
            
            return true;
        } catch (Exception e) {
            System.err.println("Exception Found");
            return false;
        }
        
        
    }
    //D= Delete Method // 
    //JSON query
    protected boolean delete_values(String from,String where){
        
        
        try {
            String url = ip_address+"deleteValues.php?from="+from+"&where="+where;
            url = url.replace("%", "%25");
            url = url.replace(" ", "%20");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
            }
            in.close();
            //print in String
            //System.out.println(response.toString());


            //Read JSON response and print
            JSONObject myResponse = new JSONObject(response.toString());
            JSONArray res = myResponse.getJSONArray("isDeleted");
            //System.out.println(res.getJSONObject(0).getString("status"));
            
            return true;
        } catch (Exception e) {
            System.err.println("Exception Found");
            return false;
        }
    }
    //</editor-fold>
    
    protected void clear_table(String table_name){
        String query = "DELETE FROM "+table_name+" WHERE id>0";
        //System.out.println(query);
        try {
            //Performs the query//
            statement.execute(query);
            JOptionPane.showMessageDialog(null, "Deleted Successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Query Failed!");
        }
    }
    protected void EditBatFile(String appToUse){
        String [] linearr;
        File f1=new File("open_a_file.bat");
        File f2=new File("open_a_file.txt");
        f1.renameTo(f2);
        try {
            line="";
           
            br=new BufferedReader(new FileReader("open_a_file.txt"));
            while((cLine=br.readLine())!=null){
                line+=cLine+"@@";
            }
            br.close();
            
            linearr=line.split("@@");
            linearr[1]="start "+appToUse+" ToPrint.txt";
            
            bw=new BufferedWriter(new FileWriter("open_a_file.txt"));
            for(int n=0;n<linearr.length;n++){
                bw.write(linearr[n]);
                bw.newLine();
            }
            bw.close();
            RenameTextFile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Batch file was not found.");
        }
        
    }
    protected boolean RenameTextFile(){
        File f1=new File("open_a_file.txt");
        File f2=new File("open_a_file.bat");
        f1.renameTo(f2);
        return true;
    }
    
    public ImageIcon getImage(String fileName){
        return new ImageIcon(getClass().getResource(fileName));
    }
    
    public void showTab(JPanel windowTabs, JPanel tabName){
        windowTabs.removeAll();
        windowTabs.repaint();
        windowTabs.revalidate();
        windowTabs.add(tabName);
        
        windowTabs.repaint();
        windowTabs.revalidate();
    }
    
    public void clear_table_rows(JTable table_nameTable){
        DefaultTableModel model=(DefaultTableModel) table_nameTable.getModel();
        model.setRowCount(0);
    }
    public void refresh_table(String select,String from,String where,JTable tableName,int [] order){
        clear_table_rows(tableName);
        String [] result = return_values(select, from, where,order);
        //Put result in table
        if(result.length > 0){
            for(int n=0;n<result.length;n++){
                add_table_row(result[n], tableName);
            }
        }
    }
    protected String get_table_row_values(int rowNumber,JTable tableName){
        String toSend = "";
        for(int n=0;n<tableName.getColumnCount();n++){
            toSend+=tableName.getValueAt(rowNumber, n)+"@@";
        }
        
        return  toSend;
    }
    protected void add_table_row(String line,JTable tableName){
        String [] row=line.split("@@");
        Object [] rows = new Object[row.length];
        
        if(row[0].length() < 1){
            return;
        }
        for(int n=0;n<row.length;n++){
            rows[n] = row[n];
        }
        
        DefaultTableModel model;
        
        model=(DefaultTableModel)tableName.getModel();
        model.addRow(rows);
        /*switch(table_number){
            case 1:{
                model=(DefaultTableModel)fs_table.getModel();
                model.addRow(new Object[]{row[0],row[1],row[3]});
                break;
            }case 2:{
                model=(DefaultTableModel)fs_purchases_table.getModel();
                model.addRow(new Object[]{row[0],row[1],row[2],row[3]});
                break;
            }case 3:{
                model=(DefaultTableModel)ph_table.getModel();
                float total=Float.parseFloat(row[1])*Float.parseFloat(row[2]);
                model.addRow(new Object[]{row[0],row[1],row[2]+"/"+row[3],String.valueOf(total)});
                break;
            }case 4:{
                model=(DefaultTableModel)ph_logs_table.getModel();
                model.addRow(new Object[]{row[1],row[2],row[3]});
                break;
            }case 5:{
                model=(DefaultTableModel)inventory_table.getModel();
                model.addRow(new Object[]{row[0],row[1],row[2],row[3],row[4]});
                break;
            }case 6:{
                model=(DefaultTableModel)summary_table.getModel();
                model.addRow(new Object[]{row[2],row[5],row[3],String.valueOf(Float.parseFloat(row[5])*Float.parseFloat(row[3])),row[4],row[6],row[0]});
                break;
            }case 7:{
                model=(DefaultTableModel)credit_lines_table.getModel();
                model.addRow(new Object[]{row[0],row[2],row[1],row[3]});
                break;
            }
        }*/
    }
    
    public class returnValuesThread implements Runnable{

        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        
        
    }
}
