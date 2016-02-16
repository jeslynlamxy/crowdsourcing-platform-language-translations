package models.Questions.LinkingExperimentV1;

import play.db.ebean.Model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PTCP extends Model {

    @Id
    int id;

    @Basic
    String head;

    @Basic
    String text;

    public PTCP(String head, String text){
        this.head = head;
        this.text = text;
    }

    public JsonObject toJson(){
        return Json.createObjectBuilder().add("head",head).add("text",text).build();
    }

    public String toString(){
        return "\n\t\thead : " + head + "\n\t\ttext : " + text + "\n";
    }

    public String getHead() {
        return head;
    }

    public String getText() {
        return text;
    }
}
