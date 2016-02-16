package models.Questions;

import com.amazonaws.mturk.requester.Assignment;
import models.Repository;
import models.Results.AssignmentResult;
import org.dom4j.DocumentException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Entity
@Inheritance
@DiscriminatorValue("PublQuestion")
public abstract class   PublishableQuestion extends Question {

    public void insert(String hitID,int publishedId) throws SQLException {
        PreparedStatement statement = Repository.getConnection().prepareStatement("INSERT INTO QuestionPublishedAs(QuestionID,mTurkID,publishedId) VALUES(?,?,?)");
        statement.setInt(1, getId());
        statement.setString(2, hitID);
        statement.setInt(3,publishedId);
        statement.execute();
    }

    public abstract AssignmentResult parseAssignment(Assignment assignment) throws DocumentException;

    public String getMturkId() throws SQLException {
        PreparedStatement statement = Repository.getConnection().prepareStatement("SELECT * FROM QuestionPublishedAs WHERE QuestionID = ?");
        statement.setInt(1,this.id);

        ResultSet rs = statement.executeQuery();
        String result = null;
        if (rs.next()){
            result = rs.getString("mTurkID");
        }
        return result;
    }

    public static Question byHITId(String hitID) throws SQLException {
        PreparedStatement statement = Repository.getConnection().prepareStatement("SELECT QuestionID FROM QuestionPublishedAs WHERE mTurkID = ?");
        statement.setString(1, hitID);

        ResultSet rs = statement.executeQuery();
        int  questionId = -1 ;
        if (rs.next()){
            questionId = rs.getInt("QuestionID");
        }
        return byId(questionId);
    }

}
