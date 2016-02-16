package models.Questions.StoryCompletionExperiment;

import au.com.bytecode.opencsv.CSVWriter;
import com.amazonaws.mturk.requester.Assignment;
import com.fasterxml.jackson.databind.JsonNode;
import models.LingoExpModel;
import models.Questions.PartQuestion;
import models.Repository;
import models.Results.AssignmentResult;
import org.dom4j.DocumentException;
import play.mvc.Result;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

@Entity
@Inheritance
@DiscriminatorValue("StoryCompletionQ")
public class StoryCompletionQuestion extends PartQuestion {

    @Basic
    String itemId;

    @Basic
    String storyType;

    @Column(columnDefinition = "TEXT")
    String story;

    public static StoryCompletionQuestion[] fillers = new StoryCompletionQuestion[]{
            new StoryCompletionQuestion("fill_01", "Filler", "Toby's coworker called in sick to work today, so now Toby is dreading his day. He has to work a double shift, because nobody else is able to fill in at such short notice. Luckily, he has a day off tomorrow, so he can sleep in and relax."),
            new StoryCompletionQuestion("fill_02", "Filler", "Laura is contemplating looking for another job, because she finds her job at the call center boring. She had always dreamed of being a flight attendent, but she's never had the courage to go for it. Perhaps this is the right time to follow her dream."),
            new StoryCompletionQuestion("fill_03", "Filler", "Paula owns a small bakery in Glasgow and is thinking about ways to publicize her business. She's considering making a Facebook page for her company, because it's free and she doesn't have a large budget. She can ask her son to help her, since he knows a lot about social media."),
            new StoryCompletionQuestion("fill_04", "Filler", "Florian is a lawyer at a big corporate firm, and he had a great day at work today. One of his clients gave him a compliment, and afterwards his boss told him he was getting a raise. He has worked on a major account for the last few months."),
            new StoryCompletionQuestion("fill_05", "Filler", "Noah has a long overdue vacation coming up, and he plans on doing three things during this vacation. First, he wants to clean his entire house, because he hasn't done any cleaning in a while. Second, he plans on finishing the last season of Dexter."),
            new StoryCompletionQuestion("fill_06", "Filler", "Claudia's boyfriend proposed to her last night, but she hasn't given him an answer yet. She does love him, but she's not sure whether she can really picture a future with him. It bothers her that he sometimes seems to care more about his car than about her."),
            new StoryCompletionQuestion("fill_07", "Filler", "Alexis's father is turning 60 next month, so she is planning a big surprise party for him. She is holding a party for all of his friends on a large boat, because her father loves boats. She has also booked a jazz band and a comedian to entertain on the boat."),
            new StoryCompletionQuestion("fill_08", "Filler", "Juliette's pregnancy leave is almost over and now she is dreading going back to work. She has really enjoyed spending time with her baby, even though it has been exhausting at times. But there might be certain advantages to going back to work as well."),
            new StoryCompletionQuestion("fill_09", "Filler", "Maisy just started a fashion blog and now she's constantly looking for ways to get more followers. Earlier today she posted a picture of a dress with yellow and black stripes which looked just like a bee. She received a lot of comments on this picture."),
            new StoryCompletionQuestion("fill_10", "Filler", "Ted and his wife have a 25th wedding anniversary coming up and so he is thinking about what to get his wife. He usually gets her a large bouquet of flowers, but he wants to do something special this time. Perhaps a trip to somewhere sunny is more appropriate.")

    };

    public StoryCompletionQuestion(String itemId,String storyType,String story){
        this.itemId = itemId;
        this.storyType = storyType;
        this.story = story;
    }

    public static PartQuestion createQuestion(LingoExpModel experiment, JsonNode questionNode) throws SQLException{
        String story = questionNode.get("story").asText();
        String itemId = questionNode.get("itemId").asText();
        String storyType = questionNode.get("storyType").asText();
        StoryCompletionQuestion question = new StoryCompletionQuestion(itemId,storyType,story);
        question.save();
        return question;
    }

    @Override
    public void writeResults(JsonNode resultNode) throws SQLException {
        String workerId = resultNode.get("workerId").asText();
        System.out.println(workerId + " submitted results.");

        PreparedStatement statement = Repository.getConnection().prepareStatement(
                "INSERT INTO StoryCompletionResults(WorkerId,itemId,result) VALUES(?,?,?)"
        );

        statement.setString(1, workerId);

        for (Iterator<JsonNode> resultIterator = resultNode.get("results").iterator(); resultIterator.hasNext(); ) {
            JsonNode r = resultIterator.next();
            String itemId = r.get("itemId").asText();
            String result = r.get("result").asText();
            try {
                statement.setString(2, itemId);
                statement.setString(3, result);
                statement.execute();
            } catch (SQLException exception) {
                Repository.saveErrorMessage(
                        "[" + statement.toString() + "], " +
                                "[" + workerId + "], " +
                                "[" + itemId + "], " +
                                "[" + result + "]"
                );
            }
        }
        statement.close();
    }

    @Override
    public void setJSONData(LingoExpModel experiment, JsonNode questionNode) throws SQLException {
        String story = questionNode.get("story").asText();
        String itemId = questionNode.get("itemId").asText();
        String storyType = questionNode.get("storyType").asText();

        this.story = story;
        this.itemId = itemId;
        this.storyType = storyType;
    }

    @Override
    public JsonObject returnJSON(){
        JsonObjectBuilder question = Json.createObjectBuilder();
        question.add("itemId",itemId);
        question.add("storyType",storyType);
        question.add("story",story);
        question.add("id",id);

        return question.build();
    }

    @Override
    public AssignmentResult parseAssignment(Assignment assignment) throws DocumentException {
        return null;
    }

    @Override
    public Result render(String assignmentId, String hitId, String workerId, String turkSubmitTo, String additionalExplanations) {
        return null;
    }

    public String getStory() {
        return story;
    }
}
