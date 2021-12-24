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

    private boolean isFunctiondefinition;
    private boolean isFunctiondeclaration;
    private boolean isParametersandqualifiers;

    private InfoForNecessaryComments infoObj;
    int previusComments = -100;

    public CommentsListener2(CommonTokenStream tokens, CPP14Parser parser) {
        this.tokens = tokens;
        this.parser = parser;
    }

    //    //自作された型のポインタ
//    @Override
//    public void enterExpressionstatement(CPP14Parser.ExpressionstatementContext ctx){
//        determineWhetherCommentIsNecessary(ctx);
//    }

    //関数定義の前
    @Override
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx){
        determineWhetherCommentIsNecessary(ctx);
    }

    //(メンバ?)関数宣言，変数宣言の前．
    @Override
    public void enterMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        //TODO memberdeclartionで，simpletypespecifierになってるやつが変数名になる？
        isParametersandqualifiers = false;
    }

    @Override public void exitMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        if(isParametersandqualifiers){
            //関数宣言
            determineWhetherCommentIsNecessary(ctx);
        }
    }

    //ParametersandqualifiersでMemberdeclarationが関数宣言？
    @Override public void enterParametersandqualifiers(CPP14Parser.ParametersandqualifiersContext ctx) {
        isParametersandqualifiers = true;
    }

    //kannsuunakami?
    @Override public void enterStatementseq(CPP14Parser.StatementseqContext ctx) { }

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
        } else if(msg.equals (results.get( results.size() -1 ))){ //msgと，esultsの最後の要素が一致してるなら，何もしない．

        } else {
            this.results.add(msg);
            setInfo(token.getLine(),token.getText());
        }
    }

    private void outPutWhereNeedToDocumentComments(Token token) {
        String msg = token.getLine() + "行目の " + token.getText()+"の前にコメントが必要です";

        //重複があるときは追加しないようにするための処理
        if(results.isEmpty()) {
            this.results.add(msg);
            setInfo(token.getLine(),token.getText());
        } else if(msg.equals (results.get( results.size() -1 ))){ //msgと，esultsの最後の要素が一致してるなら，何もしない．

        } else {
            this.results.add(msg);
            setInfo(token.getLine(),token.getText());
        }

    }

}