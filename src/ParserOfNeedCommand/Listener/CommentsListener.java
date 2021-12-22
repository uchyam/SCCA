package ParserOfNeedCommand.Listener;

import File.FileInputer;
import File.InfoForNecessaryComments;
import ParserOfNeedCommand.Generated.CPP14BaseListener;
import ParserOfNeedCommand.Generated.CPP14Parser;
import org.antlr.v4.runtime.*;
import java.util.ArrayList;
import java.util.List;

public class CommentsListener extends CPP14BaseListener {
    private CommonTokenStream tokens;
    private CPP14Parser parser;

    private ArrayList<String> results = new ArrayList<String>();
    public List<String> getResults(){ return this.results; }

    //TODO:rewrite
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

    public CommentsListener(CommonTokenStream tokens, CPP14Parser parser) {
        this.tokens = tokens;
        this.parser = parser;
        //TODO 設定ファイルをオプションで選択できるようにした方がいい？
        FileInputer fileInPuter = new FileInputer();
        fileInPuter.readSettingFileForNecessaryComments("Config/SettingForNecessaryComments.json");
        infoObj = fileInPuter.getInfoForNecessaryCommentsObj();
    }

    //    //自作された型のポインタ
//    @Override
//    public void enterExpressionstatement(CPP14Parser.ExpressionstatementContext ctx){
//        determineWhetherCommentIsNecessary(ctx);
//    }

    //関数定義の前
    @Override
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx){
        if (infoObj.isFunctionStatement()) {
            determineWhetherCommentIsNecessary(ctx);
        }
        System.out.println("kan:"+ctx.getText());
        isFunctiondefinition = true;
    }

    //hikiiduu?
    @Override public void enterParameterdeclarationlist(CPP14Parser.ParameterdeclarationlistContext ctx) {
        System.out.println("param_kata:"+ctx.getStart().getText()+",param_name:"+ctx.getStop().getText());
    }
    @Override public void exitParameterdeclarationlist(CPP14Parser.ParameterdeclarationlistContext ctx) {
    }

    @Override public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        isFunctiondefinition = false;
    }

    //クラス名，関数宣言，変数宣言の前．
    @Override
    public void enterMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        //TODO memberdeclartionで，simpletypespecifierになってるやつが変数名になる？
        System.out.println("hen:"+ctx.getText());
        isParametersandqualifiers = false;
    }
    @Override public void exitMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        if (infoObj.isFunctionStatement()) {
            if (isParametersandqualifiers) {
                determineWhetherCommentIsNecessary(ctx);
            }
        } else if (infoObj.isOthersStatement()){
            if (!isParametersandqualifiers) {
                determineWhetherCommentIsNecessary(ctx);
            }
        }
    }

    //ParametersandqualifiersでMemberdeclarationが関数宣言？
    @Override public void enterParametersandqualifiers(CPP14Parser.ParametersandqualifiersContext ctx) {
        isParametersandqualifiers = true;
    }

    //kannsuunakami?
    @Override public void enterStatementseq(CPP14Parser.StatementseqContext ctx) { }

    //変数宣言の前
    @Override
    public void enterSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
        //TODO memberdeclartionで，simpletypespecifierになってるやつが変数名になる？
        if (infoObj.isOthersStatement()) {
            determineWhetherCommentIsNecessary(ctx);
        }
    }

    //Loopの前
    @Override
    public void enterIterationstatement(CPP14Parser.IterationstatementContext ctx){
        //Iterationstatementの最も左側のTokenを取得
        if( infoObj.isIterationsStatement() ) {
            determineWhetherCommentIsNecessary(ctx);
        }
    }
    //if,switchの前
    @Override
    public void enterSelectionstatement(CPP14Parser.SelectionstatementContext ctx){
        if( infoObj.isSelectionsStatement() ) {
            determineWhetherCommentIsNecessary(ctx);
        }
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