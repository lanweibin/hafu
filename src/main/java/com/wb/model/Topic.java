package com.wb.model;

public class Topic {
    private Integer topicId;
    private String topicName;
    private String topicDesc;
    private String topicImage;
    private Integer parentTopicId;

    private Integer followedCount;

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicDesc() {
        return topicDesc;
    }

    public void setTopicDesc(String topicDesc) {
        this.topicDesc = topicDesc;
    }

    public String getTopicImage() {
        return topicImage;
    }

    public void setTopicImage(String topicImage) {
        this.topicImage = topicImage;
    }

    public Integer getParentTopicId() {
        return parentTopicId;
    }

    public void setParentTopicId(Integer parentTopicId) {
        this.parentTopicId = parentTopicId;
    }

    public Integer getFollowedCount() {
        return followedCount;
    }

    public void setFollowedCount(Integer followedCount) {
        this.followedCount = followedCount;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topicId=" + topicId +
                ", topicName='" + topicName + '\'' +
                ", topicDesc='" + topicDesc + '\'' +
                ", topicImage='" + topicImage + '\'' +
                ", parentTopicId=" + parentTopicId +
                ", followedCount=" + followedCount +
                '}';
    }
}
