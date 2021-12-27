package File;

/**
 * Created by satopi on 2017/12/04.
 */

//Jsonのために用意しているクラス
public class InfoForNecessaryComments {

    private boolean SelectionsStatement;
    private boolean IterationsStatement;
    private boolean Function;
    private boolean MemberFunction;
    private boolean Class;
    private boolean ClassMemberVariables;
    private boolean Structure;
    private boolean StructMemberVariables;
    private boolean Union;
    private boolean UnionMemberVariables;
    private boolean EnumStatement;
    private boolean GlobalVariables;
    private boolean OthersStatement;

    public boolean isSelectionsStatement() {
        return SelectionsStatement;
    }
    public boolean isIterationsStatement() {
        return IterationsStatement;
    }
    public boolean isFunction() { return Function;}
    public boolean isMemberFunction() { return MemberFunction; }
    public boolean isClass() { return Class; }
    public boolean isClassMemberVariables() { return ClassMemberVariables; }
    public boolean isStructure() { return Structure; }
    public boolean isStructMemberVariables() { return StructMemberVariables; }
    public boolean isUnion() { return Union; }
    public boolean isUnionMemberVariables() { return UnionMemberVariables; }
    public boolean isEnumStatement() { return EnumStatement; }
    public boolean isGlobalVariables() { return GlobalVariables; }
    public boolean isOthersStatement(){ return OthersStatement; }

}
