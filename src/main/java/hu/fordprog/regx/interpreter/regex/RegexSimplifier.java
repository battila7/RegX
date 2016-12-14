package hu.fordprog.regx.interpreter.regex;

/**
 * Rules are:
 * 1. No multiple braces
 * 2. No double star
 * 3. No same components of union
 */
public class RegexSimplifier {
  static Regex regex;

  public static Regex simplify(Regex original){

    regex = original;

    firstRule();

    secondRule();

    thirdRule();

    return regex;
  }

  private static void firstRule() {

  }

  private static void secondRule() {


  }

  private static void thirdRule() {

  }

}
