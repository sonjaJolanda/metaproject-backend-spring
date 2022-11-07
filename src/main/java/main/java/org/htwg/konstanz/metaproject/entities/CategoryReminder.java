package main.java.org.htwg.konstanz.metaproject.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author JaDietsch, AmHaas, MeCipa, SuMiele
 */

@Entity
@Table(name = "CategoryReminder")
public class CategoryReminder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "reminderId")
    private Long reminderId;

    @Column(name = "category")
    private String category;

    @Column(name = "text")
    private String text;

    public Long getReminderId() {
        return reminderId;
    }

    public String getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }

}
