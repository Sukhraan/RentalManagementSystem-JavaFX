package rental.database;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rental.ui.listmember.MemberListController;
import rental.ui.listarticle.ArticleListController.Article;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DatabaseHandler {

   
    private static DatabaseHandler handler = null;

    private static final String DB_URL = "jdbc:derby:database/rental;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

    static {
        createConnection();
        inflateDB();
    }

    private DatabaseHandler() {
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    private static void inflateDB() {
        List<String> tableData = new ArrayList<>();
        try {
            Set<String> loadedTables = getDBTables();
            System.out.println("Already loaded tables " + loadedTables);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(DatabaseHandler.class.getClass().getResourceAsStream("/resources/database/tables.xml"));
            NodeList nList = doc.getElementsByTagName("table-entry");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                Element entry = (Element) nNode;
                String tableName = entry.getAttribute("name");
                String query = entry.getAttribute("col-data");
                if (!loadedTables.contains(tableName.toLowerCase())) {
                    tableData.add(String.format("CREATE TABLE %s (%s)", tableName, query));
                }
            }
            if (tableData.isEmpty()) {
                System.out.println("Tables are already loaded");
            }
            else {
                System.out.println("Creating new tables.");
                createTables(tableData);
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cant load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private static Set<String> getDBTables() throws SQLException {
        Set<String> set = new HashSet<>();
        DatabaseMetaData dbmeta = conn.getMetaData();
        readDBTable(set, dbmeta, "TABLE", null);
        return set;
    }

    private static void readDBTable(Set<String> set, DatabaseMetaData dbmeta, String searchCriteria, String schema) throws SQLException {
        ResultSet rs = dbmeta.getTables(null, schema, null, new String[]{searchCriteria});
        while (rs.next()) {
            set.add(rs.getString("TABLE_NAME").toLowerCase());
        }
    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        }
        catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        }
        finally {
        }
        return result;
    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        }
        finally {
        }
    }

    /**
     * This is the method which deletes the article 
     * from the system
     * @param args Article object.
     * @return true if the article is deleted  
     * @exception SQLException
     * @see SQLException
     */
    
    public boolean deleteArticle(Article art) {
        try {
            String deleteStatement = "DELETE FROM ARTICLE WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, art.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        }
        catch (SQLException ex) {
           	  System.out.println(ex.getMessage());
        }
        return false;
    }
    

    /**
     * This is the method which checks if the article 
     * is already issued in the system
     * @param args Article object.
     * @return false if the article is not issued  
     * @exception SQLException
     * @see SQLException
     */
    
    public boolean isArticleIssued(Article art) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE articleID=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, art.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        }
        catch (SQLException ex) {
           System.out.println(ex.getMessage());
        }
        return false;
    }
    

    public boolean updateArticle(Article ar)
    {
        try {
            String update = "UPDATE ARTICLE SET TITLE=?, DESCRIPTION=?, BRAND=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, ar.getTitle());
            stmt.setString(2, ar.getDesc());
            stmt.setString(3, ar.getBrand());
            stmt.setString(4, ar.getId());
            int res = stmt.executeUpdate();
            return (res > 0);
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

  
    public static void main(String[] args) throws Exception {
        DatabaseHandler.getInstance();
    }

   

    private static void createTables(List<String> tableData) throws SQLException {
        Statement statement = conn.createStatement();
        statement.closeOnCompletion();
        for (String command : tableData) {
            System.out.println(command);
            statement.addBatch(command);
        }
        statement.executeBatch();
    }

    public Connection getConnection() {
        return conn;
    }
    
    public ArrayList<String> execArticle() {
  	  
  	  String TABLE_NAME = "ARTICLE";
  	  ArrayList<String> rsdb = new ArrayList<String>();
        try {
            stmt = conn.createStatement();
            ResultSet rs= null;
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            
            if (tables.next()) {
          	  rs =  stmt.executeQuery("SELECT * FROM " + TABLE_NAME); }
            
            while(rs.next())
            {
          	  rsdb.add(rs.getString("title"));
          	 // System.err.println(rs.getString("title") + " --- result title");            	 
            }
            
            
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        } finally {
        }
  	  
        return rsdb;
    }
    
    /**
     * This is the method is used 
     * to delete a member
     * @param args Member object.
     * @return true if member has been deleted 
     * @exception SQLException
     * @see SQLException
     */
    public boolean deleteMember(MemberListController.Member mem) {
        try {
            String delStmt = "DELETE FROM MEMBER WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(delStmt);
            stmt.setString(1, mem.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    /**
     * This is the method is used 
     * to check whether a member has
     * any article issued
     * @param args Member object.
     * @return true if member has an article issued
     * @exception SQLException
     * @see SQLException
     */
    public boolean isMemberHasAnyBooks(MemberListController.Member member) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE memberID=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, member.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    /**
     * This is the method is used 
     * update a member in the database
     * @param args Member object.
     * @return true if member has an article issued
     * @exception SQLException
     * @see SQLException
     */
    public boolean updateMember(MemberListController.Member member) {
        try {
            String update = "UPDATE MEMBER SET NAME=?, EMAIL=?, MOBILE=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getMobile());
            stmt.setString(4, member.getId());
            int res = stmt.executeUpdate();
            return (res > 0);
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    
}
