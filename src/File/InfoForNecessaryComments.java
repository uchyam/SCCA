package File;

/**
 * Created by satopi on 2017/12/04.
 */

//Jsonのために用意しているクラス
public class InfoForNecessaryComments {

    private boolean SelectionsStatement;

    private boolean IterationsStatement;

    private boolean FunctionStatement;

    private boolean OthersStatement;

    public boolean isSelectionsStatement() {
        return SelectionsStatement;
    }

    public boolean isIterationsStatement() {
        return IterationsStatement;
    }

    public boolean isFunctionStatement() {
        return FunctionStatement;
    }

    public boolean isOthersStatement(){
        return OthersStatement;
    }

}
