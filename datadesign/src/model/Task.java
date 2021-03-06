package model;

import model.exceptions.EmptyStringException;
import model.exceptions.InvalidPointException;
import model.exceptions.NullArgumentException;
import parsers.Parser;
import parsers.TaskParser;
import parsers.exceptions.ParsingException;

import java.util.*;

// TODO methods have been refactored
// Represents a Task having a description, status, priorities, set of tags and due date.
public class Task {
  public static final DueDate NO_DUE_DATE = null;

  private String description;
  private Set<Tag> tags;
  private DueDate dueDate;
  private int point;
  private Status status;
  private String id;
  private Set<Member> members;

  // MODIFIES: this
  // EFFECTS: constructs a task with the given description
  //    parses the description to extract meta-data (i.e., tags, status, priority and deadline).
  //    If description does not contain meta-data, the task is set to have no due date,
  //    status of 'To Do', and default priority level (i.e., not important nor urgent)
  //  throws EmptyStringException if description is null or empty
  public Task(String description) {
    if (description == null || description.length() == 0) {
      throw new EmptyStringException("Cannot construct a task with no description");
    }
    this.description = description;
    tags = new HashSet<>();
    dueDate = NO_DUE_DATE;
    point = 1;
    status = Status.TODO;
    setDescription(description);
    members = new HashSet<>();
    id = null;
  }

  // MODIFIES: this
  // EFFECTS: creates a tag with name tagName and adds it to this task
  //  throws EmptyStringException if tagName is null or empty
  // Note: no two tags are to have the same name
  public void addTag(String tagName) {
    addTag(new Tag(tagName));
  }

  // MODIFIES: this
  // EFFECTS: adds tag to this task if it is not already exist
  //  throws NullArgumentException if tag is null
  public void addTag(Tag tag) throws NullArgumentException {
    if (tag == null) {
      throw new NullArgumentException("tag shouldn't be null");
    } else if (!tags.contains(tag)) {
      tags.add(tag);
      tag.addTask(this);
    }
  }

  public void setId(String id) {
    this.id = id;
    Member member = new Member(id);
    members.add(member);
  }

  public String getId() {
    return id;
  }

  // MODIFIES: this
  // EFFECTS: removes the tag with name tagName from this task
  //  throws EmptyStringException if tagName is empty or null
  public void removeTag(String tagName) {
    removeTag(new Tag(tagName));
  }

  // MODIFIES: this
  // EFFECTS: removes tag from this task
  //  throws NullArgumentException if tag is null
  public void removeTag(Tag tag) throws NullArgumentException {
    if (tag == null) {
      throw new NullArgumentException("Tag is null!");
    } else if (containsTag(tag)) {
      tags.remove(tag);
      tag.removeTask(this);
    }
  }

  // EFFECTS: returns an unmodifiable set of tags
  public Set<Tag> getTags() {
    return Collections.unmodifiableSet(tags);
  }



  // MODIFIES: this
  // EFFECTS: sets the priority of this task
  //   throws NullArgumentException when priority is null
  public void setPoint(int point) throws InvalidPointException {
      if (point < 0 || point > 5){
        throw new InvalidPointException();
      }
      this.point = point;
  }

  public int getPoint() {
      return point;
  }

  // EFFECTS: returns the status of this task
  public Status getStatus() {
    return status;
  }

  // MODIFIES: this
  // EFFECTS: sets the status of this task
  //   throws NullArgumentException when status is null
  public void setStatus(Status status) {
    if (status == null) {
      throw new NullArgumentException("Illegal argument: status is null");
    }
    this.status = status;
  }

  // EFFECTS: returns the description of this task
  public String getDescription() {
    return description;
  }

  // MODIFIES: this
  // EFFECTS:  sets the description of this task
  //     parses the description to extract meta-data (i.e., tags, status, priority and deadline).
  //  throws EmptyStringException if description is null or empty
  public void setDescription(String description) {
    if (description == null || description.length() == 0) {
      throw new EmptyStringException("setDescription is called with no description");
    }
    this.description = description;
    parseDescription(description);
  }

  // EFFECTS: returns the due date of this task
  public DueDate getDueDate() {
    return dueDate;
  }

  // MODIFIES: this
  // EFFECTS: sets the due date of this task
  public void setDueDate(DueDate dueDate) {
    this.dueDate = dueDate;
  }

  // EFFECTS: returns true if task contains a tag with tagName,
  //     returns false otherwise
  //  throws EmptyStringException if tagName is empty or null
  public boolean containsTag(String tagName) {
    if (tagName == null || tagName.length() == 0) {
      throw new EmptyStringException("Tag name cannot be empty or null");
    }
    return containsTag(new Tag(tagName));
  }

  // EFFECTS: returns true if task contains this tag,
  //     returns false otherwise
  //  throws NullArgumentException if tag is null
  public boolean containsTag(Tag tag) {
    if (tag == null) {
      throw new NullArgumentException("Invalid Argument: tag cannot be null");
    }
    return tags.contains(tag);
  }

  // REQUIRES: description is non-empty
  // MODIFIES: this
  // EFFECTS: parses the description to extract meta-data (i.e., tags, status, priority and deadline).
  private void parseDescription(String description) {
    Parser parser = new TaskParser();
    try {
      parser.parse(description, this);
      this.description = parser.getDescription();
    } catch (ParsingException e) {
      this.description = description;
    }
  }

  //EFFECTS: returns a string representation of this task in the following format
  //    {
  //        Description: Read collaboration policy of the term project
  //        Due date: Sat Feb 02 2019 11:59 PM
  //        Status: IN PROGRESS
  //        Priority: IMPORTANT & URGENT
  //        Tags: #cpsc210, #project
  //    }
  // The order of printed tags do not matter.
  @Override
  public String toString() {
    StringBuffer output = new StringBuffer();
    output.append("\n{");
    output.append("\n\tDescription: " + getDescription());
    output.append("\n\tDue date: " + (getDueDate() == null ? "" : getDueDate()));
    output.append("\n\tStatus: " + getStatus());
    output.append("\n\tPoints: " + point);
    output.append("\n\tTags: " + tagsToString(new ArrayList<Tag>(getTags())));
    output.append("\n}");
    return output.toString();

  }

  // EFFECTS: returns a string containing a comma-separated list of tags,
  //     where each tag is preceded by label "Tags: "
  private String tagsToString(List<Tag> tags) {
    StringBuffer output = new StringBuffer();
    for (int i = 0; i < tags.size() - 1; i++) {
      output.append(tags.get(i) + ", ");
    }
    if (tags.size() > 0) {
      output.append(tags.get(tags.size() - 1));
    } else {
      output.append(" ");
    }
    return output.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Task task = (Task) o;
    return Objects.equals(description, task.description)
            && Objects.equals(dueDate, task.dueDate)
            && status == task.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, dueDate, point, status);
  }
}
