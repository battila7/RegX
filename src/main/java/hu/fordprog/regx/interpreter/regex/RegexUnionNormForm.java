package hu.fordprog.regx.interpreter.regex;

import java.util.ArrayList;
import java.util.List;

public class RegexUnionNormForm {

  static Regex regex;

  public static Regex normalize(Regex original){
    if(isClosureRuleApplicable(original)){
      applyClosureRule(original);
    } else if(isConcatRulesApplicable(original)){
      applyConcatRules(original);
    } else {
      regex = original;
    }

    return regex;
  }

  private static void applyClosureRule(Regex original) {
    ClosureTerm originalClosureTerm = (ClosureTerm) original;

    Union originalChild = ((Group)originalClosureTerm.getChild()).getChild();

    List<Concatenation> originalList = originalChild.getChildren();

    Concatenation newConcatenationChild = new Concatenation();

    for(Concatenation originalConcat : originalList){
      ClosureTerm closureTerm = new ClosureTerm(new Group(new Union(originalConcat)));

      newConcatenationChild.getChildren().add(closureTerm);
    }

    ClosureTerm newClosureTerm = new ClosureTerm(new Group(new Union(newConcatenationChild)));

    regex = newClosureTerm;
  }

  private static void applyConcatRules(Regex original) {
    Concatenation originalConcatenation = (Concatenation) original;

    List<Term> originalTermOfConcatList = originalConcatenation.getChildren();

    Concatenation newRootConcatenation = new Concatenation();

    /*
     * 0. get the known union at the front of the concat list with applying rule 2
     */

    int currentUnionIndex = getFirstIndexOfUnionInConcatenationFromIndex(originalConcatenation, 0);

    Union originalFirstUnion = ((Group)originalTermOfConcatList.get(currentUnionIndex).getChild()).getChild();

    Union newFirstUnion = new Union();

    if(currentUnionIndex != 0){
      for(Concatenation unionChildConcat : originalFirstUnion.getChildren()){
        Concatenation newConcat = new Concatenation();

        for (int i = 0; i < currentUnionIndex; ++i){
          Term childTerm = originalTermOfConcatList.get(i);

          newConcat.getChildren().add(childTerm);
        }

        newConcat.addUnionChild(new Union(unionChildConcat));

        newFirstUnion.getChildren().add(newConcat);
      }

      newRootConcatenation.addUnionChild(newFirstUnion);

      for(int i = currentUnionIndex + 1; i < originalTermOfConcatList.size(); ++i){
        newRootConcatenation.getChildren().add(originalTermOfConcatList.get(i));
      }
    }

    /*
     * 1. get all unions at the front of the concatenation list
     */

    

    //each pair of unions should be reformed together into a new union

    //when only one is left at the front reform a new union with all that is left at the back of the list

    //form the union and wrap it into a concatenation to keep the hierarchy

    regex = newRootConcatenation;
  }

  private static boolean isClosureRuleApplicable(Regex original) {
    return original instanceof ClosureTerm
        && ((Term)original).getChild() instanceof Group;
  }

  private static boolean isConcatRulesApplicable(Regex original) {
    return original instanceof Concatenation
        && hasUnionInConcat((Concatenation) original);
  }

  private static boolean hasUnionInConcat(Concatenation original) {
    return getFirstIndexOfUnionInConcatenationFromIndex(original, 0) != -1;
  }

  private static int getFirstIndexOfUnionInConcatenationFromIndex(Concatenation concat, int index){
    List<Term> children = concat.getChildren();

    for(int i = index; i < children.size(); ++i){
      Atom child = children.get(i).getChild();

      if(child instanceof Group){
        return i;
      }
    }

    return -1;
  }
}
