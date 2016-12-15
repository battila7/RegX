package hu.fordprog.regx.interpreter.regex;

import java.util.ArrayList;
import java.util.List;

/**
 * Rules are:
 * 1. No multiple braces and no RegexCharacter needs to be enclosed
 * 2. No double star e.g. (a*)* -> a*
 * 3. No same components of union
 */
public class RegexSimplifier {
  static Regex regex;

  public static Regex simplify(Regex original){

    regex = original;

    if(original instanceof Group){

      /*
       * 1. Targeting the Group type
       */
      //System.out.println("1. "+original.asText());
      firstRule();
      //System.out.println("/1. "+regex.asText());

    }else if(original instanceof ClosureTerm){

      /*
       * 2. Targeting the ClosureTerm type
       */
      //System.out.println("2. "+original.asText());
      secondRule();
      //System.out.println("/2. "+regex.asText());

    }else if(original instanceof Union){

      /*
       * 3. Targeting the Union type
       */
      //System.out.println("3. "+original.asText());
      thirdRule();
      //System.out.println("/3. "+regex.asText());
    }

    return regex;
  }

  private static void firstRule() {
    Group original = (Group)regex;

    if(original.getChild().getChildren().size() == 1){

      Concatenation singleConcatenation = original.getChild().getChildren().get(0);

      if(singleConcatenation.getChildren().size() == 1
          && !(singleConcatenation.getChildren().get(0) instanceof ClosureTerm)){

        Term term = singleConcatenation.getChildren().get(0);

        if (term.getChild() instanceof RegexCharacter){

          //one character should not be enclosed with braces

          regex = term.getChild();
        }else if(term.getChild() instanceof Group){

          regex = term.getChild();
        }
      }
    }
  }

  private static void secondRule() {
    ClosureTerm original = (ClosureTerm) regex;

    if (original.getChild() instanceof Group){

      Group group = (Group) original.getChild();

      if(group.getChild().getChildren().size() == 1
          && group.getChild().getChildren().get(0).getChildren().size() == 1
          && group.getChild().getChildren().get(0).getChildren().get(0) instanceof ClosureTerm){

        regex = group.getChild().getChildren().get(0).getChildren().get(0);
      }
    }
  }

  private static void thirdRule() {
    Union original = (Union) regex;

    List<Concatenation> list = new ArrayList<>();

    for(Concatenation concatenation : original.getChildren()){
      if (!list.contains(concatenation)){
        list.add(concatenation);
      }
    }
    Union union = new Union();
    union.getChildren().addAll(list);

    regex = union;
  }

}
