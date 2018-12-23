package mmstq.com.wut;

import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public class myData implements Serializable {
   public String heading, description;
   private long surveyNo;
   private boolean say,wutOptional,likeDislike,wut,open;
   private int noDislike, noLike, noSay,noWut ;

   public myData(){}

   public myData(long surveyNo, int noDislike, int noLike,int noWut, int noSay, String heading, String description, boolean say, boolean wutOptional, boolean likeDislike, boolean wut, boolean open) {
      this.heading = heading;
      this.description = description;
      this.say = say;
      this.wutOptional = wutOptional;
      this.likeDislike = likeDislike;
      this.wut = wut;
      this.open = open;
      this.surveyNo = surveyNo;
      this.noWut = noWut;
      this.noDislike = noDislike;
      this.noLike = noLike;
      this.noSay = noSay;
   }


   public long getSurveyNo() {
      return surveyNo;
   }
   public int getNoDislike() {
      return noDislike;
   }
   public void setNoWut(int noWut) {
      this.noWut = noWut;
   }
   public int getNoWut() {
      return noWut;
   }
   public int getNoLike() {
      return noLike;
   }
   public int getNoSay() {
      return noSay;
   }
   public void setSurveyNo(Long surveyNo) {
      this.surveyNo = surveyNo;
   }
   public void setNoDislike(int noDislike) {
      this.noDislike = noDislike;
   }
   public void setNoLike(int noLike) {
      this.noLike = noLike;
   }
   public void setNoSay(int noSay) {
      this.noSay = noSay;
   }
   public void setWutOptional(boolean wutOptional) {
      this.wutOptional = wutOptional;
   }
   public void setLikeDislike(boolean likeDislike) {
      this.likeDislike = likeDislike;
   }
   public void setOpen(boolean open) {
      this.open = open;
   }
   public void setSay(boolean say) {
      this.say = say;
   }
   public void setWut(boolean wut) {
      this.wut = wut;
   }
   public void setHeading(String heading) {
        this.heading = heading;
    }
   public void setDescription(String description) {
        this.description = description;
    }
   public String getHeading() {
        return heading;
    }
   public String getDescription() {
        return description;
    }
   public boolean isSay() {
      return say;
   }
   public boolean isWutOptional() {
      return wutOptional;
   }
   public boolean isLikeDislike() {
      return likeDislike;
   }
   public boolean isWut() {
      return wut;
   }
   public boolean isOpen() {
      return open;
   }
}
