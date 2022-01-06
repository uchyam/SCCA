package InsertCommentCommand;

import File.FileInputer;
import File.InfoForNecessaryComments;
import ParserOfNeedCommand.Generated.CPP14BaseListener;
import ParserOfNeedCommand.Generated.CPP14Parser;
import org.antlr.v4.runtime.*;
import java.util.ArrayList;
import java.util.List;

public class CommentsListener2 extends CPP14BaseListener {
    private CommonTokenStream tokens;
    private CPP14Parser parser;

    private ArrayList<String> results = new ArrayList<String>();
    public List<String> getResults(){ return this.results; }

    private ArrayList<Integer> resultsLineNum = new ArrayList<Integer>();
    public List<Integer> getResultsLineNum(){ return this.resultsLineNum; }
    private ArrayList<String> resultsText = new ArrayList<String>();
    public List<String> getResultsText(){ return this.resultsText; }
    private void setInfo(int num,String str){
        resultsLineNum.add(num);
        resultsText.add(str);
    }

    private boolean isFunctiondefinition = false;
    private boolean isParametersandqualifiers = false;
    private boolean isParameterdeclaration = false;
    private boolean isExpression = false;

    private InfoForNecessaryComments infoObj;
    int previusComments = -100;

    public CommentsListener2(CommonTokenStream tokens, CPP14Parser parser) {
        this.tokens = tokens;
        this.parser = parser;
    }

    List<InfoForFunctionComments> infos = new ArrayList<InfoForFunctionComments>();
    public List<InfoForFunctionComments> getInfos() {
        return infos;
    }

    InfoForFunctionComments ifc = new InfoForFunctionComments();

    //関数定義の前
    @Override
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx){
        this.ifc = new InfoForFunctionComments();
        determineWhetherCommentIsNecessary(ctx);
        isFunctiondefinition = true;
    }
    @Override public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        this.infos.add(this.ifc);
        isFunctiondefinition = false;
    }

    //引数
    @Override public void enterParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
        isParameterdeclaration = true;
    }
    @Override public void exitParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
        isParameterdeclaration = false;
    }
    @Override public void enterDeclspecifierseq(CPP14Parser.DeclspecifierseqContext ctx) {
        //kata
    }
    @Override public void enterDeclarator(CPP14Parser.DeclaratorContext ctx) {
        if (isFunctiondefinition && isParameterdeclaration) {
            this.ifc.params.add(ctx.getText());
        }
    }

    //return
    @Override public void enterJumpstatement(CPP14Parser.JumpstatementContext ctx) {
        if (isFunctiondefinition){
            isExpression = true;
        }
    }
    @Override public void enterExpression(CPP14Parser.ExpressionContext ctx) {
        if(isExpression) {
            this.ifc.returnContent = ctx.getText();
        }
        isExpression = false;
    }

    @Override public void exitMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        if(isParametersandqualifiers){
            //関数宣言
            determinePreviusComments(ctx);
        } else {
            determinePreviusComments(ctx);
        }
        isParametersandqualifiers = false;
    }

    @Override public void enterParametersandqualifiers(CPP14Parser.ParametersandqualifiersContext ctx) {
        isParametersandqualifiers = true;
    }

    //変数宣言の前
    @Override
    public void enterSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
        //TODO memberdeclartionで，simpletypespecifierになってるやつが変数名になる？
        determinePreviusComments(ctx);
    }

    @Override
    public  void exitTranslationunit(CPP14Parser.TranslationunitContext ctx){
        for(String result:results){
            System.out.println(result);
        }
    }

    //ここがもっとも重要なコード
    private void determineWhetherCommentIsNecessary(ParserRuleContext ctx) {
        Token startToken = ctx.getStart();
        Token stopToken = ctx.getStop();
        List<Token> beforeCommentChannel = getBeforeHiddenTokens(ctx, 2);
        List<Token> afterCommentChannel = getAfterHiddenTokens(ctx, 2);

        int beforeIndex = 0;
        int afterIndex = 0;

        if(beforeCommentChannel != null){
            beforeIndex = beforeCommentChannel.size() -1 ;
        }

        //TODO 条件分岐が複雑すぎる．真理値表を参照．

        if (beforeCommentChannel == null && afterCommentChannel == null) {
            outPutWhereNeedToComments(startToken);
        } else if (beforeCommentChannel == null && afterCommentChannel != null) {
            //後にあるコメントがステートメントと同じ行にある．
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                outPutWhereNeedToComments(startToken);
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        } else if (beforeCommentChannel != null && afterCommentChannel == null) {
            //保持しているコメントと，以前のコメントが一緒じゃない．
            if ((previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex() )) {
            }else{
                outPutWhereNeedToComments(startToken);
            }
        } else {
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                if (previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex()) {
                } else {
                    outPutWhereNeedToComments(startToken);
                }
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        }
    }
    private void determinePreviusComments(ParserRuleContext ctx) {
        Token startToken = ctx.getStart();
        Token stopToken = ctx.getStop();
        List<Token> beforeCommentChannel = getBeforeHiddenTokens(ctx, 2);
        List<Token> afterCommentChannel = getAfterHiddenTokens(ctx, 2);

        int beforeIndex = 0;
        int afterIndex = 0;

        if (beforeCommentChannel != null) {
//            beforeIndex = beforeCommentChannel.size() -1 ;
        }

        if (beforeCommentChannel == null && afterCommentChannel == null) {
//            outPutWhereNeedToComments(startToken);
        } else if (beforeCommentChannel == null && afterCommentChannel != null) {
            //後にあるコメントがステートメントと同じ行にある．
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
//                outPutWhereNeedToComments(startToken);
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        } else if (beforeCommentChannel != null && afterCommentChannel == null) {
            //保持しているコメントと，以前のコメントが一緒じゃない．
            if ((previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex())) {
            } else {
//                outPutWhereNeedToComments(startToken);
            }
        } else {
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                if (previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex()) {
                } else {
//                    outPutWhereNeedToComments(startToken);
                }
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        }
    }

    //隠れているトークンを取得
    private List<Token> getBeforeHiddenTokens(ParserRuleContext ctx, int type){
        Token token= ctx.getStart();
        int i = token.getTokenIndex();
        //TODO 前にある複数のコメントを取りたい．今は一つだけしか取れない．なぜ
        List<Token> CommentChannel = this.tokens.getHiddenTokensToLeft(i,type);
        return CommentChannel;
    }
    //隠れているトークンを取得
    private List <Token> getAfterHiddenTokens(ParserRuleContext ctx, int type){
        Token token = ctx.getStop();
        int i = token.getTokenIndex();
        //TODO 前にある複数のコメントを取りたい．今は一つだけしか取れない．なぜ
        List<Token> CommentChannel = this.tokens.getHiddenTokensToRight(i,type);
        return CommentChannel;
    }
    //コメントの出力の処理
    private void outPutWhereNeedToComments(Token token) {
        String msg = token.getLine() + "行目の " + token.getText()+"の前にコメントが必要です";

        //重複があるときは追加しないようにするための処理
        if(results.isEmpty()) {
            this.results.add(msg);
            setInfo(token.getLine(),token.getText());
            this.ifc.lineNum = token.getLine();
        } else if(msg.equals (results.get( results.size() -1 ))){ //msgと，esultsの最後の要素が一致してるなら，何もしない．

        } else {
            this.results.add(msg);
            setInfo(token.getLine(),token.getText());
            this.ifc.lineNum = token.getLine();
        }
    }

}