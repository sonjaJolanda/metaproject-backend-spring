package main.java.org.htwg.konstanz.metaproject.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author JaDietsch, AmHaas, MeCipa, SuMiele
 */

@Entity
@Table(name = "AutomaticReminder")
public class AutomaticReminder {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "autoRemId")
    private Long autoRemId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "metaProjectId")
    private Metaproject metaProjectId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "reminderId")
    private CategoryReminder reminderId;

    @Lob
    @Size(min = 1, max = 30000, message = "1-30000 letters and spaces")
    @Column(name = "text", length = 30000)
    private String text;

    @NotNull(message = "firstDate is null")
    @Column(name = "firstDate")
    private String firstDate;

    @NotNull(message = "secDate is null")
    @Column(name = "secDate")
    private String secDate;

    public Long getAutoRemId() {
        return autoRemId;
    }

    public void setAutoRemId(Long autoRemId) {
        this.autoRemId = autoRemId;
    }

    public Metaproject getMetaProjectId() {
        return metaProjectId;
    }

    public void setMetaProjectId(Metaproject metaProjectId) {
        this.metaProjectId = metaProjectId;
    }

    public CategoryReminder getReminderId() {
        return reminderId;
    }

    public void setReminderId(CategoryReminder reminderId) {
        this.reminderId = reminderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getSecDate() {
        return secDate;
    }

    public void setSecDate(String secDate) {
        this.secDate = secDate;
    }

}
