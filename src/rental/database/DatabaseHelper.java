package rental.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import rental.ui.listmember.MemberListController.Member;
import rental.ui.listarticle.ArticleListController.Article;
import rental.database.DatabaseHandler;;

/**
 *
 * @author Sukhraan Singh
 */
public class DatabaseHelper {


    public static boolean insertNewArticle(Article ar) {
        try {
            PreparedStatement statement = DatabaseHandler.getInstance().getConnection().prepareStatement(
                    "INSERT INTO ARTICLE(id,title,description,brand,isAvail) VALUES(?,?,?,?,?)");
            statement.setString(1, ar.getId());
            statement.setString(2, ar.getTitle());
            statement.setString(3, ar.getDesc());
            statement.setString(4, ar.getBrand());
            statement.setBoolean(5, ar.getAvailabilty());
            return statement.executeUpdate() > 0;
        }
        catch (SQLException ex) {
        System.out.println(ex.getMessage());
        }
        return false;
    }

  

    public static boolean isArticleExists(String id) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ARTICLE WHERE id=?";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, id);
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

    

    public static ResultSet getArticleInfoWithIssueData(String id) {
        try {
            String query = "SELECT ARTICLE.title, ARTICLE.description,"
            		+ " ARTICLE.isAvail, ISSUE.issueTime FROM ARTICLE LEFT JOIN ISSUE"
            		+ " on ARTICLE.id = ISSUE.articleID where ARTICLE.id = ?";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs;
        }
        catch (SQLException ex) {
           System.out.println(ex.getMessage());
        }
        return null;
    }
    
    
    public static boolean insertNewMember(Member member) {
        try {
            PreparedStatement statement = DatabaseHandler.getInstance().getConnection().prepareStatement(
                    "INSERT INTO MEMBER(id,name,mobile,email) VALUES(?,?,?,?)");
            statement.setString(1, member.getId());
            statement.setString(2, member.getName());
            statement.setString(3, member.getMobile());
            statement.setString(4, member.getEmail());
            return statement.executeUpdate() > 0;
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    
    
    public static boolean isMemberExists(String id) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM MEMBER WHERE id=?";
            PreparedStatement stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, id);
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
}
