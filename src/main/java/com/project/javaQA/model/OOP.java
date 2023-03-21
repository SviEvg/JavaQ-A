package com.project.javaQA.model;


import com.vdurmont.emoji.EmojiParser;
import lombok.Data;

;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class OOP {
    @Column(length = 2550000)
    private String question;
    @Column(length = 2550000)
    private String answer;
    private String category;
    @Id
    private Integer id;

    @Override
    public String toString() {
        return question + EmojiParser.parseToUnicode(":question:") + "\n\n" + EmojiParser.parseToUnicode(":point_down:") +
              answer + "\n\n";
    }
}
