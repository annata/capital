package com.xiyoukeji.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by dasiy on 16/12/21.
 */
@Entity
@Table(name = "evaluateRecord")
@DynamicInsert
@DynamicUpdate
public class EvaluateRecord {
    @Id
    @GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Integer id;
    @ManyToOne
    @JoinTable(name = "project_evaluateRecord", joinColumns = {@JoinColumn(name = "evaluateRecord_id")}, inverseJoinColumns = {@JoinColumn(name = "project_id")})
    private Project project;
    private String quarter;//年+季度
    private String updateTime;
    @ManyToOne
    private User user;
    private int item_all;
    private int item_one;
    private int item_two;
    private int item_three;
    private int item_four;
    private int item_five;
    private int item_six;
    private int item_seven;
    private int item_eight;
    private int item_nine;
    private int item_ten;

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getItem_all() {
        return item_all;
    }

    public void setItem_all(int item_all) {
        this.item_all = item_all;
    }

    public int getItem_one() {
        return item_one;
    }

    public void setItem_one(int item_one) {
        this.item_one = item_one;
    }

    public int getItem_two() {
        return item_two;
    }

    public void setItem_two(int item_two) {
        this.item_two = item_two;
    }

    public int getItem_three() {
        return item_three;
    }

    public void setItem_three(int item_three) {
        this.item_three = item_three;
    }

    public int getItem_four() {
        return item_four;
    }

    public void setItem_four(int item_four) {
        this.item_four = item_four;
    }

    public int getItem_five() {
        return item_five;
    }

    public void setItem_five(int item_five) {
        this.item_five = item_five;
    }

    public int getItem_six() {
        return item_six;
    }

    public void setItem_six(int item_six) {
        this.item_six = item_six;
    }

    public int getItem_seven() {
        return item_seven;
    }

    public void setItem_seven(int item_seven) {
        this.item_seven = item_seven;
    }

    public int getItem_eight() {
        return item_eight;
    }

    public void setItem_eight(int item_eight) {
        this.item_eight = item_eight;
    }

    public int getItem_nine() {
        return item_nine;
    }

    public void setItem_nine(int item_nine) {
        this.item_nine = item_nine;
    }

    public int getItem_ten() {
        return item_ten;
    }

    public void setItem_ten(int item_ten) {
        this.item_ten = item_ten;
    }
}
