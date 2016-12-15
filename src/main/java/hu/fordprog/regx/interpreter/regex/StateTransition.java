package hu.fordprog.regx.interpreter.regex;

public class StateTransition {
  private Integer from;

  private String with;

  private Integer to;

  public StateTransition(Integer from, String with, Integer to) {
    this.from = from;
    this.with = with;
    this.to = to;
  }

  public Integer getFrom() {
    return from;
  }

  public void setFrom(Integer from) {
    this.from = from;
  }

  public String getWith() {
    return with;
  }

  public void setWith(String with) {
    this.with = with;
  }

  public Integer getTo() {
    return to;
  }

  public void setTo(Integer to) {
    this.to = to;
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();

    sb.append(from)
        .append(" | ")
        .append(with)
        .append(" | ")
        .append(to)
        .append("\n");

    return sb.toString();
  }
}
