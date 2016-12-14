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

      ClosureTerm closureTerm;

      if(originalConcat.getChildren().size()==1
          && originalConcat.getChildren().get(0).getChild() instanceof RegexCharacter){

         closureTerm = new ClosureTerm(originalConcat.getChildren().get(0).getChild() );

      }else{

         closureTerm = new ClosureTerm(new Group(new Union(originalConcat)));
      }


      newConcatenationChild.getChildren().add(closureTerm);
    }

    ClosureTerm rootClosureTerm = new ClosureTerm(new Group(new Union(newConcatenationChild)));

    regex = rootClosureTerm;
  }

  private static void applyConcatRules(Regex original) {
    Concatenation originalConcatenation = (Concatenation) original;

    List<Term> originalTermOfConcatList = originalConcatenation.getChildren();

    Concatenation rootConcatenation = new Concatenation();

    rootConcatenation.getChildren().addAll(originalTermOfConcatList);

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
      rootConcatenation.getChildren().clear();

      rootConcatenation.addUnionChild(newFirstUnion);

      for(int i = currentUnionIndex + 1; i < originalTermOfConcatList.size(); ++i){
        rootConcatenation.getChildren().add(originalTermOfConcatList.get(i));
      }
    }

    /*
     * 1. get all unions at the front of the concatenation list
     */

    currentUnionIndex = getFirstIndexOfUnionInConcatenationFromIndex(rootConcatenation, 1);

    // -1:  if there are no more union in the concat, just the one on the first spot
    while (currentUnionIndex != -1){

      List<Term> oldTermList = new ArrayList<>(rootConcatenation.getChildren());

      Union currentUnion = ((Group)oldTermList.get(currentUnionIndex).getChild()).getChild();

      Union newUnion = new Union();


      if(currentUnionIndex != 1){
        for(Concatenation unionChildConcat : currentUnion.getChildren()){
          Concatenation newConcat = new Concatenation();

          //the 0. spot is the other union!
          for (int i = 1; i < currentUnionIndex; ++i){
            Term childTerm = oldTermList.get(i);

            newConcat.getChildren().add(childTerm);
          }

          newConcat.addUnionChild(new Union(unionChildConcat));

          newUnion.getChildren().add(newConcat);
        }

        rootConcatenation.getChildren().clear();

        rootConcatenation.getChildren().add(oldTermList.get(0));
        rootConcatenation.addUnionChild(newUnion);

        for(int i = currentUnionIndex + 1; i < oldTermList.size(); ++i){
          rootConcatenation.getChildren().add(oldTermList.get(i));
        }
      }

      /*
       * 2. the pair of unions at 0. and 1. index should be reformed together into a new union
       */

      oldTermList = new ArrayList<>(rootConcatenation.getChildren());

      Union firstUnion = ((Group)oldTermList.get(0).getChild()).getChild();
      Union secondUnion = ((Group)oldTermList.get(1).getChild()).getChild();

      Union mergedUnion = new Union();

     for(Concatenation firstConcat : firstUnion.getChildren()){
       for(Concatenation secondConcat : secondUnion.getChildren()){
         Concatenation concat = new Concatenation();
         concat.addConcatenationChild(firstConcat);
         concat.addConcatenationChild(secondConcat);

         mergedUnion.getChildren().add(concat);
       }
     }

      rootConcatenation.getChildren().clear();

      rootConcatenation.addUnionChild(mergedUnion);

      for(int i = 2; i < oldTermList.size(); ++i){
        rootConcatenation.getChildren().add(oldTermList.get(i));
      }

      currentUnionIndex = getFirstIndexOfUnionInConcatenationFromIndex(rootConcatenation, 1);
    }

    /*
     * 3. when only one is left at the front reform a new union with all that is left at the back of the list
     */
    List<Term> oldTermList = new ArrayList<>(rootConcatenation.getChildren());

    Union oldUnion = ((Group)oldTermList.get(0).getChild()).getChild();

    Union rootUnion = new Union();

    if(oldTermList.size() > 1){
      for(Concatenation child : oldUnion.getChildren() ){
        for(int i = 1; i < oldTermList.size(); ++i){
          Concatenation concat = new Concatenation();

          Term childTerm = oldTermList.get(i);

          concat.addConcatenationChild(child);
          concat.getChildren().add(childTerm);

          rootUnion.getChildren().add(concat);
        }
      }
    }else {
      rootUnion = oldUnion;
    }

    /*
     * 4. form the union and wrap it into a concatenation to keep the hierarchy
     */
    rootConcatenation.getChildren().clear();

    rootConcatenation.addUnionChild(rootUnion);

    regex = rootConcatenation;
  }

  private static boolean isClosureRuleApplicable(Regex original) {
    return original instanceof ClosureTerm
        && ((ClosureTerm)original).getChild() instanceof Group
        && ((Group)((ClosureTerm)original).getChild()).getChild().getChildren().size()>1;
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

      if(child instanceof Group
          && ((Group) child).getChild().getChildren().size() > 1){
        return i;
      }
    }

    return -1;
  }
}
